package com.paystore.services

import android.util.Log
import br.com.phoebus.android.payments.api.PaymentClient
import br.com.phoebus.android.payments.api.PaymentType
import br.com.phoebus.android.payments.api.PaymentV2
import br.com.phoebus.android.payments.api.client.Client.OnConnectionCallback
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactContext

class PaymentService(
  private val context: ReactContext,
  private val promise: Promise
) {
  private val paymentClient = PaymentClient()
  private var onBindConnectedPaymentService: OnBindConnectedPaymentService? = null

  private fun doBind() {
    paymentClient.bind(context.applicationContext, object : OnConnectionCallback {
      override fun onConnected() {
        Log.i("OnConnected", "Conectado")
        onBindConnectedPaymentService?.execute()
      }

      override fun onDisconnected(b: Boolean) {
        Log.i("onDisconnected", "Desconectado")
      }
    })
  }

  fun doPayment(
    value: String?,
    transactionId: String?,
    showReceiptView: Boolean,
    paymentType: List<PaymentType>?,
    installments: Int?,
    confirmPayment: Boolean
  ) {
      val doPayment: OnBindConnectedPaymentService = DoPaymentService(
        context,
        paymentClient,
        value!!,
        transactionId!!,
        showReceiptView,
        paymentType!!,
        installments!!,
        confirmPayment,
        promise
      )
      setOnBindConnectedPaymentService(doPayment)
      doBind()
  }

  fun confirmPayment(paymentV2: PaymentV2?) {
    val doConfirmPaymentService: OnBindConnectedPaymentService = DoConfirmPaymentService(
      context,
      paymentClient,
      paymentV2!!,
      promise
    )
    setOnBindConnectedPaymentService(doConfirmPaymentService)
    doBind()
  }

  private fun setOnBindConnectedPaymentService(onBindConnectedPaymentService: OnBindConnectedPaymentService?) {
    this.onBindConnectedPaymentService = onBindConnectedPaymentService
  }
}
