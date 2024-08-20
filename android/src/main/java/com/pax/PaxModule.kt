package com.pax

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.mesmart.utils.Utils
import com.pax.utils.PaxConverter

class PaxModule(
  reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
  var pax: PaxImpl? = null

  init {
    if (Utils.isA910()) {
      pax = PaxImpl(reactContext)
      PaxConverter.instance?.setContext(reactContext)
    }
  }

  override fun getName(): String {
    return "Pax"
  }

  private fun checkPax() {
    if (pax == null)
      throw Exception("Pax is null! You may not working in a Pax A910 or an error ocurred during the initialization!")
  }

  @ReactMethod
  fun searchRfid(promise: Promise) {
    try {
      checkPax()
      pax?.searchRfid(promise)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun cancelCardSearch() {
    checkPax()
    pax?.cancelCardSearch()
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun printText(
    str: String,
    fontSize: Int
  ) {
    checkPax()
    pax?.printText(str, fontSize)
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun printBpLogo() {
    checkPax()
    pax?.printBpLogo()
  }
}
