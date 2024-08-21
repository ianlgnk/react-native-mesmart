package com.paystore

import android.util.Log
import br.com.phoebus.android.payments.api.PaymentType
import br.com.phoebus.android.payments.api.provider.PaymentProviderApi
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.mesmart.utils.Utils
import com.paystore.services.PaymentService
import java.util.LinkedList

class PaystoreModule(
  reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {
  init {
    if (Utils.isA910()) {
      PaymentProviderApi.create(reactApplicationContext)
    }
  }

  override fun getName(): String {
    return "Paystore"
  }

  @ReactMethod
  fun pay(
    value: String?,
    transactionId: String?,
    showReceiptView: Boolean,
    paymentTypeArrayString: ReadableArray,
    installments: Int?,
    promise: Promise
  ) {
    val paymentTypes: MutableList<PaymentType> = LinkedList()
    for (i in 0 until paymentTypeArrayString.size()) {
      val paymentTypeString = paymentTypeArrayString.getString(i)
      if (PaymentType.valueOf(paymentTypeString) != null) {
        paymentTypes.add(PaymentType.valueOf(paymentTypeString))
      }
    }

    try {
      val paymentService = PaymentService(reactApplicationContext, promise)
      paymentService.doPayment(
        value,
        transactionId,
        showReceiptView,
        paymentTypes,
        installments,
        true
      )
    } catch (e: Exception) {
      Log.e("errorPayment", e.toString())
      promise.reject("ERROR", e.message)
    }
  }
}
