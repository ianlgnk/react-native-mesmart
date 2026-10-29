package com.gertec.utils

import android.util.Log
import br.com.gertec.easylayer.printer.Printer
import br.com.gertec.easylayer.printer.PrinterError
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.mesmart.Utils

class PrinterGPOS780(
  private val reactContext: ReactApplicationContext
) {
  fun scrollPaper(
    lines: Int,
    promise: Promise
  ) {
    try {
      Printer.getInstance(reactContext.applicationContext, listener(promise, "scrollPaper"))
        .scrollPaper(lines)
    } catch (t: Throwable) {
      reject(promise, "scrollPaper", t)
    }
  }

  fun printHtml(
    html: String,
    promise: Promise
  ) {
    try {
      Printer.getInstance(reactContext.applicationContext, listener(promise, "printHtml"))
        .printHtml(reactContext.applicationContext, html)
    } catch (t: Throwable) {
      reject(promise, "printHtml", t)
    }
  }

  private fun listener(promise: Promise, operation: String) = object : Printer.Listener {
    override fun onPrinterError(printerError: PrinterError?) {
      val message = "Printer $operation failed. Cause: ${printerError?.cause}, " +
        "error code: ${printerError?.code}, request ID: ${printerError?.requestId} " +
        "(${Utils.deviceContext()})"
      Log.e(TAG, message)
      promise.reject("GERTEC_PRINTER_ERROR", message)
    }

    override fun onPrinterSuccessful(var1: Int) {
      promise.resolve(true)
    }
  }

  // Throwable (e nao so Exception): falha da lib nativa (UnsatisfiedLinkError, NoClassDefFoundError) e Error.
  private fun reject(promise: Promise, operation: String, t: Throwable) {
    val message = "Printer $operation threw ${t.javaClass.name}: ${t.message} (${Utils.deviceContext()})"
    Log.e(TAG, message, t)
    promise.reject("GERTEC_PRINTER_ERROR", message, t)
  }

  private companion object {
    const val TAG = "MeSmartGertec"
  }
}
