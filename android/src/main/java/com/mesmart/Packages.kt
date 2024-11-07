package com.mesmart

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.pax.PaxModule
import com.paystore.PaystoreModule
import com.plugpag.PlugpagModule
import com.stone.StoneModule
import com.sunmi.SunmiModule

class Packages : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return listOf(
      SunmiModule(reactContext),
      PlugpagModule(reactContext),
      PaxModule(reactContext),
      PaystoreModule(reactContext),
      StoneModule(reactContext)
    )
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return emptyList()
  }
}
