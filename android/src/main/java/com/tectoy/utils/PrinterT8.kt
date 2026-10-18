package com.tectoy.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import br.com.stone.posandroid.providers.PosPrintProvider
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.stone.utils.StoneUtils
import stone.application.interfaces.StoneCallbackInterface

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
    // A fonte da Sunmi tem caractere de meio corpo (24 -> 32 colunas em 384 pontos); a mono e mais larga.
    paint.textScaleX = fontSize / 2f / paint.measureText("0")

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
    return whiteBitmap(scaled.height).also { Canvas(it).drawBitmap(scaled, left, 0f, null) }
  }

  private fun whiteBitmap(height: Int): Bitmap =
    Bitmap.createBitmap(PRINT_WIDTH, height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
      .apply { eraseColor(Color.WHITE) }

  companion object {
    private const val PRINT_WIDTH = 384
    private const val DEFAULT_FONT_SIZE = 24
  }
}
