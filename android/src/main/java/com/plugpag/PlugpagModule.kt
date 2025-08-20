package com.plugpag

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class PlugpagModule(
  reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
  private var plugpag: PlugpagImpl = PlugpagImpl(reactContext)

  override fun getName(): String {
    return "Plugpag"
  }

  @ReactMethod
  fun init(code: String, promise: Promise) {
    try {
      plugpag.activate(code, promise)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun destroy(promise: Promise) {
    try {
      plugpag.deactivate(promise)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun pay(
    value: String,
    paymentMethod: String,
    promise: Promise
  ) {
    try {
      plugpag.pay(value, paymentMethod, promise)
    } catch (e: Exception) {
     e.printStackTrace()
     promise.reject(e)
    }
  }

  @ReactMethod
  fun abortPayment(promise: Promise) {
    try {
      plugpag.abortPayment(promise)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun isActivated(promise: Promise) {
    try {
      promise.resolve(plugpag.isActivated)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }
}
