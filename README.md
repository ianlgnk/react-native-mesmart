# react-native-mesmart

Métodos nativos para integrar com POS Sunmi P2-B/P2-A11, Pax A910/A920, SDK de pagamento Plugpag da Pagbank, SDK de pagamento da Stone e SDK de pagamento da Paystore.

- [Plugpag](https://developer.pagbank.com.br/v1.0/reference/smart-pos-introducao)
- [Stone](https://sdkandroid.stone.com.br/reference/overview-da-sdk)
- [Paystore](http://177.69.97.18:6655/)

## Instalação

```sh
npm install react-native-mesmart
```

OU

```sh
yarn add react-native-mesmart
```

## Configurações para Sunmi P2

Adicione a seguinte permissão no AndroidManifest do seu app

```
<uses-permission android:name="com.sunmi.perm.CONTACTLESS_CARD" />
```

## Configurações para Pax A910/A920

Adicione as seguintes permissões no AndroidManifest do seu app

```
<uses-permission android:name="com.pax.permission.PICC" />
<uses-permission android:name="com.pax.permission.PRINTER" />
```

Adicione essas configurações em android/app/build.gradle do seu app

```
android {
  ...
  defaultConfig {
    ...
    ndk {
      abiFilters "armeabi-v7a"
    }
  }
  ...
  packagingOptions {
    jniLibs {
      useLegacyPackaging true
    }
  }
}
```

Crie um diretório android/app/src/main/jniLibs/armeabi-v7 e adicione o arquivo 'libDeviceConfig.so' que está presente no exemplo dessa biblioteca

## Configurações para SDK Plugpag

Adicione a seguinte permissão no AndroidManifest do seu app

```
<uses-permission android:name="br.com.uol.pagseguro.permission.MANAGE_PAYMENTS" />
```

Adicione essa configuração em android/build.gradle do seu app

```
allprojects {
  repositories {
    ...
    maven {
      url 'https://github.com/pagseguro/PlugPagServiceWrapper/raw/master'
    }
  }
}
```

## Configurações para SDK Stone

Adicione as seguintes configurações no AndroidManifest do seu app

```
<manifest
  ...
  xmlns:tools="http://schemas.android.com/tools"
>
...
  <application
    ...
    android:allowBackup="true"
    tools:replace="android:allowBackup"
  >
    ...
  </application>
</manifest>
```

Adicione essas configurações em android/build.gradle do seu app

```
buildscript {
  ext {
    ...
    localProp = new Properties()
    fileName = 'local.properties'
    localProp.load(new FileInputStream(rootProject.file(fileName)))
    stoneToken = System.env.PACKAGECLOUD_READ_TOKEN != null ? System.env.PACKAGECLOUD_READ_TOKEN : (localProp["packageCloudReadToken"] ?: "")
  }
}

allprojects {
  repositories {
    ...
    maven { url "https://packagecloud.io/priv/${stoneToken}/stone/pos-android/maven2" }
  }
}
```

Lembre-se de adicionar no arquivo local.properties do diretório android do seu app o valor packageCloudReadToken com sua chave ofericida pela Stone

## Configurações para SDK Paystore

Adicione a seguinte permissão no AndroidManifest do seu app

```
<uses-permission android:name="br.com.phoebus.android.payments.provider.READ_PERMISSION" />
```

Adicione essa configuração em android/app/build.gradle do seu app

```
dependencies {
  ...
  implementation fileTree(dir: 'libs', include: ['*.aar'])
}
```

Crie um diretório android/app/libs e adicione o arquivo 'paystore-api-v2.11.7.31.aar' que está presente no exemplo dessa biblioteca

## Listeners

```ts
import { DeviceEventEmitter } from 'react-native';
import {
  type PlugpagEventData,
  type StoneEventData,
} from 'react-native-mesmart';

useEffect(() => {
  const plugpagSubscribe = DeviceEventEmitter.addListener(
    'onPlugpagEventData',
    (plugpagEvent: PlugPagEventData) => {
      console.log(plugpagEvent);
    }
  );

  const stoneSubscribe = DeviceEventEmitter.addListener(
    'onStoneEventData',
    (event: StoneEventData) => {
      console.log(event);
    }
  );

  return () => {
    plugpagSubscribe.remove();
    stoneSubscribe.remove();
  };
}, []);
```

## License

MIT

---
