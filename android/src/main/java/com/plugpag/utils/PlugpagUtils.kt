package com.plugpag.utils

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData.Companion.EVENT_CODE_INSERTED_CARD
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData.Companion.EVENT_CODE_PIN_OK
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData.Companion.EVENT_CODE_REMOVED_CARD
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData.Companion.EVENT_CODE_SALE_APPROVED
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData.Companion.EVENT_CODE_SALE_NOT_APPROVED
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData.Companion.EVENT_CODE_WAITING_CARD
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData.Companion.EVENT_CODE_WAITING_REMOVE_CARD

class PlugpagUtils {
  companion object {
    fun resolveTransactionType(type: String): Int? {
      return when(type) {
        "debitCard" -> PlugPag.TYPE_DEBITO
        "creditCard" -> PlugPag.TYPE_CREDITO
        "voucher" -> PlugPag.TYPE_VOUCHER
        "pix" -> PlugPag.TYPE_PIX
        else -> null
      }
    }
    fun resolveTransactionEvent(data : PlugPagEventData): String {
      return when(data.eventCode) {
        EVENT_CODE_WAITING_CARD -> "Aproxime ou insira o cartão de pagamento"
        EVENT_CODE_SALE_APPROVED -> "Processando..."
        EVENT_CODE_SALE_NOT_APPROVED -> "Processando..."
        EVENT_CODE_WAITING_REMOVE_CARD -> "Por favor, remova o seu cartão"
        EVENT_CODE_INSERTED_CARD -> "Processando..."
        EVENT_CODE_PIN_OK -> "Processando..."
        EVENT_CODE_REMOVED_CARD -> "Processando..."
        18 -> "Transação aprovada"
        17 -> "Informe a senha do seu cartão"
        4 -> "Processando..."
        else -> ""
      }
    }
  }
}
