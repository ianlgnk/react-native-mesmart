package com.stone.utils

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.mesmart.Utils
import stone.application.enums.CardBrandEnum
import stone.application.enums.EntryMode
import stone.application.enums.InstalmentTransactionEnum
import stone.application.enums.TransactionStatusEnum
import stone.application.enums.TypeOfTransactionEnum
import stone.application.xml.enums.InstalmentTypeEnum
import stone.database.transaction.TransactionObject
import stone.user.Address
import stone.user.UserModel
import stone.user.device.UserPhone

object TransactionObjectMapper {
  fun fromReadableMap(map: ReadableMap): TransactionObject {
    return TransactionObject().apply {
      idFromBase = map.getInt("idFromBase")
      amount = map.getString("amount")
      requestId = map.getString("requestId")
      emailSent = map.getString("emailSent")
      timeToPassTransaction = map.getString("timeToPassTransaction")
      initiatorTransactionKey = map.getString("initiatorTransactionKey")
      acquirerTransactionKey = map.getString("acquirerTransactionKey")
      cardHolderNumber = map.getString("cardHolderNumber")
      cardHolderName = map.getString("cardHolderName")
      date = map.getString("date")
      time = map.getString("time")
      aid = map.getString("aid")
      arcq = map.getString("arcq")
      authorizationCode = map.getString("authorizationCode")
      iccRelatedData = map.getString("iccRelatedData")
      transactionReference = map.getString("transactionReference")
      actionCode = map.getString("actionCode")
      commandActionCode = map.getString("commandActionCode")
      pinpadUsed = map.getString("pinpadUsed")
      saleAffiliationKey = map.getString("saleAffiliationKey")
      cne = map.getString("cne")
      cvm = map.getString("cvm")
      balance = map.getString("balance")
      serviceCode = map.getString("serviceCode")
      subMerchantCategoryCode = map.getString("subMerchantCategoryCode")
      entryMode = map.getString("entryMode")?.let { EntryMode.valueOf(it) }
      cardBrand = map.getString("cardBrand")?.let { CardBrandEnum.valueOf(it) }
      cardBrandName = map.getString("cardBrandName")

      if (map.hasKey("cardBrandId") && !map.isNull("cardBrandId"))
        cardBrandId = map.getInt("cardBrandId")

      instalmentTransaction = InstalmentTransactionEnum.ONE_INSTALMENT
      transactionStatus = map.getString("transactionStatus")?.let { TransactionStatusEnum.valueOf(it) }
      instalmentType = map.getString("instalmentType")?.let { InstalmentTypeEnum.valueOf(it) }
      typeOfTransactionEnum = map.getString("typeOfTransaction")?.let { TypeOfTransactionEnum.valueOf(it) }
      signature = map.getString("signatureBitmap")?.let { Utils.convertStringToBitmap(it) }
      cancellationDate = map.getString("cancellationDate")?.let { Utils.convertStringToDate(it) }
      isCapture =
        if (map.hasKey("capture") && !map.isNull("capture"))
          map.getBoolean("capture")
        else null
      shortName = map.getString("shortName")
      subMerchantAddress = map.getString("subMerchantAddress")
      userModel = map.getMap("userModel")?.let { mapUserModel(it) }
      cvv = map.getString("cvv")
      isFallbackTransaction = map.getBoolean("isFallbackTransaction").takeIf { !map.isNull("isFallbackTransaction") } ?: false
      subMerchantCity = map.getString("subMerchantCity")
      subMerchantTaxIdentificationNumber = map.getString("subMerchantTaxIdentificationNumber")
      subMerchantRegisteredIdentifier = map.getString("subMerchantRegisteredIdentifier")
      subMerchantPostalAddress = map.getString("subMerchantPostalAddress")
      appLabel = map.getString("appLabel")
      cardExpireDate = map.getString("cardExpireDate")
      cardSequenceNumber = map.getString("cardSequenceNumber")
      externalId = map.getString("externalId")
      qrCode = map.getString("qrcodeBitmap")?.let { Utils.convertStringToBitmap(it) }
    }
  }

  private fun mapUserModel(map: ReadableMap?): UserModel {
    return UserModel().apply {
      merchantName = map?.getString("merchantName")
      saleAffiliationKey = map?.getString("saleAffiliationKey")
      affiliationKey = map?.getString("affiliationKey")
      merchantPassword = map?.getString("merchantPassword")
      merchantDocumentNumber = map?.getString("merchantDocumentNumber")
      stoneCode = map?.getString("stoneCode")
      currencyCode = map?.getString("currencyCode")
      merchantAddress = mapAddress(map?.getMap("merchantAddress"))
      setMerchantUserPhone(mapUserPhone(map?.getMap("merchantUserPhone")))
      tableVersion = map?.getString("tableVersion")
    }
  }

  private fun mapAddress(map: ReadableMap?): Address {
    return Address().apply {
      distric = map?.getString("distric")
      street = map?.getString("street")
      neighborhood = map?.getString("neighborhood")
      doorNumber = map?.getString("doorNumber")
      city = map?.getString("city")
    }
  }

  private fun mapUserPhone(map: ReadableMap?): UserPhone {
    return UserPhone().apply {
      merchantAndroidVersion = map?.getString("merchantAndroidVersion")
      merchantPhoneModel = map?.getString("merchantPhoneModel")
      merchantPhoneFactory = map?.getString("merchantPhoneFactory")
    }
  }

  fun toWritableMap(transactionObject: TransactionObject): WritableMap {
    return Arguments.createMap().apply {
      putInt("idFromBase", transactionObject.idFromBase)
      putString("amount", transactionObject.amount)
      putString("requestId", transactionObject.requestId)
      putString("emailSent", transactionObject.emailSent)
      putString("timeToPassTransaction", transactionObject.timeToPassTransaction)
      putString("initiatorTransactionKey", transactionObject.initiatorTransactionKey)
      putString("acquirerTransactionKey", transactionObject.acquirerTransactionKey)
      putString("cardHolderNumber", transactionObject.cardHolderNumber)
      putString("cardHolderName", transactionObject.cardHolderName)
      putString("date", transactionObject.date)
      putString("time", transactionObject.time)
      putString("aid", transactionObject.aid)
      putString("arcq", transactionObject.arcq)
      putString("authorizationCode", transactionObject.authorizationCode)
      putString("iccRelatedData", transactionObject.iccRelatedData)
      putString("transactionReference", transactionObject.transactionReference)
      putString("actionCode", transactionObject.actionCode)
      putString("commandActionCode", transactionObject.commandActionCode)
      putString("pinpadUsed", transactionObject.pinpadUsed)
      putString("saleAffiliationKey", transactionObject.saleAffiliationKey)
      putString("cne", transactionObject.cne)
      putString("cvm", transactionObject.cvm)
      putString("balance", transactionObject.balance)
      putString("serviceCode", transactionObject.serviceCode)
      putString("subMerchantCategoryCode", transactionObject.subMerchantCategoryCode)
      putString("entryMode", transactionObject.entryMode?.name ?: "UNKNOWN")
      putString("cardBrand", transactionObject.cardBrand?.name ?: "UNKNOWN")
      putString("cardBrandName", transactionObject.cardBrandName)
      putInt("cardBrandId", transactionObject.cardBrandId)
      putString("instalmentTransaction", transactionObject.instalmentTransaction.name)
      putString("transactionStatus", transactionObject.transactionStatus?.name ?: "UNKNOWN")
      putString("instalmentType", transactionObject.instalmentType?.name ?: "None")
      putString("typeOfTransaction", transactionObject.typeOfTransactionEnum?.name ?: "UNKNOWN")
      putString("signatureBitmap", Utils.bitmapToBase64(transactionObject.signature))
      putString("cancellationDate", Utils.dateToString(transactionObject.cancellationDate))
      putBoolean("capture", transactionObject.isCapture)
      putString("shortName", transactionObject.shortName)
      putString("subMerchantAddress", transactionObject.subMerchantAddress)
      putMap("userModel", userModelToWritableMap(transactionObject.userModel))
      putString("cvv", transactionObject.cvv)
      putBoolean("isFallbackTransaction", transactionObject.isFallbackTransaction)
      putString("subMerchantCity", transactionObject.subMerchantCity)
      putString("subMerchantTaxIdentificationNumber", transactionObject.subMerchantTaxIdentificationNumber)
      putString("subMerchantRegisteredIdentifier", transactionObject.subMerchantRegisteredIdentifier)
      putString("subMerchantPostalAddress", transactionObject.subMerchantPostalAddress)
      putString("appLabel", transactionObject.appLabel)
      putString("cardExpireDate", transactionObject.cardExpireDate)
      putString("cardSequenceNumber", transactionObject.cardSequenceNumber)
      putString("externalId", transactionObject.externalId)
      putString("qrcodeBitmap", Utils.bitmapToBase64(transactionObject.qrCode))
    }
  }

  private fun userModelToWritableMap(userModel: UserModel?): WritableMap {
    return Arguments.createMap().apply {
      putString("merchantName", userModel?.merchantName)
      putString("saleAffiliationKey", userModel?.saleAffiliationKey)
      putString("affiliationKey", userModel?.affiliationKey)
      putString("merchantPassword", userModel?.merchantPassword)
      putString("merchantDocumentNumber", userModel?.merchantDocumentNumber)
      putString("stoneCode", userModel?.stoneCode)
      putString("currencyCode", userModel?.currencyCode)
      putMap("merchantAddress", addressToWritableMap(userModel?.merchantAddress))
      putMap("merchantUserPhone", userPhoneToWritableMap(userModel?.userPhone))
      putString("tableVersion", userModel?.tableVersion)
    }
  }

  private fun addressToWritableMap(address: Address?): WritableMap {
    return Arguments.createMap().apply {
      putString("distric", address?.distric)
      putString("street", address?.street)
      putString("neighborhood", address?.neighborhood)
      putString("doorNumber", address?.doorNumber)
      putString("city", address?.city)
    }
  }

  private fun userPhoneToWritableMap(userPhone: UserPhone?): WritableMap {
    return Arguments.createMap().apply {
      putString("merchantAndroidVersion", userPhone?.merchantAndroidVersion)
      putString("merchantPhoneModel", userPhone?.merchantPhoneModel)
      putString("merchantPhoneFactory", userPhone?.merchantPhoneFactory)
    }
  }
}
