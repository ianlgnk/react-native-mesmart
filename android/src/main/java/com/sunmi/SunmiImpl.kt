package com.sunmi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.mesmart.R
import com.sunmi.utils.SunmiPrintHelper
import com.sunmi.utils.SunmiRfidHelper
import sunmi.paylib.SunmiPayKernel
import sunmi.paylib.SunmiPayKernel.ConnectCallback

class SunmiImpl(
  private val reactContext: ReactApplicationContext
) {
  private val payKernel = SunmiPayKernel.getInstance()
  private var sunmiRfidHelper: SunmiRfidHelper = SunmiRfidHelper(payKernel)
  private var sunmiPrintHelper: SunmiPrintHelper = SunmiPrintHelper(reactContext)

  init {
    payKernel.initPaySDK(reactContext, object : ConnectCallback {
      override fun onConnectPaySDK() {}
      override fun onDisconnectPaySDK() {}
    })
  }

  fun searchRfid(promise: Promise) {
    sunmiRfidHelper.searchRfid(promise)
  }

  fun cancelCardSearch() {
    sunmiRfidHelper.cancelCardSearch()
  }

  fun printText(
    str: String,
    fontSize: Int,
    isBold: Boolean,
    isUnderline: Boolean
  ) {
    sunmiPrintHelper.printText(
      str,
      fontSize.toFloat(),
      isBold,
      isUnderline,
      null)
  }

  fun printEmptyLine() {
    sunmiPrintHelper.printEmptyLine()
  }

  fun printBpLogo() {
    val options = BitmapFactory.Options().apply {
      inTargetDensity = 160
      inDensity = 160
    }
    val bitmap = BitmapFactory.decodeResource(reactContext.resources, R.drawable.logo_black, options)

    sunmiPrintHelper.printBitmap(
      Bitmap.createScaledBitmap(bitmap, 375, 100, true),
      1
    )
  }

  fun setPrinterAlign(align: Int) {
      sunmiPrintHelper.setPrinterAlign(align)
  }
}

