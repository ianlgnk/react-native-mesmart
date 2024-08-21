import { NativeModules, Platform } from 'react-native';
import {
  type SunmiAPI,
  type PlugpagAPI,
  type PaxAPI,
  type PlugPagTransactionTypes,
  type PlugPagEventData,
  type PlugPagTransactionResult,
  type PaystoreAPI,
  type PaystorePaymentTypes,
  type PaystorePaymentStatus,
  type PaystoreCaptureType,
  type PaystoreCard,
  type PaystoreReceipts,
  type PaystorePaymentResult,
  PlugPagEvent,
  PlugPagResult,
} from './@types';

export const IOS_ERROR =
  "⚠️ The package 'react-native-mesmart' doesn't work in iOS environment!";

export const LINKING_ERROR =
  `⚠️ The package 'react-native-mesmart' doesn't seem to be linked. Make sure: \n\n` +
  ' - You rebuilt the app after installing the package\n' +
  ' - You are not using Expo Go\n';

if (Platform.OS === 'ios') throw new Error(IOS_ERROR);

const plugpagApi: PlugpagAPI = NativeModules.Plugpag
  ? NativeModules.Plugpag
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const sunmiApi: SunmiAPI = NativeModules.Sunmi
  ? NativeModules.Sunmi
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const paxApi: PaxAPI = NativeModules.Pax
  ? NativeModules.Pax
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const paystoreApi: PaystoreAPI = NativeModules.Paystore
  ? NativeModules.Paystore
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const Sunmi = {
  searchRfid: sunmiApi.searchRfid,
  cancelCardSearch: sunmiApi.cancelCardSearch,
  printText: (
    str: string,
    fontSize: number = 24,
    isBold: boolean = false,
    isUnderline: boolean = false
  ) => sunmiApi.printText(str, fontSize, isBold, isUnderline),
  printEmptyLine: sunmiApi.printEmptyLine,
  printBpLogo: sunmiApi.printBpLogo,
  setPrinterAlign: (align: 'left' | 'center' | 'right') => {
    if (align === 'left') sunmiApi.setPrinterAlign(0);
    if (align === 'center') sunmiApi.setPrinterAlign(1);
    if (align === 'right') sunmiApi.setPrinterAlign(2);
  },
};

const Plugpag = {
  init: plugpagApi.init,
  abortPayment: plugpagApi.abortPayment,
  printEstablishmentReceipt: plugpagApi.printEstablishmentReceipt,
  printClientReceipt: plugpagApi.printClientReceipt,
  customDialogClientViaPrinter: plugpagApi.customDialogClientViaPrinter,
  pay: plugpagApi.pay,
  resolveTransactionEvent: plugpagApi.resolveTransactionEvent,
};

const Pax = {
  searchRfid: paxApi.searchRfid,
  cancelCardSearch: paxApi.cancelCardSearch,
  printText: (str: string, fontSize: number = 24) =>
    paxApi.printText(str, fontSize),
  printBpLogo: paxApi.printBpLogo,
};

const Paystore = {
  pay: paystoreApi.pay,
};

export { Sunmi, Plugpag, Pax, Paystore, PlugPagEvent, PlugPagResult };

export type {
  PlugPagEventData,
  PlugPagTransactionResult,
  PlugPagTransactionTypes,
  PaystorePaymentTypes,
  PaystorePaymentStatus,
  PaystoreCaptureType,
  PaystoreCard,
  PaystoreReceipts,
  PaystorePaymentResult,
};
