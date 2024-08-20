# react-native-mesmart

Native methods to integrate with Sunmi P2, Pax A910 and Plugpag payment SDK

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
```

Create a new directory at android/app/src/main/jniLibs/armeabi-v7a and add the file 'libDeviceConfig.so' to this new folder

## Permissions

Add those permissions in your AndroidManifest.xml

```
<uses-permission android:name="com.sunmi.perm.CONTACTLESS_CARD" />
<uses-permission android:name="br.com.uol.pagseguro.permission.MANAGE_PAYMENTS" />
<uses-permission android:name="com.pax.permission.PICC" />
<uses-permission android:name="com.pax.permission.PRINTER" />
```

## Usage

```ts
import { Sunmi, Plugpag, Pax } from 'react-native-mesmart';

// Register event listener to catch onPlugPagEventData
useEffect(() => {
  const eventEmitter = new NativeEventEmitter(NativeModules.Plugpag);
  let eventListener = eventEmitter.addListener(
    'onPlugPagEventData',
    (plugpagEvent: PlugPagEventData) => {
      console.log(plugpagEvent);
    }
  );

  return () => {
    eventListener.remove();
  };
}, []);
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
