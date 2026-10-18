package com.tectoy

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class TectoyModule(
  reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
  private var tectoy: TectoyImpl = TectoyImpl(reactContext)

  override fun getName(): String {
    return "Tectoy"
  }

  @ReactMethod
  fun scanRfid(promise: Promise) {
    try {
      tectoy.scanRfid(promise)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun cancelScan(promise: Promise) {
    try {
      tectoy.cancelScan(promise)
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
      tectoy.printText(str, fontSize, isBold, isUnderline)
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun printEmptyLine(promise: Promise) {
    try {
      tectoy.printEmptyLine()
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
      tectoy.printBitmap(bitmapStr, width, height)
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun printBpLogo(promise: Promise) {
    try {
      tectoy.printBpLogo()
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun setPrinterAlign(align: String, promise: Promise) {
    try {
      tectoy.setPrinterAlign(align)
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun commitPrint(promise: Promise) {
    try {
      tectoy.commitPrint(promise)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }
}
