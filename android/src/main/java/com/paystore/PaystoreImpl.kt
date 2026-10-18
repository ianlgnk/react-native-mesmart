package com.paystore

import br.com.phoebus.android.payments.api.PaymentType
import br.com.phoebus.android.payments.api.provider.PaymentProviderApi
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.paystore.services.PaymentService
import java.util.LinkedList

class PaystoreImpl(
  private val reactContext: ReactApplicationContext
) {
  var isActivated = false

  fun activate() {
    if (isActivated)
      throw Exception("Paystore já foi ativado!")

    PaymentProviderApi.create(reactContext)
    isActivated = true
  }

  fun pay(
    value: String?,
    transactionId: String?,
    showReceiptView: Boolean,
    paymentTypeArrayString: ReadableArray,
    installments: Int?,
    promise: Promise
  ) {
    if (!isActivated)
      throw Exception("Paystore não foi ativado!")

    val paymentTypes: MutableList<PaymentType> = LinkedList()
    for (i in 0 until paymentTypeArrayString.size()) {
      val paymentTypeString = paymentTypeArrayString.getString(i) ?: continue
      if (PaymentType.valueOf(paymentTypeString) != null) {
        paymentTypes.add(PaymentType.valueOf(paymentTypeString))
      }
    }

    val paymentService = PaymentService(reactContext, promise)
    paymentService.doPayment(
      value,
      transactionId,
      showReceiptView,
      paymentTypes,
      installments,
      true
    )
  }
}
