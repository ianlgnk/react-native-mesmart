package com.tectoy.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import br.com.stone.posandroid.providers.PosPrintProvider
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.stone.utils.StoneUtils
import stone.application.interfaces.StoneCallbackInterface
import java.lang.reflect.Field
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy

/**
 * Mesmas primitivas da Sunmi, mas acumuladas: o HAL da Tectoy avanca o papel pra corte no fim de
 * cada job da Stone, entao o cupom inteiro vai num job so em commitPrint(). O texto e desenhado
 * aqui porque a T8 (firmware Stone) bloqueia o processo da WebView, entao HTML nao renderiza.
 */
class PrinterT8(
  private val reactContext: ReactApplicationContext
) {
  private val blocks = mutableListOf<Bitmap>()
  private var align = Layout.Alignment.ALIGN_NORMAL

  @Synchronized
  fun setPrinterAlign(value: String) {
    align = when (value) {
      "left" -> Layout.Alignment.ALIGN_NORMAL
      "center" -> Layout.Alignment.ALIGN_CENTER
      "right" -> Layout.Alignment.ALIGN_OPPOSITE
      else -> throw Exception("'$value' not recognized! Use only one of these: ['left', 'center', 'right']")
    }
  }

  @Synchronized
  fun printText(
    str: String,
    fontSize: Int,
    isBold: Boolean,
    isUnderline: Boolean
  ) {
    blocks += textBlock(str.removeSuffix("\n"), fontSize, isBold, isUnderline)
  }

  @Synchronized
  fun printEmptyLine() {
    blocks += textBlock("", DEFAULT_FONT_SIZE, false, false)
  }

  @Synchronized
  fun printBitmap(bitmap: Bitmap) {
    blocks += imageBlock(bitmap)
    // Igual a Sunmi, que manda "\n\n" depois da imagem.
    printEmptyLine()
    printEmptyLine()
  }

  @Synchronized
  fun commitPrint(promise: Promise) {
    val job = blocks.toList()
    blocks.clear()
    if (job.isEmpty()) {
      promise.resolve(true)
      return
    }

    guardLateEvents()
    val provider = PosPrintProvider(reactContext)
    job.forEach { provider.addBitmap(it) }
    provider.connectionCallback = object : StoneCallbackInterface {
      override fun onSuccess() {
        promise.resolve(true)
      }

      override fun onError() {
        promise.reject(
          "listOfErrors: ${
            StoneUtils.convertErrorsEnumToReadableArray(provider.listOfErrors, true)
          }\n" +
          "listOfErrorsEnum: ${provider.listOfErrors}"
        )
      }
    }
    provider.execute()
  }

  /**
   * O HAL da Tectoy (posandroid-manufacturer-tectoy 2.2.10, SDK Stone 4.20.x) solta o PosPrinter no
   * fim do job (onFinish -> close -> release) com eventos de status ainda na fila do main looper; o
   * proximo onInfo chama getPrintStateInfo() com mPosPrinter nulo e o NPE derruba o app. Abre aqui o
   * gerenciador que o HAL reaproveita no job e embrulha o listener que ele entrega ao PosPrinter no
   * beginPrint, pra descartar so os eventos que chegam depois do release. Tudo por reflexao: se o
   * HAL mudar, imprime como antes.
   */
  private fun guardLateEvents() {
    try {
      val services = Class.forName("com.transire.transireservice.TransireServiceManager")
        .getMethod("getInstance").invoke(null) ?: return
      val manager = services.javaClass.getMethod("getPOIPrinterService").invoke(services) ?: return
      val listenerField = field(manager.javaClass, "eventListener")
      val listener = listenerField.get(manager) ?: return
      if (Proxy.isProxyClass(listener.javaClass)) return

      val guard = InvocationHandler { _, method, args ->
        try {
          method.invoke(listener, *(args ?: emptyArray<Any?>()))
        } catch (e: InvocationTargetException) {
          if (e.cause !is NullPointerException || !isReleased(args?.firstOrNull())) throw e.cause ?: e
          Log.w(TAG, "Evento ${method.name} da impressora depois do release ignorado")
          null
        }
      }
      listenerField.set(
        manager,
        Proxy.newProxyInstance(listener.javaClass.classLoader, listener.javaClass.interfaces, guard)
      )
    } catch (t: Throwable) {
      Log.w(TAG, "Impressora sem protecao contra eventos tardios do HAL", t)
    }
  }

  private fun isReleased(printer: Any?): Boolean =
    printer != null && runCatching { field(printer.javaClass, "mPosPrinter").get(printer) == null }.getOrDefault(false)

  private fun field(type: Class<*>, name: String): Field =
    type.getDeclaredField(name).apply { isAccessible = true }

  private fun textBlock(
    text: String,
    fontSize: Int,
    isBold: Boolean,
    isUnderline: Boolean
  ): Bitmap {
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
      color = Color.BLACK
      textSize = fontSize.toFloat()
      typeface = Typeface.MONOSPACE
      isFakeBoldText = isBold
      isUnderlineText = isUnderline
    }
    // A fonte da Sunmi tem caractere de meio corpo (24 -> 32 colunas em 384 pontos). Calibra medindo o
    // layout real, porque o negrito sintetico alarga o avanco e o measureText nao pega; 1% de folga pra
    // linha de 32 colunas nao quebrar por arredondamento.
    val target = fontSize / 2f * CALIBRATION_CHARS * 0.99f
    repeat(2) { paint.textScaleX *= target / Layout.getDesiredWidth(CALIBRATION_SAMPLE, paint) }

    val layout = StaticLayout.Builder
      .obtain(text, 0, text.length, paint, PRINT_WIDTH)
      .setAlignment(align)
      .build()

    return whiteBitmap(layout.height).also { layout.draw(Canvas(it)) }
  }

  private fun imageBlock(image: Bitmap): Bitmap {
    val scaled =
      if (image.width <= PRINT_WIDTH) image
      else Bitmap.createScaledBitmap(image, PRINT_WIDTH, image.height * PRINT_WIDTH / image.width, true)
    val left = when (align) {
      Layout.Alignment.ALIGN_CENTER -> (PRINT_WIDTH - scaled.width) / 2f
      Layout.Alignment.ALIGN_OPPOSITE -> (PRINT_WIDTH - scaled.width).toFloat()
      else -> 0f
    }
    // Retangulo de destino explicito: drawBitmap(x, y) escalaria pela densidade (logo decodificado a 160dpi).
    val dst = RectF(left, 0f, left + scaled.width, scaled.height.toFloat())
    return whiteBitmap(scaled.height).also { Canvas(it).drawBitmap(scaled, null, dst, null) }
  }

  private fun whiteBitmap(height: Int): Bitmap =
    Bitmap.createBitmap(PRINT_WIDTH, height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
      .apply { eraseColor(Color.WHITE) }

  companion object {
    private const val TAG = "PrinterT8"
    private const val PRINT_WIDTH = 384
    private const val DEFAULT_FONT_SIZE = 24
    private const val CALIBRATION_CHARS = 32
    private val CALIBRATION_SAMPLE = "=".repeat(CALIBRATION_CHARS)
  }
}
