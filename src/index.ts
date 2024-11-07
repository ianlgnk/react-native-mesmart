import { NativeModules, Platform } from 'react-native';
import { printClientReceiptInSunmi } from './sunmi';
import { printClientReceiptInPax } from './pax';
import { payWithPaystore } from './paystore';

export const IOS_ERROR =
  "⚠️ The package 'react-native-mesmart' doesn't work in iOS environment!";

if (Platform.OS === 'ios') throw new Error(IOS_ERROR);

const Plugpag = {
  init: NativeModules.Plugpag.init,
  destroy: NativeModules.Plugpag.destroy,
  pay: NativeModules.Plugpag.pay,
  abortPayment: NativeModules.Plugpag.abortPayment,
  resolveEvent: NativeModules.Plugpag.resolveEvent,
  isActivated: NativeModules.Plugpag.isActivated,
};
const Sunmi = {
  scanRfid: NativeModules.Sunmi.scanRfid,
  cancelScan: NativeModules.Sunmi.cancelScan,
  printClientReceipt: printClientReceiptInSunmi,
  printText: (
    str: string,
    fontSize: number = 24,
    isBold: boolean = false,
    isUnderline: boolean = false
  ) => NativeModules.Sunmi.printText(str, fontSize, isBold, isUnderline),
  printEmptyLine: NativeModules.Sunmi.printEmptyLine,
  printBpLogo: NativeModules.Sunmi.printBpLogo,
  setPrinterAlign: NativeModules.Sunmi.setPrinterAlign,
};
const Pax = {
  scanRfid: NativeModules.Pax.scanRfid,
  cancelScan: NativeModules.Pax.cancelScan,
  printClientReceipt: printClientReceiptInPax,
  printerInit: NativeModules.Pax.printerInit,
  printerStart: NativeModules.Pax.printerStart,
  addText: (str: string, fontSize: number = 24) =>
    NativeModules.Pax.addText(str, fontSize),
  addBpLogo: NativeModules.Pax.addBpLogo,
};
const Paystore = {
  init: NativeModules.Paystore.init,
  pay: payWithPaystore,
  isActivated: NativeModules.Paystore.isActivated,
};
const Stone = {
  init: NativeModules.Stone.init,
  isActivated: NativeModules.Stone.isActivated,
  getStoneCode: NativeModules.Stone.getStoneCode,
  pay: NativeModules.Stone.pay,
  abortPayment: NativeModules.Stone.abortPayment,
  printVia: NativeModules.Stone.printVia,
  captureTransaction: NativeModules.Stone.captureTransaction,
  cancelTransaction: NativeModules.Stone.cancelTransaction,
};

export { Plugpag, Sunmi, Pax, Paystore, Stone };
export type * from './@types/index.d.ts';
