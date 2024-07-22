package com.sunmi

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.sunmi.utils.SunmiUtils

class SunmiModule(
  reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
  private var sunmi: SunmiImpl? = null

  init {
    if (SunmiUtils.isSunmiP2()) {
      sunmi = SunmiImpl(reactContext)
    }
  }

  override fun getName(): String {
    return "Sunmi"
  }

  private fun checkSunmi() {
    if (sunmi == null)
      throw Exception("Sunmi is null! You may not working in a Sunmi P2 or an error ocurred during the initialization!")
  }

  @ReactMethod
  fun searchRfid(promise: Promise) {
    try {
      checkSunmi()
      this.sunmi!!.searchRfid(promise)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun cancelCardSearch() {
    checkSunmi()
    this.sunmi!!.cancelCardSearch()
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun printText(
    str: String,
    fontSize: Int,
    isBold: Boolean,
    isUnderline: Boolean
  ) {
    checkSunmi()
    this.sunmi!!.printText(str, fontSize, isBold, isUnderline)
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun printEmptyLine() {
    checkSunmi()
    sunmi!!.printEmptyLine()
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun printBpLogo() {
    checkSunmi()
    sunmi!!.printBpLogo()
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun setPrinterAlign(align: Int) {
    checkSunmi()
    sunmi!!.setPrinterAlign(align)
  }
}
