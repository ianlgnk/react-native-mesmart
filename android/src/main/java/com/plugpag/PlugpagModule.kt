package com.plugpag

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.plugpag.utils.PlugpagUtils

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
      plugpag.activate(code)
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun destroy(promise: Promise) {
    try {
      plugpag.deactivate()
      promise.resolve(null)
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
      plugpag.abortPayment()
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun resolveEvent(data: ReadableMap, promise: Promise) {
    try {
      val plugPagEventData = PlugPagEventData(data.getInt("eventCode"))
      promise.resolve(PlugpagUtils.resolveEvent(plugPagEventData))
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
