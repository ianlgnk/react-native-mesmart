package com.stone

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.stone.utils.TransactionObjectMapper
import stone.application.enums.ReceiptType

class StoneModule(
  reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
  private var stone: StoneImpl = StoneImpl(reactContext)

  override fun getName(): String {
    return "Stone"
  }

  @ReactMethod
  fun init(keys: ReadableMap, appName: String, promise: Promise) {
    try {
      val providerId = keys.getString("providerId").orEmpty()
      val authorization = keys.getString("authorization").orEmpty()

      if (providerId.isEmpty())
        throw Exception("providerId cannot be empty.")
      if (authorization.isEmpty())
        throw Exception("authorization cannot be empty.")
      if (appName.isEmpty())
        throw Exception("appName cannot be empty.")

      stone.activate(providerId, authorization, appName)
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun isActivated(promise: Promise) {
    try {
      promise.resolve(stone.isActivated)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun getStoneCode(promise: Promise) {
    try {
      promise.resolve(stone.stoneCode)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun pay(value: String, paymentMethod: String, isCapture: Boolean, promise: Promise) {
    try {
      stone.pay(value, paymentMethod, isCapture, promise)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun abortPayment(promise: Promise) {
    try {
      stone.abortPayment(promise)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun printVia(data: ReadableMap, type: String, promise: Promise) {
    try {
      stone.printVia(
        TransactionObjectMapper.fromReadableMap(data),
        ReceiptType.valueOf(type.uppercase()),
        promise
      )
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun captureTransaction(data: ReadableMap, promise: Promise) {
    try {
      stone.captureTransaction(
        TransactionObjectMapper.fromReadableMap(data),
        promise
      )
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun cancelTransaction(data: ReadableMap, promise: Promise) {
    try {
      stone.cancelTransaction(
        TransactionObjectMapper.fromReadableMap(data),
        promise
      )
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }
}
