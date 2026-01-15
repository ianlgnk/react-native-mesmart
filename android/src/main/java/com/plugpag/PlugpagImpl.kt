package com.plugpag

import android.content.pm.PackageManager
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagActivationData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagCustomPrinterLayout
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventListener
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagVoidData
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.plugpag.utils.PlugpagUtils

class PlugpagImpl(
  private val reactContext: ReactApplicationContext
) {
  private val SERVICE_PKG = "br.com.uol.pagseguro.plugpagservice"
  private val plugPag = PlugPag(reactContext)
  private var activationCode = ""
  var isActivated = false

  init {
    plugPag.setEventListener(object : PlugPagEventListener {
      override fun onEvent(data: PlugPagEventData) {
        reactContext
          .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
          .emit("onPlugpagEventData", Arguments.createMap().apply {
            putInt("eventCode", data.eventCode)
            putString("customMessage", data.customMessage)
          })
      }
    })

    plugPag.setPlugPagCustomPrinterLayout(PlugPagCustomPrinterLayout().apply {
      buttonBackgroundColor = "#F8BB12"
      maxTimeShowPopup = 1
    })
  }

  private fun isServiceAvailable(): Boolean {
    return try {
      reactContext.packageManager.getPackageInfo(SERVICE_PKG, 0)
      true
    } catch (_: PackageManager.NameNotFoundException) {
      false
    }
  }

  private fun checkPlugpag() {
    if (!isServiceAvailable()) throw Exception("Plugpag SDK is not available in this device.")
    if (!isActivated)
      throw Exception("Plugpag SDK has not been activated.")
  }

  fun activate(code: String, promise: Promise) {
   Thread {
     try {
        if (!isServiceAvailable()) throw Exception("Plugpag SDK is not available in this device.")
        if (isActivated)
          throw Exception("Plugpag SDK has already been activated with the code: '${activationCode}'.")

        val terminalResult = plugPag.initializeAndActivatePinpad(
          PlugPagActivationData(code)
        )

        if (terminalResult.result != PlugPag.RET_OK)
          throw Exception(terminalResult.errorMessage)

        activationCode = code
        isActivated = true

        promise.resolve(null)
     } catch (e: Exception) {
       e.printStackTrace()
       promise.reject(e)
     }
   }.start()
  }

  fun deactivate(promise: Promise) {
    Thread {
      try {
        checkPlugpag()

        val terminalResult = plugPag.deactivate(
          PlugPagActivationData(activationCode)
        )

        if (terminalResult.result != PlugPag.RET_OK)
          throw Exception(terminalResult.errorMessage)

        activationCode = ""
        isActivated = false

        promise.resolve(null)
      } catch (e: Exception) {
        e.printStackTrace()
        promise.reject(e)
      }
    }.start()
  }

  fun pay(
    value: String,
    paymentMethod: String,
    promise: Promise
  ) {
    Thread {
      try {
        checkPlugpag()

        val paymentData = PlugpagUtils.resolvePaymentMethod(paymentMethod)?.let {
          PlugPagPaymentData(
            it,
            value.toInt(),
            PlugPag.INSTALLMENT_TYPE_A_VISTA,
            1,
            null
          )
        }

        val paymentResult = paymentData?.let { plugPag.doPayment(it) }
        promise.resolve(com.plugpag.utils.TransactionObjectMapper.toWritableMap(paymentResult))
      } catch (e: Exception) {
        e.printStackTrace()
        promise.reject(e)
      }
    }.start()
  }

  fun abortPayment(promise: Promise) {
    Thread {
      try {
        checkPlugpag()
        plugPag.abort()
        promise.resolve(null)
      } catch (e: Exception) {
        e.printStackTrace()
        promise.reject(e)
      }
    }.start()
  }

  fun voidPayment(
    transactionCode: String,
    transactionId: String,
    printReceipt: Boolean,
    voidType: String?,
    promise: Promise
  ) {
    Thread {
      try {
        checkPlugpag()

        if (transactionCode.isBlank())
          throw Exception("transactionCode cannot be empty.")
        if (transactionId.isBlank())
          throw Exception("transactionId cannot be empty.")

        val voidData = PlugPagVoidData(
          transactionCode,
          transactionId,
          printReceipt,
          PlugpagUtils.resolveVoidType(voidType)
        )

        val voidResult = plugPag.voidPayment(voidData)
        promise.resolve(com.plugpag.utils.TransactionObjectMapper.toWritableMap(voidResult))
      } catch (e: Exception) {
        e.printStackTrace()
        promise.reject(e)
      }
    }.start()
  }
}
