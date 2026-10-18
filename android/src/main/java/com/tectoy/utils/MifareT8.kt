package com.tectoy.utils

import android.os.Handler
import android.os.Looper
import br.com.stone.posandroid.providers.PosMifareProvider
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.mesmart.Utils
import com.stone.utils.StoneUtils
import stone.application.interfaces.StoneCallbackInterface

class MifareT8(
  private val reactContext: ReactApplicationContext
) {
  private val mainHandler = Handler(Looper.getMainLooper())

  @Volatile private var provider: PosMifareProvider? = null
  @Volatile private var timedOut = false

  // O HAL da Tectoy encerra o polling apos 60s sem chamar callback nenhum, o que deixaria o
  // PosMifareProvider (e a promise) pendurados; cancelar antes forca o onError.
  private val timeout = Runnable {
    timedOut = true
    provider?.cancelDetection()
  }

  fun scanRfid(promise: Promise) {
    if (provider != null) {
      promise.reject("Service is busy, try again later.")
      return
    }

    val mifare = PosMifareProvider(reactContext)
    provider = mifare
    timedOut = false
    mifare.connectionCallback = object : StoneCallbackInterface {
      override fun onSuccess() {
        mainHandler.removeCallbacks(timeout)
        provider = null
        try {
          promise.resolve(
            Utils.resolveRFID(Utils.byteArrayToHexString(mifare.cardUUID))
          )
        } catch (e: Exception) {
          e.printStackTrace()
          promise.reject(e)
        } finally {
          try { mifare.powerOff() } catch (_: Exception) {}
        }
      }

      override fun onError() {
        mainHandler.removeCallbacks(timeout)
        provider = null
        if (timedOut) {
          promise.reject("Timeout to read RFID card!")
          return
        }
        promise.reject(
          "listOfErrors: ${
            StoneUtils.convertErrorsEnumToReadableArray(mifare.listOfErrors, true)
          }\n" +
          "listOfErrorsEnum: ${mifare.listOfErrors}"
        )
      }
    }
    mainHandler.postDelayed(timeout, TIMEOUT_MS)
    mifare.execute()
  }

  fun cancelScan(promise: Promise) {
    try {
      provider?.cancelDetection()
      promise.resolve(true)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  companion object {
    private const val TIMEOUT_MS = 30000L
  }
}
