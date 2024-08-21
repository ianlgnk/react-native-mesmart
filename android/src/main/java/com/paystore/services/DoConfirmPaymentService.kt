package com.paystore.services

import android.util.Log
import br.com.phoebus.android.payments.api.ErrorData
import br.com.phoebus.android.payments.api.PaymentClient
import br.com.phoebus.android.payments.api.PaymentClient.PaymentCallback
import br.com.phoebus.android.payments.api.PaymentStatus
import br.com.phoebus.android.payments.api.PaymentV2
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.paystore.utils.Constants
import com.paystore.utils.PaymentV2Mapper

class DoConfirmPaymentService(
  private val mContext: ReactContext,
  private val mPaymentClient: PaymentClient,
  var paymentV2: PaymentV2,
  private val promise: Promise
) : OnBindConnectedPaymentService {
  override fun execute() {
    try {
      mPaymentClient.confirmPayment(paymentV2.paymentId, object : PaymentCallback<Any?> {
        override fun onSuccess(o: Any?) {
          Log.e("sendEvent", "ConfirmPayment")
          paymentV2.paymentStatus = PaymentStatus.CONFIRMED
          mContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit("onPayment", PaymentV2Mapper.toWritableMap(paymentV2))
          unBind()
        }

        override fun onError(errorData: ErrorData) {
          promise.reject(Constants.ERROR, errorData?.responseMessage ?: "-")
          unBind()
        }
      })
    } catch (e: Exception) {
      Log.e("Payment fail", e.message!!)
      unBind()
    }
  }

  private fun unBind() {
    if (mPaymentClient.isBound) {
      mPaymentClient.unbind(mContext)
    }
  }
}
