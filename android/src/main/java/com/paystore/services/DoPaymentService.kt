package com.paystore.services

import android.util.Log
import br.com.phoebus.android.payments.api.ErrorData
import br.com.phoebus.android.payments.api.PaymentClient
import br.com.phoebus.android.payments.api.PaymentClient.PaymentCallback
import br.com.phoebus.android.payments.api.PaymentRequestV2
import br.com.phoebus.android.payments.api.PaymentType
import br.com.phoebus.android.payments.api.PaymentV2
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.paystore.utils.Constants
import com.paystore.utils.CredentialsUtils
import com.paystore.utils.DataTypeUtils
import com.paystore.utils.PaymentV2Mapper

class DoPaymentService(
  private val mContext: ReactContext,
  private val mPaymentClient: PaymentClient,
  private val value: String,
  private val transactionId: String,
  private val showReceiptView: Boolean,
  private val payments: List<PaymentType>,
  private val installments: Int,
  private val confirmPayment: Boolean,
  private val promise: Promise
) : OnBindConnectedPaymentService {
  override fun execute() {
    Log.e("Pagamentos", "------------")
    Log.e("Valor", value)
    Log.e("transactionId", transactionId)

    val paymentRequest = PaymentRequestV2()

    try {
      paymentRequest.withValue(DataTypeUtils.getValueFromString(value))
        .withAppTransactionId(transactionId)
        .withApplicationInfo(CredentialsUtils.myAppInfo)
        .withShowReceiptView(showReceiptView)
        .withPaymentTypes(payments)
      paymentRequest.tokenizeCard = false
      paymentRequest.printMerchantReceipt = true
      paymentRequest.printCustomerReceipt = true
      if (this.installments != 0) {
        paymentRequest.installments = installments
      }
    } catch (e: Exception) {
      Log.e("Error", e.message!!)
      promise.reject(
        "ERRORS" + "REQUEST",
        "ERROR NAME  :" + e.message + ", ERROR_CODE : " + e.toString()
      )
    }

    try {
      mPaymentClient.startPaymentV2(paymentRequest, object : PaymentCallback<PaymentV2?> {
        override fun onSuccess(paymentV2: PaymentV2?) {
          if (confirmPayment) {
            val paymentService = PaymentService(mContext, promise)
            paymentService.confirmPayment(paymentV2)
          } else {
            mContext
              .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
              .emit("onPayment", paymentV2?.let { PaymentV2Mapper.toWritableMap(it) })
          }
          unBind()
        }

        override fun onError(errorData: ErrorData) {
          Log.e(
            "Error",
            if (errorData != null) errorData.paymentsResponseCode + " - " + errorData.responseMessage else "-"
          )
          promise.reject(
            Constants.ERROR,
            if (errorData != null) errorData.paymentsResponseCode + " - " + errorData.responseMessage else ""
          )
          unBind()
        }
      })
    } catch (e: Exception) {
      promise.reject("ERROR", e.message)
      unBind()
    }
  }

  private fun unBind() {
    if (mPaymentClient.isBound) {
      mPaymentClient.unbind(mContext)
    }
  }
}
