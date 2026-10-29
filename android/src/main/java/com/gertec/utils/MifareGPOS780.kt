package com.gertec.utils

import android.util.Log
import br.com.gertec.easylayer.contactless.MifareClassic
import com.facebook.react.bridge.Promise
import com.mesmart.Utils

class MifareGPOS780 {
  @Volatile private var canceled = false
  @Volatile private var scanning = false
  @Volatile private var worker: Thread? = null

  private var mifare: MifareClassic = MifareClassic()

  fun scanRfid(promise: Promise) {
    if (scanning) {
      promise.reject("Service is busy, try again later.")
      return
    }
    scanning = true
    canceled = false

    worker = Thread {
      try {
        val totalTimeoutMs = 30000
        val sliceMs = 500
        val deadline = System.currentTimeMillis() + totalTimeoutMs
        var uid: ByteArray? = null

        while (!canceled && System.currentTimeMillis() < deadline) {
          try {
            uid = mifare.connect(sliceMs)
            break
          } catch (e: Exception) {
            val msg = e.message?.lowercase().orEmpty()
            val isTimeoutLike = msg.contains("timeout") || msg.contains("polling")
            if (!isTimeoutLike) {
              Log.e(TAG, "RFID connect failed: ${e.javaClass.name}: ${e.message}")
              throw e
            }
          } finally {
            try { mifare.close() } catch (_: Exception) {}
          }
        }

        when {
          canceled   -> {}
          uid != null -> promise.resolve(
            Utils.resolveRFID(Utils.byteArrayToHexString(uid))
          )
          else       -> promise.reject("Timeout to read RFID card!")
        }
      } catch (t: Throwable) {
        // Throwable: falha da lib nativa e Error e, sem isso, a thread morre e a promise nunca resolve.
        val message = "RFID scan threw ${t.javaClass.name}: ${t.message} (${Utils.deviceContext()})"
        Log.e(TAG, message, t)
        promise.reject("GERTEC_RFID_ERROR", message, t)
      } finally {
        try { mifare.close() } catch (_: Exception) {}
        mifare = MifareClassic()
        scanning = false
        worker = null
      }
    }.also { it.start() }
  }

  fun cancelScan(promise: Promise) {
    try {
      canceled = true
      try { mifare.close() } catch (_: Exception) {}
      worker?.interrupt()
      scanning = false
      promise.resolve(true)
    } catch (e: Exception) {
      e.printStackTrace()
      promise.reject(e)
    }
  }

  private companion object {
    const val TAG = "MeSmartGertec"
  }
}
