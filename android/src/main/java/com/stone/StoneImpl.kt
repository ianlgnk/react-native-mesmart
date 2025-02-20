package com.stone

import br.com.stone.posandroid.providers.PosPrintReceiptProvider
import br.com.stone.posandroid.providers.PosTransactionProvider
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.mesmart.Utils
import com.stone.utils.StoneUtils
import com.stone.utils.TransactionObjectMapper
import stone.application.StoneStart
import stone.application.enums.Action
import stone.application.enums.InstalmentTransactionEnum
import stone.application.enums.ReceiptType
import stone.application.interfaces.StoneActionCallback
import stone.application.interfaces.StoneCallbackInterface
import stone.database.transaction.TransactionObject
import stone.providers.BaseTransactionProvider
import stone.providers.CancellationProvider
import stone.providers.CaptureTransactionProvider
import stone.utils.Stone
import stone.utils.keys.StoneKeyType

class StoneImpl(
  private val reactContext: ReactApplicationContext
) {
  private var transaction: TransactionObject? = null
  private var paymentProvider: BaseTransactionProvider? = null
  var stoneCode = ""
  var isActivated = false

  private fun sendEvent(
    reactContext: ReactContext,
    params: WritableMap?
  ) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit("onStoneEventData", params)
  }

  private fun checkStone() {
    if (!isActivated)
      throw Exception("Stone não foi ativado.")
  }

  fun activate(providerId: String, authorization: String, appName: String) {
    if (isActivated)
      throw Exception("Stone já foi ativado.")

    val userList = StoneStart.init(reactContext, mapOf(
      StoneKeyType.QRCODE_PROVIDERID    to providerId,
      StoneKeyType.QRCODE_AUTHORIZATION to authorization,
    ))
    Stone.setAppName(appName)

    val userWithStoneCode = userList?.find { it.stoneCode.isNotEmpty() }
    if (userList != null && userWithStoneCode != null)
      stoneCode = userWithStoneCode.stoneCode

    isActivated = true
  }

  fun pay(value: String, paymentMethod: String, isCapture: Boolean, promise: Promise) {
    checkStone()

    transaction = TransactionObject()
    transaction!!.amount = value
    transaction!!.instalmentTransaction = InstalmentTransactionEnum.getAt(0)
    transaction!!.typeOfTransaction = StoneUtils.resolveTransactionType(paymentMethod)
    transaction!!.isCapture = isCapture

    paymentProvider = PosTransactionProvider(
      reactContext,
      transaction,
      Stone.getUserModel(0)
    )
    paymentProvider?.useDefaultUI(true)
    paymentProvider?.setConnectionCallback(object: StoneActionCallback {
      override fun onSuccess() {
        sendEvent(
          reactContext,
          Arguments.createMap().apply {
            putString("type", "onSuccess")
            putString("status", StoneUtils.resolveTransactionStatus(paymentProvider?.transactionStatus))
            putString("statusEnum", paymentProvider?.transactionStatus?.name.orEmpty())
            putString("action", StoneUtils.resolveActionCode(transaction?.actionCode))
            putString("actionCode", transaction?.actionCode.orEmpty())
            putString("messageFromAuthorize", paymentProvider?.messageFromAuthorize.orEmpty())
            putString("authorizationCode", paymentProvider?.authorizationCode.orEmpty())
            transaction?.let {
              putMap("transaction", TransactionObjectMapper.toWritableMap(it))
            }
          }
        )

        paymentProvider = null
        transaction = null
      }

      override fun onError() {
        sendEvent(
          reactContext,
          Arguments.createMap().apply {
            putString("type", "onError")
            putString("status", StoneUtils.resolveTransactionStatus(paymentProvider?.transactionStatus))
            putString("statusEnum", paymentProvider?.transactionStatus?.name.orEmpty())
            putString("action", StoneUtils.resolveActionCode(transaction?.actionCode))
            putString("actionCode", transaction?.actionCode.orEmpty())
            putString("messageFromAuthorize", paymentProvider?.messageFromAuthorize.orEmpty())
            transaction?.let {
              putMap("transaction", TransactionObjectMapper.toWritableMap(it))
            }
            putArray("listOfErrors",
              StoneUtils.convertErrorsEnumToReadableArray(
                paymentProvider?.listOfErrors,
                true
              )
            )
            putArray("listOfErrorsEnum",
              StoneUtils.convertErrorsEnumToReadableArray(
                paymentProvider?.listOfErrors,
                false)
            )
          }
        )

        paymentProvider = null
        transaction = null
      }

      override fun onStatusChanged(action: Action?) {
        sendEvent(
          reactContext,
          Arguments.createMap().apply {
            putString("type", "onStatusChanged")
            putString("action", StoneUtils.resolveTransactionAction(action))
            putString("actionEvent", action?.name.orEmpty())
            if (action == Action.TRANSACTION_WAITING_QRCODE_SCAN)
              putString("qrCode", Utils.bitmapToBase64(transaction?.qrCode))
          }
        )
      }
    })
    paymentProvider?.execute()
    promise.resolve(null)
  }

  fun abortPayment(promise: Promise) {
    checkStone()
    if (paymentProvider == null) return promise.resolve(false)
    paymentProvider?.abortPayment()
  }

  fun printVia(transaction: TransactionObject, type: ReceiptType, promise: Promise) {
    checkStone()

    val provider = PosPrintReceiptProvider(
      reactContext,
      transaction,
      type
    )
    provider.connectionCallback = object: StoneCallbackInterface {
      override fun onSuccess() {
        promise.resolve(null)
      }

      override fun onError() {
        promise.reject(
          "listOfErrors: ${
            StoneUtils.convertErrorsEnumToReadableArray(provider.listOfErrors, true)
          }\n" +
          "listOfErrorsEnums: ${provider.listOfErrors}"
        )
      }
    }
    provider.execute()
  }

  fun captureTransaction(transaction: TransactionObject, promise: Promise) {
    checkStone()

    val provider = CaptureTransactionProvider(reactContext, transaction)
    provider.connectionCallback = object: StoneCallbackInterface {
      override fun onSuccess() {
        promise.resolve(
          Arguments.createMap().apply {
            putString("responseCode", provider.responseCodeEnum.name)
            putString("messageFromAuthorize", provider.messageFromAuthorize.orEmpty())
            putMap("transaction",
              TransactionObjectMapper.toWritableMap(
                provider.transactionObject
              )
            )
          }
        )
      }

      override fun onError() {
        promise.reject(
          "Falha ao tentar capturar transação.\n" +
          "responseCode: ${
            try {
              provider.responseCodeEnum
            } catch (e: Exception) {
              null
            }
          }\n" +
          "messageFromAuthorize: ${provider.messageFromAuthorize}\n" +
          "listOfErrors: ${
            StoneUtils.convertErrorsEnumToReadableArray(provider.listOfErrors, true)
          }\n" +
          "listOfErrorsEnum: ${provider.listOfErrors}"
        )
      }
    }
    provider.execute()
  }

  fun cancelTransaction(transaction: TransactionObject, promise: Promise) {
    checkStone()

    val provider = CancellationProvider(reactContext, transaction)
    provider.connectionCallback = object: StoneCallbackInterface {
      override fun onSuccess() {
        promise.resolve(
          Arguments.createMap().apply {
            putString("action", StoneUtils.resolveActionCode(provider.Cancellation().actionCode))
            putString("actionCode", provider.Cancellation().actionCode.orEmpty())
            putString("responseCode", provider.responseCodeEnum.name)
            putString("messageFromAuthorize", provider.messageFromAuthorize.orEmpty())
            putMap(
              "transaction",
              TransactionObjectMapper.toWritableMap(
                transaction
              )
            )
          }
        )
      }

      override fun onError() {
        promise.reject(
          "Falha ao tentar cancelar transação.\n" +
          "action: ${StoneUtils.resolveActionCode(provider.Cancellation().actionCode)}\n" +
          "actionCode: ${provider.Cancellation().actionCode}\n" +
          "responseCode: ${
            try {
              provider.responseCodeEnum
            } catch (e: Exception) {
              null
            }
          }\n" +
          "messageFromAuthorize: ${provider.messageFromAuthorize}\n" +
          "listOfErrors: ${
            StoneUtils.convertErrorsEnumToReadableArray(provider.listOfErrors, true)
          }\n" +
          "listOfErrorsEnum: ${provider.listOfErrors}"
        )
      }
    }
    provider.execute()
  }
}
