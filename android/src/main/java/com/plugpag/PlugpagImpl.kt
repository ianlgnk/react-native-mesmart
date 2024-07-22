package com.plugpag

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagActivationData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagCustomPrinterLayout
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventListener
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.plugpag.utils.PlugpagUtils

class PlugpagImpl(
  private val reactContext: ReactApplicationContext,
  val activationCode: String
) {
  private val plugPag = PlugPag(reactContext)

  init {
    setEventListener()
  }

  private fun setEventListener() {
    plugPag.setEventListener(object : PlugPagEventListener {
      override fun onEvent(data: PlugPagEventData) {
        sendEvent(
          reactContext,
          Arguments.createMap().apply {
            putInt("eventCode", data.eventCode)
            putString("customMessage", data.customMessage)
          }
        )
      }
    })
  }

  private fun sendEvent(
    reactContext: ReactContext,
    params: WritableMap?
  ) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit("onPlugPagEventData", params)
  }

  fun pay(
    rechargeValue: String,
    transactionType: String,
    promise: Promise
  ) {
    Thread {
      run {
        val paymentData = PlugpagUtils.resolveTransactionType(transactionType)?.let {
          PlugPagPaymentData(
            it,
            rechargeValue.toInt(),
            PlugPag.INSTALLMENT_TYPE_A_VISTA,
            1,
            null
          )
        }

        val terminalResult = plugPag.initializeAndActivatePinpad(
          PlugPagActivationData(activationCode)
        )

        if (terminalResult.result == PlugPag.RET_OK) {
          val paymentResult = paymentData?.let { plugPag.doPayment(it) }
          promise.resolve(
            Arguments.createMap().apply {
              putString("message", paymentResult?.message)
              putString("errorCode", paymentResult?.errorCode)
              paymentResult?.result?.let { putInt("result", it) }
            }
          )
        } else {
          throw Exception("Error on initialize and activate pinpad. Maybe your activationCode is not valid!")
        }
      }
    }.start()
  }

  fun abortTransaction() {
    Thread {
      run {
        plugPag.abort()
      }
    }.start()
  }

  fun printEstablishmentReceipt() {
    plugPag.reprintStablishmentReceipt()
  }

  fun printClientReceipt() {
    plugPag.reprintCustomerReceipt()
  }

  fun customDialogClientViaPrinter(color: String) {
    Thread {
      run {
        val customPrinter = PlugPagCustomPrinterLayout()
        customPrinter.buttonBackgroundColor = color
        customPrinter.maxTimeShowPopup = 20
        plugPag.setPlugPagCustomPrinterLayout(customPrinter)
      }
    }.start()
  }
}
