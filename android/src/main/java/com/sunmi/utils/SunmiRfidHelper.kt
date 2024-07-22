package com.sunmi.utils

import android.os.Bundle
import com.facebook.react.bridge.Promise
import com.sunmi.pay.hardware.aidl.AidlConstants
import com.sunmi.pay.hardware.aidlv2.readcard.CheckCardCallbackV2
import sunmi.paylib.SunmiPayKernel

class SunmiRfidHelper(private val payKernel: SunmiPayKernel) {
  private var attempt: Int = 0

  fun searchRfid(promise: Promise) {
    try {
      cancelCardSearch()
      payKernel.mReadCardOptV2.checkCard(
        AidlConstants.CardType.MIFARE.value,
        object : CheckCardCallbackV2.Stub() {
          override fun findMagCard(bundle: Bundle) {
            handleOnFail("findMagCard", promise)
          }

          override fun findICCard(s: String) {
            handleOnFail("findICCard", promise)
          }

          override fun findRFCard(s: String) {
            promise.resolve(SunmiUtils.resolveRFIDBySerialNumber(s))
            cancelCardSearch()
          }

          override fun onError(i: Int, s: String) {
            handleOnFail("$i - $s", promise)
          }

          override fun findICCardEx(bundle: Bundle) {
            handleOnFail("findICCardEx", promise)
          }

          override fun findRFCardEx(bundle: Bundle) {
            handleOnFail("findRFCardEx", promise)
          }

          override fun onErrorEx(bundle: Bundle) {
            handleOnFail("onErrorEx", promise)
          }
        },
        0
      )
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
      cancelCardSearch()
    }
  }

  fun cancelCardSearch() {
    payKernel.mReadCardOptV2.cancelCheckCard()
  }

  private fun handleOnFail(errorMessage: String, promise: Promise) {
    attempt++
    if (attempt == 5) {
      promise.reject("500", errorMessage)
      attempt = 0
    }
    else searchRfid(promise)
  }
}
