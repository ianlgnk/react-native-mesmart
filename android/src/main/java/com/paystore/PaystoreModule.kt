package com.paystore

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray

class PaystoreModule(
  reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
  val paystore = PaystoreImpl(reactContext)

  override fun getName(): String {
    return "Paystore"
  }

  @ReactMethod
  fun init(promise: Promise) {
    try {
      paystore.activate()
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun pay(
    value: String?,
    transactionId: String?,
    showReceiptView: Boolean,
    paymentTypeArrayString: ReadableArray,
    installments: Int?,
    promise: Promise
  ) {
    try {
      paystore.pay(value, transactionId, showReceiptView, paymentTypeArrayString, installments, promise)
      promise.resolve(null)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod
  fun isActivated(promise: Promise) {
    try {
      promise.resolve(paystore.isActivated)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }
}
