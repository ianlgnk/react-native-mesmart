package com.gertec.utils

import br.com.gertec.easylayer.printer.Printer
import br.com.gertec.easylayer.printer.PrinterError
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext

class PrinterGPOS780(
  private val reactContext: ReactApplicationContext
) {
  fun scrollPaper(
    lines: Int,
    promise: Promise
  ) {
    Printer.getInstance(reactContext.applicationContext, object: Printer.Listener {
      override fun onPrinterError(printerError: PrinterError?) {
        promise.reject("Cause: ${printerError?.cause}\\nError code: ${printerError?.code}\\nRequest ID: ${printerError?.requestId}\"")
      }
      override fun onPrinterSuccessful(var1: Int) {
        promise.resolve(true)
      }
    }).scrollPaper(lines)
  }

  fun printHtml(
    html: String,
    promise: Promise
  ) {
    Printer.getInstance(reactContext.applicationContext, object: Printer.Listener {
      override fun onPrinterError(printerError: PrinterError?) {
        promise.reject("Cause: ${printerError?.cause}\\nError code: ${printerError?.code}\\nRequest ID: ${printerError?.requestId}\"")
      }
      override fun onPrinterSuccessful(var1: Int) {
        promise.resolve(true)
      }
    }).printHtml(reactContext.applicationContext, html)
  }
}
