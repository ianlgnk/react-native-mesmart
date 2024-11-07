package com.pax

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class PaxModule(
  reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
  private var pax = PaxImpl(reactContext)

  override fun getName(): String {
    return "Pax"
  }

  @ReactMethod
  fun scanRfid(promise: Promise) {
    try {
      pax.scanRfid(promise)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun cancelScan(promise: Promise) {
    try {
      pax.cancelScan()
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun printerInit(promise: Promise) {
    try {
      pax.printerInit()
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun printerStart(promise: Promise) {
    try {
      pax.printerStart()
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun addText(
    str: String,
    fontSize: Int,
    promise: Promise
  ) {
    try {
      pax.addText(str, fontSize)
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun addBpLogo(promise: Promise) {
    try {
      pax.addBpLogo()
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }
}
