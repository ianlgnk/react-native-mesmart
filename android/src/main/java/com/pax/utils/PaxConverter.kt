package com.pax.utils

import android.content.Context
import android.util.Log
import java.io.UnsupportedEncodingException
import java.text.DecimalFormat

class PaxConverter private constructor() {
  private var context: Context? = null

  fun setContext(context: Context?) {
    this.context = context
  }

  @Throws(IllegalArgumentException::class)
  fun bcdToStr(b: ByteArray?): String {
    if (b == null) {
      Log.e(TAG, "bcdToStr input arg is null")
      throw IllegalArgumentException("bcdToStr input arg is null")
    }

    val HEX_DIGITS =
      charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    val sb = StringBuilder(b.size * 2)
    for (i in b.indices) {
      sb.append(HEX_DIGITS[b[i].toInt() and 0xf0 ushr 4])
      sb.append(HEX_DIGITS[b[i].toInt() and 0x0f])
    }

    return sb.toString()
  }

  @Throws(IllegalArgumentException::class)
  fun strToBcd(str: String?, paddingPosition: EPaddingPosition?): ByteArray {
    var str = str
    if (str == null || paddingPosition == null) {
      Log.e(TAG, "strToBcd input arg is null")
      throw IllegalArgumentException("strToBcd input arg is null")
    }

    var len = str.length
    val mod = len % 2
    if (mod != 0) {
      str = if (paddingPosition == EPaddingPosition.PADDING_RIGHT) {
        str + "0"
      } else {
        "0$str"
      }
      len = str.length
    }
    var abt = ByteArray(len)
    if (len >= 2) {
      len = len / 2
    }
    val bbt = ByteArray(len)
    abt = str.toByteArray()
    var j: Int
    var k: Int
    for (p in 0 until str.length / 2) {
      j = if ((abt[2 * p] >= 'a'.code.toByte()) && (abt[2 * p] <= 'z'.code.toByte())) {
        abt[2 * p] - 'a'.code.toByte() + 0x0a
      } else if ((abt[2 * p] >= 'A'.code.toByte()) && (abt[2 * p] <= 'Z'.code.toByte())) {
        abt[2 * p] - 'A'.code.toByte() + 0x0a
      } else {
        abt[2 * p] - '0'.code.toByte()
      }

      k = if ((abt[2 * p + 1] >= 'a'.code.toByte()) && (abt[2 * p + 1] <= 'z'.code.toByte())) {
        abt[2 * p + 1] - 'a'.code.toByte() + 0x0a
      } else if ((abt[2 * p + 1] >= 'A'.code.toByte()) && (abt[2 * p + 1] <= 'Z'.code.toByte())) {
        abt[2 * p + 1] - 'A'.code.toByte() + 0x0a
      } else {
        abt[2 * p + 1] - '0'.code.toByte()
      }

      val a = (j shl 4) + k
      val b = a.toByte()
      bbt[p] = b
    }
    return bbt
  }

  @Throws(IllegalArgumentException::class)
  fun longToByteArray(l: Long, to: ByteArray?, offset: Int, endian: EEndian?) {
    if (to == null || endian == null) {
      Log.e(TAG, "longToByteArray input arg is null")
      throw IllegalArgumentException("longToByteArray input arg is null")
    }

    if (endian == EEndian.BIG_ENDIAN) {
      to[offset] = ((l ushr 56) and 0xffL).toByte()
      to[offset + 1] = ((l ushr 48) and 0xffL).toByte()
      to[offset + 2] = ((l ushr 40) and 0xffL).toByte()
      to[offset + 3] = ((l ushr 32) and 0xffL).toByte()
      to[offset + 4] = ((l ushr 24) and 0xffL).toByte()
      to[offset + 5] = ((l ushr 16) and 0xffL).toByte()
      to[offset + 6] = ((l ushr 8) and 0xffL).toByte()
      to[offset + 7] = (l and 0xffL).toByte()
    } else {
      to[offset + 7] = ((l ushr 56) and 0xffL).toByte()
      to[offset + 6] = ((l ushr 48) and 0xffL).toByte()
      to[offset + 5] = ((l ushr 40) and 0xffL).toByte()
      to[offset + 4] = ((l ushr 32) and 0xffL).toByte()
      to[offset + 3] = ((l ushr 24) and 0xffL).toByte()
      to[offset + 2] = ((l ushr 16) and 0xffL).toByte()
      to[offset + 1] = ((l ushr 8) and 0xffL).toByte()
      to[offset] = (l and 0xffL).toByte()
    }
  }

  @Throws(IllegalArgumentException::class)
  fun longToByteArray(l: Long, endian: EEndian?): ByteArray {
    if (endian == null) {
      Log.e(TAG, "longToByteArray input arg is null")
      throw IllegalArgumentException("longToByteArray input arg is null")
    }

    val to = ByteArray(8)

    if (endian == EEndian.BIG_ENDIAN) {
      to[0] = ((l ushr 56) and 0xffL).toByte()
      to[1] = ((l ushr 48) and 0xffL).toByte()
      to[2] = ((l ushr 40) and 0xffL).toByte()
      to[3] = ((l ushr 32) and 0xffL).toByte()
      to[4] = ((l ushr 24) and 0xffL).toByte()
      to[5] = ((l ushr 16) and 0xffL).toByte()
      to[6] = ((l ushr 8) and 0xffL).toByte()
      to[7] = (l and 0xffL).toByte()
    } else {
      to[7] = ((l ushr 56) and 0xffL).toByte()
      to[6] = ((l ushr 48) and 0xffL).toByte()
      to[5] = ((l ushr 40) and 0xffL).toByte()
      to[4] = ((l ushr 32) and 0xffL).toByte()
      to[3] = ((l ushr 24) and 0xffL).toByte()
      to[2] = ((l ushr 16) and 0xffL).toByte()
      to[1] = ((l ushr 8) and 0xffL).toByte()
      to[0] = (l and 0xffL).toByte()
    }

    return to
  }

  @Throws(IllegalArgumentException::class)
  fun intToByteArray(i: Int, to: ByteArray?, offset: Int, endian: EEndian?) {
    if (to == null || endian == null) {
      Log.e(TAG, "longToByteArray input arg is null")
      throw IllegalArgumentException("longToByteArray input arg is null")
    }

    if (endian == EEndian.BIG_ENDIAN) {
      to[offset] = ((i ushr 24) and 0xff).toByte()
      to[offset + 1] = ((i ushr 16) and 0xff).toByte()
      to[offset + 2] = ((i ushr 8) and 0xff).toByte()
      to[offset + 3] = (i and 0xff).toByte()
    } else {
      to[offset] = (i and 0xff).toByte()
      to[offset + 1] = ((i ushr 8) and 0xff).toByte()
      to[offset + 2] = ((i ushr 16) and 0xff).toByte()
      to[offset + 3] = ((i ushr 24) and 0xff).toByte()
    }
  }

  @Throws(IllegalArgumentException::class)
  fun intToByteArray(i: Int, endian: EEndian?): ByteArray {
    if (endian == null) {
      Log.e(TAG, "intToByteArray input arg is null")
      throw IllegalArgumentException("intToByteArray input arg is null")
    }

    val to = ByteArray(4)

    if (endian == EEndian.BIG_ENDIAN) {
      to[0] = ((i ushr 24) and 0xff).toByte()
      to[1] = ((i ushr 16) and 0xff).toByte()
      to[2] = ((i ushr 8) and 0xff).toByte()
      to[3] = (i and 0xff).toByte()
    } else {
      to[0] = (i and 0xff).toByte()
      to[1] = ((i ushr 8) and 0xff).toByte()
      to[2] = ((i ushr 16) and 0xff).toByte()
      to[3] = ((i ushr 24) and 0xff).toByte()
    }

    return to
  }

  @Throws(IllegalArgumentException::class)
  fun shortToByteArray(s: Short, to: ByteArray?, offset: Int, endian: EEndian?) {
    if (to == null || endian == null) {
      Log.e(TAG, "shortToByteArray input arg is null")
      throw IllegalArgumentException("shortToByteArray input arg is null")
    }

    if (endian == EEndian.BIG_ENDIAN) {
      to[offset] = ((s.toInt() ushr 8) and 0xff).toByte()
      to[offset + 1] = (s.toInt() and 0xff).toByte()
    } else {
      to[offset] = (s.toInt() and 0xff).toByte()
      to[offset + 1] = ((s.toInt() ushr 8) and 0xff).toByte()
    }
  }

  @Throws(IllegalArgumentException::class)
  fun shortToByteArray(s: Short, endian: EEndian?): ByteArray {
    if (endian == null) {
      Log.e(TAG, "shortToByteArray input arg is null")
      throw IllegalArgumentException("shortToByteArray input arg is null")
    }

    val to = ByteArray(2)

    if (endian == EEndian.BIG_ENDIAN) {
      to[0] = ((s.toInt() ushr 8) and 0xff).toByte()
      to[1] = (s.toInt() and 0xff).toByte()
    } else {
      to[0] = (s.toInt() and 0xff).toByte()
      to[1] = ((s.toInt() ushr 8) and 0xff).toByte()
    }

    return to
  }

  @Throws(IllegalArgumentException::class)
  fun longFromByteArray(from: ByteArray?, offset: Int, endian: EEndian?): Long {
    if (from == null || endian == null) {
      Log.e(TAG, "longFromByteArray input arg is null")
      throw IllegalArgumentException("longFromByteArray input arg is null")
    }

    return if (endian == EEndian.BIG_ENDIAN) {
      (((from[offset].toInt() shl 56).toLong() and -0x100000000000000L) or ((from[offset + 1].toInt() shl 48).toLong() and 0xff000000000000L)
        or ((from[offset + 2].toInt() shl 40).toLong() and 0xff0000000000L) or ((from[offset + 3].toInt() shl 32).toLong() and 0xff00000000L)
        or ((from[offset + 4].toInt() shl 24) and -0x1000000).toLong() or ((from[offset + 5].toInt() shl 16) and 0xff0000)
        .toLong()
        or ((from[offset + 6].toInt() shl 8) and 0xff00).toLong() or (from[offset + 7].toInt() and 0xff).toLong())
    } else {
      (((from[offset + 7].toInt() shl 56).toLong() and -0x100000000000000L) or ((from[offset + 6].toInt() shl 48).toLong() and 0xff000000000000L)
        or ((from[offset + 5].toInt() shl 40).toLong() and 0xff0000000000L) or ((from[offset + 4].toInt() shl 32).toLong() and 0xff00000000L)
        or ((from[offset + 3].toInt() shl 24) and -0x1000000).toLong() or ((from[offset + 2].toInt() shl 16) and 0xff0000)
        .toLong()
        or ((from[offset + 1].toInt() shl 8) and 0xff00).toLong() or (from[offset].toInt() and 0xff).toLong())
    }
  }

  @Throws(IllegalArgumentException::class)
  fun intFromByteArray(from: ByteArray?, offset: Int, endian: EEndian?): Int {
    if (from == null || endian == null) {
      Log.e(TAG, "intFromByteArray input arg is null")
      throw IllegalArgumentException("intFromByteArray input arg is null")
    }

    return if (endian == EEndian.BIG_ENDIAN) {
      (((from[offset].toInt() shl 24) and -0x1000000) or ((from[offset + 1].toInt() shl 16) and 0xff0000)
        or ((from[offset + 2].toInt() shl 8) and 0xff00) or (from[offset + 3].toInt() and 0xff))
    } else {
      (((from[offset + 3].toInt() shl 24) and -0x1000000) or ((from[offset + 2].toInt() shl 16) and 0xff0000)
        or ((from[offset + 1].toInt() shl 8) and 0xff00) or (from[offset].toInt() and 0xff))
    }
  }

  @Throws(IllegalArgumentException::class)
  fun shortFromByteArray(from: ByteArray?, offset: Int, endian: EEndian?): Short {
    if (from == null || endian == null) {
      Log.e(TAG, "shortFromByteArray input arg is null")
      throw IllegalArgumentException("shortFromByteArray input arg is null")
    }

    return if (endian == EEndian.BIG_ENDIAN) {
      (((from[offset].toInt() shl 8) and 0xff00) or (from[offset + 1].toInt() and 0xff)).toShort()
    } else {
      (((from[offset + 1].toInt() shl 8) and 0xff00) or (from[offset].toInt() and 0xff)).toShort()
    }
  }

  @Throws(IllegalArgumentException::class)
  fun stringPadding(
    src: String?,
    paddingChar: Char,
    expLength: Long,
    paddingpos: EPaddingPosition?
  ): String {
    if (src == null || paddingpos == null) {
      Log.e(TAG, "stringPadding input arg is null")
      throw IllegalArgumentException("stringPadding input arg is null")
    }

    if (src.length >= expLength) {
      return src
    }

    if (paddingpos == EPaddingPosition.PADDING_RIGHT) {
      val sb = StringBuffer(src)
      for (i in 0 until expLength - src.length) {
        sb.append(paddingChar)
      }

      return sb.toString()
    } else {
      val sb = StringBuffer()
      for (i in 0 until expLength - src.length) {
        sb.append(paddingChar)
      }

      sb.append(src)
      return sb.toString()
    }
  }

  @Throws(IllegalArgumentException::class)
  fun amountMajorToMinUnit(major: Double, exponent: ECurrencyExponent?): String {
    if (exponent == null) {
      Log.e(TAG, "amountMajorToMinUnit input arg is null")
      throw IllegalArgumentException("amountMajorToMinUnit input arg is null")
    }

    val decimalFormat = DecimalFormat("0.000")
    val majorS = decimalFormat.format(major)

    val ht = majorS.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
      .toTypedArray()

    val h = ht[0]
    var t = ht[1]

    if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_2) {
      t = t.substring(0, 2)

      return h + t
    } else if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_0) {
      return h
    } else if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_1) {
      t = t.substring(0, 1)

      return h + t
    } else if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_3) {
      t = t.substring(0, 3)

      return h + t
    }

    return ""
  }

  @Throws(IllegalArgumentException::class)
  fun amountMajorToMinUnit(major: String?, exponent: ECurrencyExponent?): String {
    var major = major
    if (major == null || exponent == null) {
      Log.e(TAG, "amountMajorToMinUnit input arg is null")
      throw IllegalArgumentException("amountMajorToMinUnit input arg is null")
    }
    major = major.replace(",", "")
    return amountMajorToMinUnit(major.trim { it <= ' ' }.toDouble(), exponent)
  }

  @Throws(IllegalArgumentException::class)
  fun amountMinUnitToMajor(
    minUnit: String?,
    exponent: ECurrencyExponent?,
    isCommaStyle: Boolean
  ): String {
    if (minUnit == null || exponent == null || minUnit == "") {
      Log.e(TAG, "amountMinUnitToMajor input arg is null")
      throw IllegalArgumentException("amountMinUnitToMajor input arg is null")
    }
    if (!minUnit.matches("[0-9]+".toRegex())) {
      Log.e(TAG, "amountMinUnitToMajor input arg is illegal")
      throw IllegalArgumentException("amountMinUnitToMajor input arg is illegal")
    }
    // 去掉头尾可能的空格
    var temp = minUnit.trim { it <= ' ' }
    temp = temp.toLong().toString() + "" // 删除minUnit的前导0
    if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_2) {
      if (temp.length < 3) {
        val add = 3 - temp.length
        for (i in 0 until add) {
          temp = "0$temp"
        }
      }
      temp = temp.substring(0, temp.length - 2) + "." + temp.substring(temp.length - 2)
    } else if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_1) {
      if (temp.length < 2) {
        val add = 2 - temp.length
        for (i in 0 until add) {
          temp = "0$temp"
        }
      }
      temp = temp.substring(0, temp.length - 1) + "." + temp.substring(temp.length - 1)
    } else if (exponent == ECurrencyExponent.CURRENCY_EXPONENT_3) {
      if (temp.length < 4) {
        val add = 4 - temp.length
        for (i in 0 until add) {
          temp = "0$temp"
        }
      }
      temp = temp.substring(0, temp.length - 3) + "." + temp.substring(temp.length - 3)
    }
    if (isCommaStyle == true) {
      val index = temp.indexOf(".")
      var strAdd = ""
      if (index != -1) {
        strAdd = temp.substring(index)
        temp = temp.substring(0, index)
      }
      temp = StringBuffer(temp).reverse().toString() // 先将字符串颠倒顺序
      var str2 = ""
      val size = if ((temp.length % 3 == 0)) (temp.length / 3) else (temp.length / 3 + 1) // 每三位取一长度
      /*
             * 比如把一段字符串分成n段,第n段可能不是三个数,有可能是一个或者两个, 现将字符串分成两部分.一部分为前n-1段,第二部分为第n段.前n-1段，每一段加一",".而第n段直接取出即可
             */
      for (i in 0 until size - 1) { // 前n-1段
        str2 += temp.substring(i * 3, i * 3 + 3) + ","
      }
      for (i in size - 1 until size) { // 第n段
        str2 += temp.substring(i * 3, temp.length)
      }
      str2 = StringBuffer(str2).reverse().toString()
      str2 += strAdd
      return str2
    }
    return temp
  }

  /**
   * padding position
   *
   */
  enum class EPaddingPosition {
    /**
     * padding left
     */
    PADDING_LEFT,

    /**
     * padding right
     */
    PADDING_RIGHT
  }

  /**
   * endian type
   *
   */
  enum class EEndian {
    /**
     * little endian
     */
    LITTLE_ENDIAN,

    /**
     * big endian
     */
    BIG_ENDIAN
  }

  /**
   * currency exponent. refer to ISO4217
   *
   */
  enum class ECurrencyExponent {
    /**
     * currency exponent 0
     */
    CURRENCY_EXPONENT_0,

    /**
     * currency exponent 1
     */
    CURRENCY_EXPONENT_1,

    /**
     * currency exponent 2
     */
    CURRENCY_EXPONENT_2,

    /**
     * currency exponent 3
     */
    CURRENCY_EXPONENT_3
  }

  companion object {
    private val TAG: String = PaxConverter::class.java.simpleName

    @get:Synchronized
    var instance: PaxConverter? = null
      get() {
        if (field == null) {
          field = PaxConverter()
        }

        return field
      }
      private set

    fun bytes2String(source: ByteArray): String {
      var result = ""
      try {
        if (source.size > 0) result = String(source, charset("UTF-8"))
      } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
      }
      return result.trim { it <= ' ' }
    }
  }
}
