# react-native-mesmart

Native methods to integrate with Sunmi P2, Pax A910, Plugpag payment SDK and Paystore payment SDK.

- [Plugpag](https://developer.pagbank.com.br/v1.0/reference/smart-pos-introducao)
- [Paystore](https://backendphoebus.paystore.com.br/portal/api/docs/sdk/android/index.html)

## Installation

```sh
npm install react-native-mesmart
```

OR

```sh
yarn add react-native-mesmart
```

Add those lines below in your android/build.gradle

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

In your android/app/build.gradle, you must add those configs below

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

dependencies {
  ...
  implementation fileTree(dir: 'libs', include: ['*.aar'])
}
```

Create a new directory at android/app/src/main/jniLibs/armeabi-v7a and add the file 'libDeviceConfig.so' to this new folder

Create a new directory at android/app/libs/ and add the file 'paystore-api-v2.11.3.24.aar' to this new folder

## Permissions

Add those permissions in your AndroidManifest.xml

```
<uses-permission android:name="com.sunmi.perm.CONTACTLESS_CARD" />
<uses-permission android:name="br.com.uol.pagseguro.permission.MANAGE_PAYMENTS" />
<uses-permission android:name="com.pax.permission.PICC" />
<uses-permission android:name="com.pax.permission.PRINTER" />
<uses-permission android:name="br.com.phoebus.android.payments.provider.READ_PERMISSION" />
```

## Usage

```ts
import { DeviceEventEmitter } from 'react-native';
import { Sunmi, Plugpag, Pax, Paystore } from 'react-native-mesmart';

// Register event listener to catch onPlugPagEventData
useEffect(() => {
  const subscribe = DeviceEventEmitter.addListener(
    'onPlugPagEventData',
    (plugpagEvent: PlugPagEventData) => {
      console.log(plugpagEvent);
    }
  );

  return () => {
    subscribe.remove();
  };
}, []);
```

## License

MIT

---
