import { NativeModules, Platform } from 'react-native';
import {
  type SunmiAPI,
  type PlugpagAPI,
  type PaxAPI,
  type TransactionTypes,
  type PlugPagEventData,
  type PlugPagTransactionResult,
  PlugPagEvent,
  PlugPagResult,
} from './@types';
import { IOS_ERROR, LINKING_ERROR } from './errorMessages';

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

const Sunmi = {
  searchRfid: () => sunmiApi.searchRfid(),
  cancelCardSearch: () => sunmiApi.cancelCardSearch(),
  printText: (
    str: string,
    fontSize: number = 24,
    isBold: boolean = false,
    isUnderline: boolean = false
  ) => sunmiApi.printText(str, fontSize, isBold, isUnderline),
  printEmptyLine: () => sunmiApi.printEmptyLine(),
  printBpLogo: () => sunmiApi.printBpLogo(),
  setPrinterAlign: (align: 'left' | 'center' | 'right') => {
    if (align === 'left') sunmiApi.setPrinterAlign(0);
    if (align === 'center') sunmiApi.setPrinterAlign(1);
    if (align === 'right') sunmiApi.setPrinterAlign(2);
  },
};

const Plugpag = {
  init: (activationCode: string) => plugpagApi.init(activationCode),
  abortPayment: () => plugpagApi.abortPayment(),
  printEstablishmentReceipt: () => plugpagApi.printEstablishmentReceipt(),
  printClientReceipt: () => plugpagApi.printClientReceipt(),
  customDialogClientViaPrinter: (color: string) =>
    plugpagApi.customDialogClientViaPrinter(color),
  pay: (rechargeValue: string, transactionType: TransactionTypes) =>
    plugpagApi.pay(rechargeValue, transactionType),
  resolveTransactionEvent: (data: PlugPagEventData): string =>
    plugpagApi.resolveTransactionEvent(data),
};

const Pax = {
  searchRfid: () => paxApi.searchRfid(),
  cancelCardSearch: () => paxApi.cancelCardSearch(),
  printText: (str: string, fontSize: number = 24) =>
    paxApi.printText(str, fontSize),
  printBpLogo: () => paxApi.printBpLogo(),
};

export { Sunmi, Plugpag, Pax, PlugPagEvent, PlugPagResult };

export type { PlugPagEventData, PlugPagTransactionResult, TransactionTypes };
