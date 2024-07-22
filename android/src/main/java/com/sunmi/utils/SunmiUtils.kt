package com.sunmi.utils

import android.os.Build

class SunmiUtils {
  companion object {
    fun isSunmiP2(): Boolean {
      return Build.MODEL.equals("P2-B", true)
    }

    fun resolveRFIDBySerialNumber(value: String): String {
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
  }
}
