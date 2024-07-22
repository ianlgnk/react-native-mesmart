package com.plugpag

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.plugpag.utils.PlugpagUtils

class PlugpagModule(
  private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
  private var plugpag: PlugpagImpl? = null

  override fun getName(): String {
    return "Plugpag"
  }

  private fun checkPlugpag() {
    if (plugpag == null)
      throw Exception("Plugpag is not initiated! Did you use initPlugpag(code: string) method before calling this method?")
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun init(code: String) {
    if (plugpag != null)
      throw Exception("Plugpag has already been initiated with code: '" + plugpag!!.activationCode + "'!")

    plugpag = PlugpagImpl(reactContext, code)
  }

  @ReactMethod
  fun pay(
    rechargeValue: String,
    transactionType: String,
    promise: Promise
  ) {
    try {
      checkPlugpag()
      plugpag!!.pay(rechargeValue, transactionType, promise)
    } catch (e: Exception) {
     e.printStackTrace()
     promise.reject(e)
    }
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun abortPayment() {
    checkPlugpag()
    plugpag!!.abortTransaction()
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun printEstablishmentReceipt() {
    checkPlugpag()
    plugpag!!.printEstablishmentReceipt()
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun printClientReceipt() {
    checkPlugpag()
    plugpag!!.printClientReceipt()
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun customDialogClientViaPrinter(color: String) {
    checkPlugpag()
    plugpag!!.customDialogClientViaPrinter(color)
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun resolveTransactionEvent(data: ReadableMap): String {
    val plugPagEventData = PlugPagEventData(data.getInt("eventCode"))
    return PlugpagUtils.resolveTransactionEvent(plugPagEventData)
  }
}
