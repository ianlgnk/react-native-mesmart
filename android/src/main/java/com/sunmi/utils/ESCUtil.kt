package com.sunmi.utils

object ESCUtil {
  const val ESC: Byte = 0x1B // Escape
  const val FS: Byte = 0x1C // Text delimiter
  const val GS: Byte = 0x1D // Group separator
  const val DLE: Byte = 0x10 // data link escape
  const val EOT: Byte = 0x04 // End of transmission
  const val ENQ: Byte = 0x05 // Enquiry character
  const val SP: Byte = 0x20 // Spaces
  const val HT: Byte = 0x09 // Horizontal list
  const val LF: Byte = 0x0A //Print and wrap (horizontal orientation)
  const val CR: Byte = 0x0D // Home key
  const val FF: Byte =
    0x0C // Carriage control (print and return to the standard mode (in page mode))
  const val CAN: Byte = 0x18 // Canceled (cancel print data in page mode)

  fun setPrinterDarkness(value: Int): ByteArray {
    val result = ByteArray(9)
    result[0] = GS
    result[1] = 40
    result[2] = 69
    result[3] = 4
    result[4] = 0
    result[5] = 5
    result[6] = 5
    result[7] = (value shr 8).toByte()
    result[8] = value.toByte()
    return result
  }

  fun nextLine(lineNum: Int): ByteArray {
    val result = ByteArray(lineNum)
    for (i in 0 until lineNum) {
      result[i] = LF
    }

    return result
  }

  fun setDefaultLineSpace(): ByteArray {
    return byteArrayOf(0x1B, 0x32)
  }

  fun setLineSpace(height: Int): ByteArray {
    return byteArrayOf(0x1B, 0x33, height.toByte())
  }

  fun underlineWithOneDotWidthOn(): ByteArray {
    val result = ByteArray(3)
    result[0] = ESC
    result[1] = 45
    result[2] = 1
    return result
  }

  fun underlineWithTwoDotWidthOn(): ByteArray {
    val result = ByteArray(3)
    result[0] = ESC
    result[1] = 45
    result[2] = 2
    return result
  }

  fun underlineOff(): ByteArray {
    val result = ByteArray(3)
    result[0] = ESC
    result[1] = 45
    result[2] = 0
    return result
  }

  fun boldOn(): ByteArray {
    val result = ByteArray(3)
    result[0] = ESC
    result[1] = 69
    result[2] = 0xF
    return result
  }

  fun boldOff(): ByteArray {
    val result = ByteArray(3)
    result[0] = ESC
    result[1] = 69
    result[2] = 0
    return result
  }

  fun singleByte(): ByteArray {
    val result = ByteArray(2)
    result[0] = FS
    result[1] = 0x2E
    return result
  }

  fun singleByteOff(): ByteArray {
    val result = ByteArray(2)
    result[0] = FS
    result[1] = 0x26
    return result
  }

  fun setCodeSystemSingle(charset: Byte): ByteArray {
    val result = ByteArray(3)
    result[0] = ESC
    result[1] = 0x74
    result[2] = charset
    return result
  }

  fun setCodeSystem(charset: Byte): ByteArray {
    val result = ByteArray(3)
    result[0] = FS
    result[1] = 0x43
    result[2] = charset
    return result
  }

  fun alignLeft(): ByteArray {
    val result = ByteArray(3)
    result[0] = ESC
    result[1] = 97
    result[2] = 0
    return result
  }

  fun alignCenter(): ByteArray {
    val result = ByteArray(3)
    result[0] = ESC
    result[1] = 97
    result[2] = 1
    return result
  }

  fun alignRight(): ByteArray {
    val result = ByteArray(3)
    result[0] = ESC
    result[1] = 97
    result[2] = 2
    return result
  }

  fun cutter(): ByteArray {
    val data = byteArrayOf(0x1d, 0x56, 0x01)
    return data
  }

  fun labellocate(): ByteArray {
    return byteArrayOf(0x1C, 0x28, 0x4C, 0x02, 0x00, 0x43, 0x31)
  }

  fun labelout(): ByteArray {
    return byteArrayOf(0x1C, 0x28, 0x4C, 0x02, 0x00, 0x42, 0x31)
  }
}
