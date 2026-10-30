package com.mesmart

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
  fun isSunmiP2(): Boolean {
    return Build.MODEL.equals("P2-B", true) || Build.MODEL.equals("P2-A11", true)
  }

  fun isA910(): Boolean {
    return Build.MODEL.equals("A910", true)
  }

  /** Cobre GPOS780 e GPOS780S (mesma familia Gertec). */
  fun isGPOS780(): Boolean {
    return Build.MODEL.startsWith("GPOS780", true)
  }

  /** GPOS780S: nao inicializa o GEDI da libgedi 1.16, usa o SDK Neo (ver com.gertec.NeoSdkLoader). */
  fun isGPOS780S(): Boolean {
    return Build.MODEL.startsWith("GPOS780S", true)
  }

  /** Contexto do aparelho p/ mensagens de erro do Gertec (modelo + Android), p/ debugar em campo. */
  fun deviceContext(): String {
    return "model=${Build.MODEL}, android=${Build.VERSION.RELEASE} (sdk ${Build.VERSION.SDK_INT})"
  }

  fun isT8(): Boolean {
    return Build.MODEL.equals("T8", true)
  }

  fun resolveRFID(value: String): String {
    val length = value.length

    val n = StringBuilder()
    n.append(value.substring(0, 2)).append("-")

    if (length >= 4) {
      n.append(value.substring(2, 4))
      if (length > 4) n.append("-")
    }

    if (length >= 6) {
      n.append(value.substring(4, 6))
      if (length > 6) n.append("-")
    }

    if (length >= 8) {
      n.append(value.substring(6, 8))
      if (length > 8) n.append("-")
    }

    if (length >= 10) {
      n.append(value.substring(6, 10))
    }

    val split = n.toString().split("-")
    val splitSize = split.size
    val nova = StringBuilder()

    when (splitSize) {
      4 -> nova.append(split[3]).append(split[2]).append(split[1]).append(split[0])
      3 -> nova.append(split[2]).append(split[1]).append(split[0])
      2 -> nova.append(split[1]).append(split[0])
      else -> {}
    }

    val hexResult = nova.toString()
    val converted = hexResult.toLong(16)

    var id = converted.toString()

    if (id.length < 10) {
      id = "0$converted"
      if (id.length < 10) id = "00$converted"
      if (id.length < 10) id = "000$converted"
    }

    return id
  }

  fun convertStringToBitmap(data: String?): Bitmap? {
    return try {
      data?.let {
        val decodedBytes = Base64.decode(it, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
      }
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun convertStringToDate(dateString: String?): Date? {
    return try {
      val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      dateString?.let { format.parse(it) }
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun bitmapToBase64(bitmap: Bitmap?): String? {
    return bitmap?.let {
      val outputStream = java.io.ByteArrayOutputStream()
      it.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
      val byteArray = outputStream.toByteArray()
      Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
  }

  fun dateToString(date: Date?): String? {
    return date?.let {
      val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      format.format(it)
    }
  }

  fun byteArrayToHexString(input: ByteArray): String {
    val builder = StringBuilder()
    for (b in input) {
      builder.append(String.format("%02X", b))
    }
    return builder.toString()
  }
}
