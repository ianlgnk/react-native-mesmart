package com.plugpag.utils

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult

object TransactionObjectMapper {
  fun toWritableMap(result: PlugPagTransactionResult?): WritableMap {
    return Arguments.createMap().apply {
      putNullableString("message", result?.message)
      putNullableString("errorCode", result?.errorCode)
      putNullableInt("result", result?.result)
      putNullableString("transactionCode", result?.transactionCode)
      putNullableString("transactionId", result?.transactionId)
      putNullableString("nsu", result?.nsu)
      putNullableString("hostNsu", result?.hostNsu)
      putNullableString("autoCode", result?.autoCode)
      putNullableString("userReference", result?.userReference)
      putNullableString("appIdentification", result?.appIdentification)
      putNullableString("typeTransaction", result?.typeTransaction)
      putNullableString("date", result?.date)
      putNullableString("time", result?.time)
      putNullableString("cardBrand", result?.cardBrand)
      putNullableString("bin", result?.bin)
      putNullableString("holder", result?.holder)
      putNullableString("holderName", result?.holderName)
      putNullableString("extendedHolderName", result?.extendedHolderName)
      putNullableString("cardApplication", result?.cardApplication)
      putNullableString("cardIssuerNationality", result?.cardIssuerNationality?.name)
      putNullableString("label", result?.label)
      putNullableString("readerModel", result?.readerModel)
      putNullableString("terminalSerialNumber", result?.terminalSerialNumber)
      putNullableString("amount", result?.amount)
      putNullableInt("originalAmount", result?.originalAmount)
      putNullableString("availableBalance", result?.availableBalance)
      putNullableString("currentBalance", result?.currentBalance)
      putNullableString("accumulatedValue", result?.accumulatedValue)
      putNullableString("installments", result?.installments?.toString())
      putNullableInt("paymentType", result?.paymentType)
      putNullableString("buyerName", result?.buyerName)
      putNullableString("consumerIdentification", result?.consumerIdentification)
      putNullableString("consumerPhoneNumber", result?.consumerPhoneNumber)
      putNullableString("pixTxIdCode", result?.pixTxIdCode)
      putNullableString("preAutoDueDate", result?.preAutoDueDate)
      putNullableString("preAutoOriginalAmount", result?.preAutoOriginalAmount)
      putNullableString("partialPayPartiallyAuthorizedAmount", result?.partialPayPartiallyAuthorizedAmount)
      putNullableString("partialPayRemainingAmount", result?.partialPayRemainingAmount)
      putNullableString("cardHash", result?.cardHash)
      putNullableString("transactionReference", null)
      putNullableString("availableFieldsUnusedNote", null)
      putNullableString("clubePagScreensIds", result?.clubePagScreensIds)
      putNullableInt("userRegistered", result?.userRegistered)
    }
  }

  private fun WritableMap.putNullableString(key: String, value: String?) {
    if (value != null) putString(key, value) else putNull(key)
  }

  private fun WritableMap.putNullableInt(key: String, value: Int?) {
    if (value != null) putInt(key, value) else putNull(key)
  }
}
