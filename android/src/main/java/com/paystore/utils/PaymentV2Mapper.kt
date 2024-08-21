package com.paystore.utils

import br.com.phoebus.android.payments.api.PaymentV2
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap

object PaymentV2Mapper {
  fun toWritableMap(paymentV2: PaymentV2): WritableMap {
    val map = Arguments.createMap()

    map.putString("cardToken", paymentV2.cardToken)
    map.putString("acquirerAdditionalMessage", paymentV2.acquirerAdditionalMessage)
    map.putString("note", paymentV2.note)
    map.putString("dni", paymentV2.dni)
    map.putString("qrId", paymentV2.qrId)
    map.putString("accountTypeId", paymentV2.accountTypeId)
    map.putString("planId", paymentV2.planId)
    map.putString("productShortName", paymentV2.productShortName)
    map.putString("batchNumber", paymentV2.batchNumber)
    map.putString("nsuTerminal", paymentV2.nsuTerminal)
    map.putBoolean("lastTrx", paymentV2.isLastTrx)
    map.putString("ticketNumber", paymentV2.ticketNumber)
    map.putString("cardHolderName", paymentV2.cardHolderName)
    map.putString("terminalId", paymentV2.terminalId)
    map.putString("appTransactionId", paymentV2.appTransactionId)
    map.putString("acquirerNsu", paymentV2.acquirerNsu)
    map.putString(
      "originalValue",
      if (paymentV2.originalValue != null) paymentV2.originalValue.toString() else null
    )
    map.putString(
      "additionalValue",
      if (paymentV2.additionalValue != null) paymentV2.additionalValue.toString() else null
    )
    map.putString(
      "additionalValueType",
      if (paymentV2.additionalValueType != null) paymentV2.additionalValueType.name else null
    )

    return map
  }
}

