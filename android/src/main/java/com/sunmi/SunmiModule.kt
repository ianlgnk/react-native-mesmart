package com.sunmi

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class SunmiModule(
  reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
  private var sunmi: SunmiImpl = SunmiImpl(reactContext)

  override fun getName(): String {
    return "Sunmi"
  }

  @ReactMethod
  fun scanRfid(promise: Promise) {
    try {
      sunmi.scanRfid(promise)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun cancelScan(promise: Promise) {
    try {
      sunmi.cancelScan()
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun printText(
    str: String,
    fontSize: Int,
    isBold: Boolean,
    isUnderline: Boolean,
    promise: Promise
  ) {
    try {
      sunmi.printText(str, fontSize, isBold, isUnderline)
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun printEmptyLine(promise: Promise) {
    try {
      sunmi.printEmptyLine()
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun printBitmap(
    bitmapStr: String,
    width: Int,
    height: Int,
    promise: Promise
  ) {
    try {
      sunmi.printBitmap(bitmapStr, width, height)
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun printBpLogo(promise: Promise) {
    try {
      sunmi.printBpLogo()
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun setPrinterAlign(align: String, promise: Promise) {
    try {
      sunmi.setPrinterAlign(align)
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }
}
