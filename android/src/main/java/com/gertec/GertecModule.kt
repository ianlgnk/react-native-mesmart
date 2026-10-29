package com.gertec

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class GertecModule(
  reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
  private var gertec: GertecImpl = GertecImpl(reactContext)

  override fun getName(): String {
    return "Gertec"
  }

  @ReactMethod
  fun scanRfid(promise: Promise) {
    try {
      gertec.scanRfid(promise)
    } catch (e: Throwable) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun cancelScan(promise: Promise) {
    try {
      gertec.cancelScan(promise)
    } catch (e: Throwable) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun scrollPrinterPaper(
    lines: Int,
    promise: Promise
  ) {
    try {
      gertec.scrollPrinterPaper(lines, promise)
    } catch (e: Throwable) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun printHtml(
    html: String,
    promise: Promise
  ) {
    try {
      gertec.printHtml(html, promise)
    } catch (e: Throwable) {
      e.printStackTrace()
      promise.reject(e)
    }
  }
}
