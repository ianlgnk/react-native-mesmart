package com.gertec

import android.content.Context
import android.os.Process
import com.gertec.backend.GertecBackend
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexClassLoader
import java.io.File

/**
 * Carrega o SDK Gertec Neo (libgedi 2.x) num classloader proprio, so na GPOS780S.
 *
 * O app usa a libgedi 1.16 nos demais POS e as duas tem as mesmas classes e o mesmo libgedi.so (JNI por nome,
 * entao nao da p/ renomear pacote), por isso a Neo vai num dex a parte (assets/gertec-neo, ver gertec-neo/build.sh)
 * e o libgedi.so dela fica fora de jniLibs, extraido p/ uma pasta que so esse classloader enxerga.
 */
internal object NeoSdkLoader {
  private const val ASSET_DIR = "gertec-neo"
  private const val ADAPTER_CLASS = "com.gertec.neo.NeoGertecBackend"

  @Synchronized
  fun load(context: Context): GertecBackend {
    val abi = if (Process.is64Bit()) "arm64-v8a" else "armeabi-v7a"
    val assets = context.assets
    val version = assets.open("$ASSET_DIR/VERSION").bufferedReader().use { it.readText().trim() }

    val root = File(context.codeCacheDir, "$ASSET_DIR/$version-$abi")
    val jar = File(root, "neo-bridge.jar")
    val libDir = File(root, "lib")
    val so = File(libDir, "libgedi.so")

    if (!jar.exists() || !so.exists()) {
      File(context.codeCacheDir, ASSET_DIR).deleteRecursively()
      libDir.mkdirs()
      copyAsset(context, "$ASSET_DIR/neo-bridge.jar", jar)
      copyAsset(context, "$ASSET_DIR/$abi/libgedi.so", so)
    }
    // Android 14+ recusa dex gravavel em DexClassLoader.
    jar.setReadOnly()

    val parent = GertecBackend::class.java.classLoader
      ?: throw IllegalStateException("Sem classloader do app para o SDK Gertec Neo")
    val loader = ChildFirstDexClassLoader(jar.absolutePath, libDir.absolutePath, parent)
    val adapter = loader.loadClass(ADAPTER_CLASS)
    return adapter.getConstructor(Context::class.java).newInstance(context.applicationContext) as GertecBackend
  }

  private fun copyAsset(context: Context, assetPath: String, dest: File) {
    context.assets.open(assetPath).use { input ->
      dest.outputStream().use { output -> input.copyTo(output) }
    }
  }
}

/**
 * Classloader "filho primeiro": as classes do dex da Neo (br.com.gertec.*, gedi.*, ...) tem o mesmo nome das da 1.16
 * que estao no app, entao procura no dex antes de perguntar ao pai. Plataforma, Kotlin, React Native e a interface
 * com.gertec.backend continuam vindo do pai, para os tipos trocados entre os dois lados serem os mesmos.
 */
internal class ChildFirstDexClassLoader(
  dexPath: String,
  libPath: String,
  parent: ClassLoader
) : DexClassLoader(dexPath, null, libPath, parent) {

  override fun loadClass(name: String, resolve: Boolean): Class<*> {
    synchronized(this) {
      findLoadedClass(name)?.let { return it }
      if (!isShared(name)) {
        try {
          val clazz = findClass(name)
          if (resolve) resolveClass(clazz)
          return clazz
        } catch (_: ClassNotFoundException) {
          // nao esta no dex da Neo: cai no pai
        }
      }
      return super.loadClass(name, resolve)
    }
  }

  // O libgedi.so da Neo tem que vir daqui (nao da 1.16 do app); o resto (ex.: opencv) pode vir do pai.
  override fun findLibrary(name: String): String? =
    super.findLibrary(name) ?: (parent as? BaseDexClassLoader)?.findLibrary(name)

  private fun isShared(name: String): Boolean = SHARED_PREFIXES.any { name.startsWith(it) }

  private companion object {
    val SHARED_PREFIXES = arrayOf(
      "java.", "javax.", "android.", "androidx.", "dalvik.", "libcore.",
      "kotlin.", "kotlinx.", "com.facebook.", "com.gertec.backend."
    )
  }
}
