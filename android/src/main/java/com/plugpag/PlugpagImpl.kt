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
  private val reactContext: ReactApplicationContext
) {
  private val plugPag = PlugPag(reactContext)
  private var activationCode = ""
  var isActivated = false

  init {
    setEventListener()
    customDialogClientViaPrinter()
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
      .emit("onPlugpagEventData", params)
  }

  private fun customDialogClientViaPrinter() {
    val customPrinter = PlugPagCustomPrinterLayout()
    customPrinter.buttonBackgroundColor = "#F8BB12"
    customPrinter.maxTimeShowPopup = 1
    plugPag.setPlugPagCustomPrinterLayout(customPrinter)
  }

  private fun checkPlugpag() {
    if (!isActivated)
      throw Exception("Plugpag não foi ativado.")
  }

  fun activate(code: String) {
    if (isActivated)
      throw Exception("Plugpag já foi ativado com o código: '${activationCode}'!")

    val terminalResult = plugPag.initializeAndActivatePinpad(
      PlugPagActivationData(code)
    )

    if (terminalResult.result != PlugPag.RET_OK)
      throw Exception(terminalResult.errorMessage)

    activationCode = code
    isActivated = true
  }

  fun deactivate() {
    if (!isActivated)
      throw Exception("Plugpag não foi ativado!")

    val terminalResult = plugPag.deactivate(
      PlugPagActivationData(activationCode)
    )

    if (terminalResult.result != PlugPag.RET_OK)
      throw Exception(terminalResult.errorMessage)

    activationCode = ""
    isActivated = false
  }

  fun pay(
    value: String,
    paymentMethod: String,
    promise: Promise
  ) {
    Thread {
      run {
        try {
          this.checkPlugpag()

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
      }
    }.start()
  }

  fun abortPayment() {
    checkPlugpag()
    plugPag.abort()
  }
}
