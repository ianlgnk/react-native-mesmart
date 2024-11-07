package com.pax

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.mesmart.R
import com.mesmart.Utils
import com.pax.dal.IPicc
import com.pax.dal.IPrinter
import com.pax.dal.entity.EDetectMode
import com.pax.dal.entity.EFontTypeAscii
import com.pax.dal.entity.EFontTypeExtCode
import com.pax.dal.entity.EPiccType
import com.pax.dal.entity.PiccCardInfo
import com.pax.neptunelite.api.NeptuneLiteUser
import com.pax.utils.PaxConverter

class PaxImpl(
  private val reactContext: ReactApplicationContext
) {
  private var picc: IPicc? = null
  private var printer: IPrinter? = null
  private val handler = Handler(Looper.getMainLooper())
  private var isSearchingCard = false

  init {
    if (Utils.isA910()) {
      picc = NeptuneLiteUser
        .getInstance()
        .getDal(reactContext)
        .getPicc(EPiccType.INTERNAL)
      printer = NeptuneLiteUser
        .getInstance()
        .getDal(reactContext)
        .printer
      PaxConverter.instance?.setContext(reactContext)
    }
  }

  private fun checkPax(instance: String) {
    if (instance == "picc" && picc == null)
      throw Exception("Picc da Pax não foi inicializado! Você deve utilizar esse método em um dispositivo Pax A910 ou ocorreu algum erro durante a inicialização!")
    if (instance == "printer" && printer == null)
      throw Exception("Printer da Pax não foi inicializado! Você deve utilizar esse método em um dispositivo Pax A910 ou ocorreu algum erro durante a inicialização!")
  }

  fun scanRfid(promise: Promise) {
    checkPax("picc")

    isSearchingCard = true
    val timeoutRunnable = Runnable {
      if (isSearchingCard) {
        isSearchingCard = false
        promise.reject("Nenhum RFID encontrado em 30 segundos!")
      }
    }

    handler.postDelayed(timeoutRunnable, 30000)

    Thread {
      try {
        while (isSearchingCard) {
          picc?.open()
          val cardInfo: PiccCardInfo? = picc?.detect(EDetectMode.ONLY_M)
          if (cardInfo != null) {
            handler.removeCallbacks(timeoutRunnable)
            isSearchingCard = false
            promise.resolve(
              PaxConverter.instance?.bcdToStr(
                if ((cardInfo.serialInfo == null)) "".toByteArray()
                else cardInfo.serialInfo
              )?.let {
                Utils.resolveRFID(it)
              }
            )
            return@Thread
          }
          picc?.close()
          Thread.sleep(100)
        }
      } catch (e: Exception) {
        e.printStackTrace()
        promise.reject(e)
      }
    }.start()
  }

  fun cancelScan() {
    checkPax("picc")
    isSearchingCard = false
    handler.removeCallbacksAndMessages(null)
  }

  fun printerInit() {
    checkPax("printer")
    printer?.init()
  }

  fun printerStart() {
    checkPax("printer")
    printer?.start()
  }

  fun addText(
    str: String,
    fontSize: Int
  ) {
    checkPax("printer")
    printer?.spaceSet(0, 0)
    if (fontSize == 20) printer?.fontSet(EFontTypeAscii.FONT_8_32, EFontTypeExtCode.FONT_16_16)
    else printer?.fontSet(EFontTypeAscii.FONT_12_24, EFontTypeExtCode.FONT_16_16)
    printer?.printStr(str, null)
  }

  fun addBpLogo() {
    checkPax("printer")
    val options = BitmapFactory.Options().apply {
      inTargetDensity = 160
      inDensity = 160
    }
    val bitmap = BitmapFactory.decodeResource(
      reactContext.resources,
      R.drawable.logo_black,
      options
    )
    printer?.printBitmap(
      Bitmap.createScaledBitmap(bitmap, 375, 100, true)
    )
  }
}
