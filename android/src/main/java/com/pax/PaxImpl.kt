package com.pax

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.mesmart.R
import com.mesmart.utils.Utils
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
  private val picc: IPicc = NeptuneLiteUser
                              .getInstance()
                              .getDal(reactContext)
                              .getPicc(EPiccType.INTERNAL)
  private val printer: IPrinter = NeptuneLiteUser
                                    .getInstance()
                                    .getDal(reactContext)
                                    .printer
  private val handler = Handler(Looper.getMainLooper())
  private var isSearchingCard = false

  fun searchRfid(promise: Promise) {
    isSearchingCard = true
    val timeoutRunnable = Runnable {
      if (isSearchingCard) {
        isSearchingCard = false
        promise.reject("TimeoutException", "Card not found within 30 seconds!")
      }
    }

    handler.postDelayed(timeoutRunnable, 30000)

    Thread {
      while (isSearchingCard) {
        picc.open()
        val cardInfo: PiccCardInfo? = picc.detect(EDetectMode.ONLY_M)
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
        picc.close()
        Thread.sleep(100)
      }
    }.start()
  }

  fun cancelCardSearch() {
    isSearchingCard = false
    handler.removeCallbacksAndMessages(null)
  }

  fun printText(
    str: String,
    fontSize: Int
  ) {
    printer.init()
    printer.spaceSet(0, 0)
    if (fontSize == 20) printer.fontSet(EFontTypeAscii.FONT_8_32, EFontTypeExtCode.FONT_16_16)
    else printer.fontSet(EFontTypeAscii.FONT_12_24, EFontTypeExtCode.FONT_16_16)
    printer.printStr(str, null)
    printer.start()
  }

  fun printBpLogo() {
    printer.init()
    val options = BitmapFactory.Options().apply {
      inTargetDensity = 160
      inDensity = 160
    }
    val bitmap = BitmapFactory.decodeResource(
      reactContext.resources,
      R.drawable.logo_black,
      options
    )
    printer.printBitmap(
      Bitmap.createScaledBitmap(bitmap, 375, 100, true)
    )
    printer.start()
  }
}
