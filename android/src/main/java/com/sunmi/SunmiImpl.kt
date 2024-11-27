package com.sunmi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.mesmart.R
import com.mesmart.Utils
import com.sunmi.helpers.SunmiPrintHelper
import com.sunmi.helpers.SunmiRfidHelper
import sunmi.paylib.SunmiPayKernel
import sunmi.paylib.SunmiPayKernel.ConnectCallback
import java.io.ByteArrayInputStream

class SunmiImpl(
  private val reactContext: ReactApplicationContext
) {
  private var payKernel: SunmiPayKernel? = null
  private var sunmiRfidHelper: SunmiRfidHelper? = null
  private var sunmiPrintHelper: SunmiPrintHelper = SunmiPrintHelper(reactContext)

  init {
    if (Utils.isSunmiP2()) {
      payKernel = SunmiPayKernel.getInstance()

      payKernel?.initPaySDK(reactContext, object : ConnectCallback {
        override fun onConnectPaySDK() {}
        override fun onDisconnectPaySDK() {}
      })

      sunmiRfidHelper = payKernel?.let { SunmiRfidHelper(it) }
    }
  }

  private fun checkKernel() {
    if (payKernel == null)
      throw Exception("Kernel da SunmiP2 não inicializado! Você deve utilizar esse método em um dispositivo SunmiP2 ou ocorreu algum erro durante a inicialização!")
  }

  fun scanRfid(promise: Promise) {
    checkKernel()
    sunmiRfidHelper?.scanRfid(promise)
  }

  fun cancelScan() {
    checkKernel()
    sunmiRfidHelper?.cancelScan()
  }

  fun printText(
    str: String,
    fontSize: Int,
    isBold: Boolean,
    isUnderline: Boolean
  ) {
    checkKernel()
    sunmiPrintHelper.printText(
      str,
      fontSize.toFloat(),
      isBold,
      isUnderline,
      null)
  }

  fun printEmptyLine() {
    checkKernel()
    sunmiPrintHelper.printEmptyLine()
  }

  fun printBpLogo() {
    checkKernel()

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

  fun setPrinterAlign(align: String) {
    checkKernel()
    when (align) {
      "left" -> sunmiPrintHelper.setPrinterAlign(0)
      "center" -> sunmiPrintHelper.setPrinterAlign(1)
      "right" -> sunmiPrintHelper.setPrinterAlign(2)
      else -> throw Exception("'$align' não reconhecido! Utilize somente um desses: ['left', 'center', 'right']")
    }
  }

  fun printBitmap(bitmapStr: String, width: Int, height: Int) {
    val options = BitmapFactory.Options().apply {
      inTargetDensity = 160
      inDensity = 160
    }
    val decodedBytes = Base64.decode(bitmapStr, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(decodedBytes), null, options)
      ?: throw Exception("Erro ao decodificar o bitmap. Verifique se o bitmap enviado é um base64.")

    sunmiPrintHelper.printBitmap(
      Bitmap.createScaledBitmap(
        bitmap,
        width,
        height,
        true
      ),
      1
    )
  }
}

