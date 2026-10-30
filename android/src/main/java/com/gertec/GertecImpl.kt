package com.gertec

import android.util.Log
import br.com.gertec.gedi.GEDI
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import com.gertec.backend.GertecBackend
import com.gertec.backend.GertecCallback
import com.gertec.utils.MifareGPOS780
import com.gertec.utils.PrinterGPOS780
import com.mesmart.Utils

class GertecImpl(
  private val reactContext: ReactApplicationContext
) {
  private val isSupported = Utils.isGPOS780()

  // GPOS780S usa o SDK Gertec Neo (libgedi 2.x), carregado a parte; os demais GPOS780 seguem na libgedi 1.16.
  private val useNeo = Utils.isGPOS780S()

  @Volatile private var mifare: MifareGPOS780? = null
  @Volatile private var printer: PrinterGPOS780? = null
  private var neo: GertecBackend? = null
  private var neoError: Throwable? = null

  // Falha no init da GEDI (ex.: libgedi.so incompativel com o Android do aparelho) nao pode derrubar o modulo:
  // fica guardada e volta como erro legivel em cada chamada.
  private var initError: Throwable? = null

  init {
    if (isSupported && !useNeo) {
      try {
        GEDI.init(reactContext.applicationContext)
      } catch (t: Throwable) {
        initError = t
        Log.e(TAG, "GEDI.init failed: ${t.javaClass.name}: ${t.message} (${Utils.deviceContext()})", t)
      }
    }
  }

  private fun checkSupported() {
    if (!isSupported) throw Exception("Unsupported device (Supported models GPOS780, GPOS780S). ${Utils.deviceContext()}")
    initError?.let {
      throw Exception("GEDI init failed: ${it.javaClass.name}: ${it.message} (${Utils.deviceContext()})", it)
    }
  }

  // GEDI.init() sobe a instancia numa thread e engole falhas; a lib de RFID le GEDI.getInstance() sem argumento
  // e leva NPE se ainda for null. getInstance(context) cria de forma sincrona e deixa a causa real subir,
  // com timeout p/ nao travar a fila de modulos nativos caso o init fique pendurado (visto na GPOS780S).
  private fun ensureGedi() {
    if (GEDI.getInstance() != null) return
    val executor = Executors.newSingleThreadExecutor()
    try {
      executor.submit<Any?> { GEDI.getInstance(reactContext.applicationContext) }.get(GEDI_INIT_TIMEOUT_S, TimeUnit.SECONDS)
    } catch (e: TimeoutException) {
      throw Exception("GEDI init timeout (${GEDI_INIT_TIMEOUT_S}s) (${Utils.deviceContext()})", e)
    } catch (e: ExecutionException) {
      val cause = e.cause ?: e
      throw Exception("GEDI init failed: ${cause.javaClass.name}: ${cause.message} (${Utils.deviceContext()})", cause)
    } finally {
      executor.shutdownNow()
    }
    if (GEDI.getInstance() == null) {
      throw Exception("GEDI instance is null after init (${Utils.deviceContext()})")
    }
  }

  @Synchronized
  private fun ensureMifare(): MifareGPOS780 {
    checkSupported()
    ensureGedi()
    if (mifare == null) mifare = MifareGPOS780()
    return mifare!!
  }

  @Synchronized
  private fun ensurePrinter(): PrinterGPOS780 {
    checkSupported()
    if (printer == null) printer = PrinterGPOS780(reactContext)
    return printer!!
  }

  // Carga preguicosa: so a GPOS780S paga o custo de extrair o dex/.so da Neo, e a falha vira erro legivel por chamada.
  @Synchronized
  private fun ensureNeo(): GertecBackend {
    neo?.let { return it }
    neoError?.let { throw neoLoadException(it) }
    return try {
      NeoSdkLoader.load(reactContext.applicationContext).also { neo = it }
    } catch (t: Throwable) {
      neoError = t
      Log.e(TAG, "Gertec Neo SDK load failed: ${t.javaClass.name}: ${t.message} (${Utils.deviceContext()})", t)
      throw neoLoadException(t)
    }
  }

  private fun neoLoadException(t: Throwable) =
    Exception("Gertec Neo SDK load failed: ${t.javaClass.name}: ${t.message} (${Utils.deviceContext()})", t)

  private fun neoCallback(promise: Promise, transform: (Any?) -> Any? = { it }) = object : GertecCallback {
    override fun resolve(value: Any?) {
      try {
        promise.resolve(transform(value))
      } catch (t: Throwable) {
        promise.reject("GERTEC_RFID_ERROR", t.message, t)
      }
    }

    override fun reject(code: String, message: String?, cause: Throwable?) {
      if (cause != null) promise.reject(code, message, cause) else promise.reject(code, message)
    }
  }

  fun scanRfid(promise: Promise) {
    try {
      if (useNeo) {
        ensureNeo().scanRfid(neoCallback(promise) { Utils.resolveRFID(Utils.byteArrayToHexString(it as ByteArray)) })
        return
      }
      ensureMifare().scanRfid(promise)
    } catch (t: Throwable) {
      promise.reject("GERTEC_RFID_ERROR", t.message, t)
    }
  }

  fun cancelScan(promise: Promise) {
    try {
      if (useNeo) {
        ensureNeo().cancelScan(neoCallback(promise))
        return
      }
      ensureMifare().cancelScan(promise)
    } catch (t: Throwable) {
      promise.reject("GERTEC_RFID_ERROR", t.message, t)
    }
  }

  fun scrollPrinterPaper(
    lines: Int,
    promise: Promise
  ) {
    try {
      if (useNeo) {
        ensureNeo().scrollPrinterPaper(lines, neoCallback(promise))
        return
      }
      ensurePrinter().scrollPaper(lines, promise)
    } catch (t: Throwable) {
      promise.reject("GERTEC_PRINTER_ERROR", t.message, t)
    }
  }

  fun printHtml(
    html: String,
    promise: Promise
  ) {
    try {
      if (useNeo) {
        ensureNeo().printHtml(html, neoCallback(promise))
        return
      }
      ensurePrinter().printHtml(html, promise)
    } catch (t: Throwable) {
      promise.reject("GERTEC_PRINTER_ERROR", t.message, t)
    }
  }

  private companion object {
    const val TAG = "MeSmartGertec"
    const val GEDI_INIT_TIMEOUT_S = 5L
  }
}
