package com.paystore.utils

import br.com.phoebus.android.payments.api.ApplicationInfo
import br.com.phoebus.android.payments.api.Credentials

object CredentialsUtils {
  const val TEST_APPLICATION_ID: String = "0"
  const val TEST_SECRET_TOKEN: String = "REPLACE_WITH_PAYSTORE_SECRET_TOKEN"

  val myCredentials: Credentials
    get() {
      val credentials = Credentials()
      credentials.applicationId = TEST_APPLICATION_ID
      credentials.secretToken = TEST_SECRET_TOKEN
      return credentials
    }

  val myAppInfo: ApplicationInfo
    get() {
      val applicationInfo = ApplicationInfo()
      applicationInfo.credentials = myCredentials
      applicationInfo.softwareVersion = "1.0"

      return applicationInfo
    }
}

