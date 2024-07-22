# react-native-mesmart

Sunmi RFID reader and Plugpag wrapper for React Native

## Installation

```sh
npm install react-native-mesmart
```

OR

```sh
yarn add react-native-mesmart
```

Now, you just need to add that code line below in your build.gradle in your react native application

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

## Usage

```ts
import {
  readRfidCardInSunmi,
  cancelRfidCardSearchInSunmi,
  initPlugpag,
  destroyPlugpag,
  abortPayment,
  printEstablishmentReceipt,
  printClientReceipt,
  customDialogClientViaPrinter,
  pay,
  resolveTransactionEvent,
} from 'react-native-mesmart';

// ...

// Read rfid asynchronously
const rfid: string = await readRfidCardInSunmi();

// Cancel rfid read
cancelRfidCardSearchInSunmi();

// Init PlugPag wrapper
const activationCode: string = '9999999'
initPlugpag(activationCode)

// Destroy PlugPag instance
destroyPlugpag()

// Abort payment process
abortPayment()

// Print establishment receipt
printEstablishmentReceipt()

// Print client receipt
printClientReceipt()

// Custom client modal to print receipt
const color: string = "#000"
customDialogClientViaPrinter(color)

// Make transaction process with PlugPag wrapper
const value: string = "2000"                          // In cents, 2000 means R$ 20,00
const paymentMethod: TransactionTypes = "creditCard"  // TransactionTypes = 'debitCard' | 'creditCard' | 'pix' | 'voucher'
const result = await pay(value, paymentMethod)

// Return a message based on a PlugPagEvent
const plugpagEvent: PlugPagEventData = {
  customMessage: 'customMessage'
  eventCode: 0                                        // PlugPagEvent
}
const message: string = resolveTransactionEvent(plugpagEvent)

// Register event listener to catch onPlugPagEventData
useEffect(() => {
  const eventEmitter = new NativeEventEmitter(NativeModules.Plugpag);
  let eventListener = eventEmitter.addListener(
    'onPlugPagEventData',
    (plugpagEvent: PlugPagEventData) => { console.log(plugpagEvent) },
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
