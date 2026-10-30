package com.gertec.neo;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import br.com.gertec.easylayer.contactless.MifareClassic;
import br.com.gertec.easylayer.printer.Printer;
import br.com.gertec.easylayer.printer.PrinterError;
import br.com.gertec.gedi.GEDI;
import com.gertec.backend.GertecBackend;
import com.gertec.backend.GertecCallback;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Implementacao do GertecBackend sobre o SDK Gertec Neo (libgedi 2.x + easy layer Neo), usada na GPOS780S.
 * Compilada contra a Neo e empacotada junto dela em assets/gertec-neo/neo-bridge.jar (ver gertec-neo/build.sh),
 * carregada por um classloader proprio, entao nao colide com a libgedi 1.16 que o app usa nos demais POS.
 */
public class NeoGertecBackend implements GertecBackend {
  private static final String TAG = "MeSmartGertecNeo";
  private static final String RFID_ERROR = "GERTEC_RFID_ERROR";
  private static final String PRINTER_ERROR = "GERTEC_PRINTER_ERROR";
  private static final long GEDI_INIT_TIMEOUT_S = 5L;

  private final Context context;
  private volatile boolean canceled = false;
  private volatile boolean scanning = false;
  private volatile Thread worker;
  private volatile MifareClassic current;
  private Throwable initError;

  public NeoGertecBackend(Context context) {
    this.context = context.getApplicationContext();
    try {
      GEDI.init(this.context);
    } catch (Throwable t) {
      initError = t;
      Log.e(TAG, "GEDI.init failed: " + t.getClass().getName() + ": " + t.getMessage(), t);
    }
  }

  // GEDI.init() sobe a instancia numa thread e engole falhas; a lib de RFID le GEDI.getInstance() sem argumento
  // e leva NPE se ainda for null. getInstance(context) cria de forma sincrona e deixa a causa real subir,
  // com timeout p/ nao travar a fila de modulos nativos caso o init fique pendurado.
  private void ensureGedi() throws Exception {
    if (initError != null) {
      throw new Exception(
          "GEDI init failed: " + initError.getClass().getName() + ": " + initError.getMessage() + " (" + deviceContext() + ")",
          initError);
    }
    if (GEDI.getInstance() != null) return;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      executor
          .submit(
              new Callable<Object>() {
                @Override
                public Object call() {
                  return GEDI.getInstance(context);
                }
              })
          .get(GEDI_INIT_TIMEOUT_S, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      throw new Exception("GEDI init timeout (" + GEDI_INIT_TIMEOUT_S + "s) (" + deviceContext() + ")", e);
    } catch (ExecutionException e) {
      Throwable cause = e.getCause() != null ? e.getCause() : e;
      throw new Exception(
          "GEDI init failed: " + cause.getClass().getName() + ": " + cause.getMessage() + " (" + deviceContext() + ")",
          cause);
    } finally {
      executor.shutdownNow();
    }
    if (GEDI.getInstance() == null) {
      throw new Exception("GEDI instance is null after init (" + deviceContext() + ")");
    }
  }

  @Override
  public void scanRfid(final GertecCallback callback) {
    synchronized (this) {
      if (scanning) {
        callback.reject(RFID_ERROR, "Service is busy, try again later.", null);
        return;
      }
      scanning = true;
      canceled = false;
    }

    worker =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                MifareClassic card = null;
                try {
                  ensureGedi();
                  card = new MifareClassic();
                  current = card;

                  long deadline = System.currentTimeMillis() + 30000;
                  byte[] uid = null;

                  while (!canceled && System.currentTimeMillis() < deadline) {
                    try {
                      uid = card.connect(500);
                      break;
                    } catch (Exception e) {
                      String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
                      boolean timeoutLike = msg.contains("timeout") || msg.contains("polling");
                      if (!timeoutLike) {
                        Log.e(TAG, "RFID connect failed: " + e.getClass().getName() + ": " + e.getMessage());
                        throw e;
                      }
                    } finally {
                      try {
                        card.close();
                      } catch (Exception ignored) {
                      }
                    }
                  }

                  if (canceled) {
                    return;
                  }
                  if (uid != null) {
                    callback.resolve(uid);
                  } else {
                    callback.reject(RFID_ERROR, "Timeout to read RFID card!", null);
                  }
                } catch (Throwable t) {
                  String message =
                      "RFID scan threw " + t.getClass().getName() + ": " + t.getMessage() + " (" + deviceContext() + ")";
                  Log.e(TAG, message, t);
                  callback.reject(RFID_ERROR, message, t);
                } finally {
                  try {
                    if (card != null) card.close();
                  } catch (Exception ignored) {
                  }
                  current = null;
                  scanning = false;
                  worker = null;
                }
              }
            });
    worker.start();
  }

  @Override
  public void cancelScan(GertecCallback callback) {
    try {
      canceled = true;
      MifareClassic card = current;
      if (card != null) {
        try {
          card.close();
        } catch (Exception ignored) {
        }
      }
      Thread thread = worker;
      if (thread != null) thread.interrupt();
      scanning = false;
      callback.resolve(Boolean.TRUE);
    } catch (Throwable t) {
      callback.reject(RFID_ERROR, "RFID cancel threw " + t.getClass().getName() + ": " + t.getMessage(), t);
    }
  }

  @Override
  public void scrollPrinterPaper(int lines, GertecCallback callback) {
    try {
      ensureGedi();
      Printer.getInstance(context, listener(callback, "scrollPaper")).scrollPaper(lines);
    } catch (Throwable t) {
      rejectPrinter(callback, "scrollPaper", t);
    }
  }

  @Override
  public void printHtml(String html, GertecCallback callback) {
    try {
      ensureGedi();
      Printer.getInstance(context, listener(callback, "printHtml")).printHtml(context, html);
    } catch (Throwable t) {
      rejectPrinter(callback, "printHtml", t);
    }
  }

  private Printer.Listener listener(final GertecCallback callback, final String operation) {
    return new Printer.Listener() {
      @Override
      public void onPrinterError(PrinterError printerError) {
        String message =
            "Printer "
                + operation
                + " failed. Cause: "
                + (printerError == null ? null : printerError.getCause())
                + ", error code: "
                + (printerError == null ? null : printerError.getCode())
                + ", request ID: "
                + (printerError == null ? null : printerError.getRequestId())
                + " ("
                + deviceContext()
                + ")";
        Log.e(TAG, message);
        callback.reject(PRINTER_ERROR, message, null);
      }

      @Override
      public void onPrinterSuccessful(int requestId) {
        callback.resolve(Boolean.TRUE);
      }
    };
  }

  private void rejectPrinter(GertecCallback callback, String operation, Throwable t) {
    String message =
        "Printer " + operation + " threw " + t.getClass().getName() + ": " + t.getMessage() + " (" + deviceContext() + ")";
    Log.e(TAG, message, t);
    callback.reject(PRINTER_ERROR, message, t);
  }

  private static String deviceContext() {
    return "model=" + Build.MODEL + ", android=" + Build.VERSION.RELEASE + " (sdk " + Build.VERSION.SDK_INT + "), sdk=neo";
  }
}
