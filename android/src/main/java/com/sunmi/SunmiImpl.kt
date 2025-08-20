package com.sunmi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.mesmart.R
import com.mesmart.Utils
import com.sunmi.utils.PrinterSunmiP2
import com.sunmi.utils.MifareSunmiP2
import sunmi.paylib.SunmiPayKernel
import sunmi.paylib.SunmiPayKernel.ConnectCallback
import java.io.ByteArrayInputStream

class SunmiImpl(
  private val reactContext: ReactApplicationContext
) {
  private val isSupported = Utils.isSunmiP2()
  private var payKernel: SunmiPayKernel? = null
  private var mifareSunmiP2: MifareSunmiP2? = null
  private var printerSunmiP2: PrinterSunmiP2 = PrinterSunmiP2(reactContext)

  init {
    if (isSupported) {
      payKernel = SunmiPayKernel.getInstance()

      payKernel?.initPaySDK(reactContext, object : ConnectCallback {
        override fun onConnectPaySDK() {}
        override fun onDisconnectPaySDK() {}
      })

      mifareSunmiP2 = payKernel?.let { MifareSunmiP2(it) }
    }
  }

  private fun checkKernel() {
    if (!isSupported) throw Exception("Unsupported device (Supported model Sunmi P2).")
    if (payKernel == null)
      throw Exception("")
  }

  fun scanRfid(promise: Promise) {
    checkKernel()
    mifareSunmiP2?.scanRfid(promise)
  }

  fun cancelScan() {
    checkKernel()
    mifareSunmiP2?.cancelScan()
  }

  fun printText(
    str: String,
    fontSize: Int,
    isBold: Boolean,
    isUnderline: Boolean
  ) {
    checkKernel()
    printerSunmiP2.printText(
      str,
      fontSize.toFloat(),
      isBold,
      isUnderline,
      null)
  }

  fun printEmptyLine() {
    checkKernel()
    printerSunmiP2.printEmptyLine()
  }

  fun printBpLogo() {
    checkKernel()

    val options = BitmapFactory.Options().apply {
      inTargetDensity = 160
      inDensity = 160
    }
    val bitmap = BitmapFactory.decodeResource(reactContext.resources, R.drawable.logo_black, options)

    printerSunmiP2.printBitmap(
      Bitmap.createScaledBitmap(bitmap, 375, 100, true),
      1
    )
  }

  fun setPrinterAlign(align: String) {
    checkKernel()
    when (align) {
      "left" -> printerSunmiP2.setPrinterAlign(0)
      "center" -> printerSunmiP2.setPrinterAlign(1)
      "right" -> printerSunmiP2.setPrinterAlign(2)
      else -> throw Exception("'$align' '' not recognized! Use only one of these: ['left', 'center', 'right']")
    }
  }

  fun printBitmap(bitmapStr: String, width: Int, height: Int) {
    val options = BitmapFactory.Options().apply {
      inTargetDensity = 160
      inDensity = 160
    }
    val decodedBytes = Base64.decode(bitmapStr, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(decodedBytes), null, options)
      ?: throw Exception("Error decoding bitmap. Check if the uploaded bitmap is base64.")

    printerSunmiP2.printBitmap(
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

