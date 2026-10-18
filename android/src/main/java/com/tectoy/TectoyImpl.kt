package com.tectoy

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.mesmart.R
import com.mesmart.Utils
import com.tectoy.utils.MifareT8
import com.tectoy.utils.PrinterT8
import stone.application.StoneStart

class TectoyImpl(
  private val reactContext: ReactApplicationContext
) {
  private val isSupported = Utils.isT8()

  @Volatile private var mifare: MifareT8? = null
  @Volatile private var printer: PrinterT8? = null

  /** Os providers da Stone precisam do SDK iniciado (nao ativado); o modulo Stone pode ja ter iniciado. */
  @Synchronized
  private fun ensureStoneSdk() {
    if (!isSupported) throw Exception("Unsupported device (Supported model Tectoy T8).")
    if (!StoneStart.SDKInitialized) StoneStart.init(reactContext, emptyMap())
  }

  @Synchronized
  private fun ensureMifare(): MifareT8 {
    ensureStoneSdk()
    if (mifare == null) mifare = MifareT8(reactContext)
    return mifare!!
  }

  @Synchronized
  private fun ensurePrinter(): PrinterT8 {
    ensureStoneSdk()
    if (printer == null) printer = PrinterT8(reactContext)
    return printer!!
  }

  fun scanRfid(promise: Promise) {
    try {
      ensureMifare().scanRfid(promise)
    } catch (t: Throwable) {
      promise.reject(t)
    }
  }

  fun cancelScan(promise: Promise) {
    try {
      ensureMifare().cancelScan(promise)
    } catch (t: Throwable) {
      promise.reject(t)
    }
  }

  fun printText(
    str: String,
    fontSize: Int,
    isBold: Boolean,
    isUnderline: Boolean
  ) {
    ensurePrinter().printText(str, fontSize, isBold, isUnderline)
  }

  fun printEmptyLine() {
    ensurePrinter().printEmptyLine()
  }

  fun printBitmap(bitmapStr: String, width: Int, height: Int) {
    val bitmap = Utils.convertStringToBitmap(bitmapStr)
      ?: throw Exception("Error decoding bitmap. Check if the uploaded bitmap is base64.")
    ensurePrinter().printBitmap(Bitmap.createScaledBitmap(bitmap, width, height, true))
  }

  fun printBpLogo() {
    val options = BitmapFactory.Options().apply {
      inTargetDensity = 160
      inDensity = 160
    }
    val bitmap = BitmapFactory.decodeResource(reactContext.resources, R.drawable.logo_black, options)
    ensurePrinter().printBitmap(Bitmap.createScaledBitmap(bitmap, 375, 100, true))
  }

  fun setPrinterAlign(align: String) {
    ensurePrinter().setPrinterAlign(align)
  }

  fun commitPrint(promise: Promise) {
    ensurePrinter().commitPrint(promise)
  }
}
