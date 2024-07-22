package com.sunmi.utils

import android.graphics.Bitmap
import android.os.RemoteException
import com.facebook.react.bridge.ReactApplicationContext
import com.sunmi.peripheral.printer.InnerPrinterCallback
import com.sunmi.peripheral.printer.InnerPrinterException
import com.sunmi.peripheral.printer.InnerPrinterManager
import com.sunmi.peripheral.printer.SunmiPrinterService
import com.sunmi.peripheral.printer.WoyouConsts

class SunmiPrintHelper(
  private val reactContext: ReactApplicationContext
) {
  var NoSunmiPrinter: Int = 0x00000000
  var CheckSunmiPrinter: Int = 0x00000001
  var FoundSunmiPrinter: Int = 0x00000002
  var LostSunmiPrinter: Int = 0x00000003
  // sunmiPrinter means checking the printer connection status
  var sunmiPrinter: Int = CheckSunmiPrinter

  private var sunmiPrinterService: SunmiPrinterService? = null

  private val innerPrinterCallback: InnerPrinterCallback = object : InnerPrinterCallback() {
    override fun onConnected(service: SunmiPrinterService) {
      sunmiPrinterService = service
      checkSunmiPrinterService(service)
      sunmiPrinterService!!.printerInit(null)
    }

    override fun onDisconnected() {
      sunmiPrinterService = null
      sunmiPrinter = LostSunmiPrinter
    }
  }

  init {
    initSunmiPrinterService()
  }

  private fun initSunmiPrinterService() {
    try {
      val ret = InnerPrinterManager.getInstance().bindService(
        reactContext,
        innerPrinterCallback
      )
      if (!ret) {
        sunmiPrinter = this.NoSunmiPrinter
      }
    } catch (e: InnerPrinterException) {
      e.printStackTrace()
    }
  }

  private fun checkSunmiPrinterService(service: SunmiPrinterService) {
    var ret = false
    try {
      ret = InnerPrinterManager.getInstance().hasPrinter(service)
    } catch (e: InnerPrinterException) {
      e.printStackTrace()
    }
    sunmiPrinter = if (ret) FoundSunmiPrinter else NoSunmiPrinter
  }

  fun printText(
    content: String?,
    size: Float,
    isBold: Boolean,
    isUnderLine: Boolean,
    typeface: String?
  ) {
    if (sunmiPrinterService == null) {
      return
    }

    try {
      try {
        sunmiPrinterService!!.setPrinterStyle(
          WoyouConsts.ENABLE_BOLD,
          if (isBold) WoyouConsts.ENABLE else WoyouConsts.DISABLE
        )
      } catch (e: RemoteException) {
        if (isBold) {
          sunmiPrinterService!!.sendRAWData(ESCUtil.boldOn(), null)
        } else {
          sunmiPrinterService!!.sendRAWData(ESCUtil.boldOff(), null)
        }
      }
      try {
        sunmiPrinterService!!.setPrinterStyle(
          WoyouConsts.ENABLE_UNDERLINE,
          if (isUnderLine) WoyouConsts.ENABLE else WoyouConsts.DISABLE
        )
      } catch (e: RemoteException) {
        if (isUnderLine) {
          sunmiPrinterService!!.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null)
        } else {
          sunmiPrinterService!!.sendRAWData(ESCUtil.underlineOff(), null)
        }
      }
      sunmiPrinterService!!.printTextWithFont(content, typeface, size, null)
    } catch (e: RemoteException) {
      e.printStackTrace()
    }
  }

  fun printEmptyLine() {
    if (sunmiPrinterService == null) {
      return
    }

    try {
      sunmiPrinterService!!.lineWrap(1, null)
    } catch (e: RemoteException) {
      e.printStackTrace()
    }
  }

  fun printBitmap(bitmap: Bitmap?, orientation: Int) {
    if (sunmiPrinterService == null) {
      return
    }

    try {
      if (orientation == 0) {
        sunmiPrinterService!!.printBitmap(bitmap, null)
        sunmiPrinterService!!.printText("\n", null)
      } else {
        sunmiPrinterService!!.printBitmap(bitmap, null)
        sunmiPrinterService!!.printText("\n\n", null)
      }
    } catch (e: RemoteException) {
      e.printStackTrace()
    }
  }

  fun setPrinterAlign(align: Int) {
    if (sunmiPrinterService == null) {
      return
    }

    try {
      sunmiPrinterService!!.setAlignment(align, null)
    } catch (e: RemoteException) {
      e.printStackTrace()
    }
  }
}
