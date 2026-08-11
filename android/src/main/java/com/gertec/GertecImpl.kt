package com.gertec

import br.com.gertec.gedi.GEDI
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.gertec.utils.MifareGPOS780
import com.gertec.utils.PrinterGPOS780
import com.mesmart.Utils

class GertecImpl(
  private val reactContext: ReactApplicationContext
) {
  private val isSupported = Utils.isGPOS780()

  @Volatile private var mifare: MifareGPOS780? = null
  @Volatile private var printer: PrinterGPOS780? = null

  init {
    if (isSupported) {
      GEDI.init(reactContext.applicationContext)
    }
  }

  @Synchronized
  private fun ensureMifare(): MifareGPOS780 {
    if (!isSupported) throw Exception("Unsupported device (Supported models GPOS780, GPOS780S).")
    if (mifare == null) mifare = MifareGPOS780()
    return mifare!!
  }

  @Synchronized
  private fun ensurePrinter(): PrinterGPOS780 {
    if (!isSupported) throw Exception("Unsupported device (Supported models GPOS780, GPOS780S).")
    if (printer == null) printer = PrinterGPOS780(reactContext)
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

  fun scrollPrinterPaper(
    lines: Int,
    promise: Promise
  ) {
    ensurePrinter().scrollPaper(lines, promise)
  }

  fun printHtml(
    html: String,
    promise: Promise
  ) {
    ensurePrinter().printHtml(html, promise)
  }
}
