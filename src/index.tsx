import { NativeModules, Platform } from 'react-native';
import {
  type SunmiAPI,
  type PlugpagAPI,
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

const searchRfid = () => sunmiApi.searchRfid();
const cancelCardSearch = () => sunmiApi.cancelCardSearch();
const printText = (
  str: string,
  fontSize: number = 24,
  isBold: boolean = false,
  isUnderline: boolean = false
) => sunmiApi.printText(str, fontSize, isBold, isUnderline);
const printEmptyLine = () => sunmiApi.printEmptyLine();
const printBpLogo = () => sunmiApi.printBpLogo();
const setPrinterAlign = (align: 'left' | 'center' | 'right') => {
  if (align === 'left') sunmiApi.setPrinterAlign(0);
  if (align === 'center') sunmiApi.setPrinterAlign(1);
  if (align === 'right') sunmiApi.setPrinterAlign(2);
};

const init = (activationCode: string) => plugpagApi.init(activationCode);
const pay = (rechargeValue: string, transactionType: TransactionTypes) =>
  plugpagApi.pay(rechargeValue, transactionType);
const abortPayment = () => plugpagApi.abortPayment();
const printEstablishmentReceipt = () => plugpagApi.printEstablishmentReceipt();
const printClientReceipt = () => plugpagApi.printClientReceipt();
const customDialogClientViaPrinter = (color: string) =>
  plugpagApi.customDialogClientViaPrinter(color);
const resolveTransactionEvent = (data: PlugPagEventData): string =>
  plugpagApi.resolveTransactionEvent(data);

const Sunmi = {
  searchRfid,
  cancelCardSearch,
  printText,
  printEmptyLine,
  printBpLogo,
  setPrinterAlign,
};

const Plugpag = {
  init,
  abortPayment,
  printEstablishmentReceipt,
  printClientReceipt,
  customDialogClientViaPrinter,
  pay,
  resolveTransactionEvent,
};

export { Sunmi, Plugpag, PlugPagEvent, PlugPagResult };

export type { PlugPagEventData, PlugPagTransactionResult, TransactionTypes };
