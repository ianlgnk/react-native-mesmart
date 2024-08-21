package com.paystore.utils

import java.math.BigDecimal

object DataTypeUtils {
  fun getValueFromString(s: String?): BigDecimal {
    return BigDecimal(s)
  }
}
