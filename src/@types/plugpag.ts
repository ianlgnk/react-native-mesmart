export interface PlugpagAPI {
  init: (activationCode: string) => void;
  pay: (
    rechargeValue: string,
    transactionType: TransactionTypes
  ) => Promise<PlugPagTransactionResult>;
  abortPayment: () => void;
  printEstablishmentReceipt: () => void;
  printClientReceipt: () => void;
  customDialogClientViaPrinter: (color: string) => void;
  resolveTransactionEvent: (data: PlugPagEventData) => string;
}

export type TransactionTypes =
  | 'debitCard'
  | 'creditCard'
  | 'pix'
  | 'voucher'
  | 'money'
  | 'bonus';

export interface PlugPagEventData {
  customMessage?: string;
  eventCode: PlugPagEvent;
}

export enum PlugPagEvent {
  EVENT_CODE_CUSTOM_MESSAGE = -2,
  EVENT_CODE_DEFAULT = -1,
  EVENT_CODE_WAITING_CARD = 0,
  EVENT_CODE_INSERTED_CARD = 1,
  EVENT_CODE_PIN_REQUESTED = 2,
  EVENT_CODE_PIN_OK = 3,
  EVENT_CODE_SALE_END = 4,
  EVENT_CODE_AUTHORIZING = 5,
  EVENT_CODE_INSERTED_KEY = 6,
  EVENT_CODE_WAITING_REMOVE_CARD = 7,
  EVENT_CODE_REMOVED_CARD = 8,
  EVENT_CODE_CVV_REQUESTED = 9,
  EVENT_CODE_CVV_OK = 10,
  EVENT_CODE_CAR_BIN_REQUESTED = 11,
  EVENT_CODE_CAR_BIN_OK = 12,
  EVENT_CODE_CAR_HOLDER_REQUESTED = 13,
  EVENT_CODE_CAR_HOLDER_OK = 14,
  EVENT_CODE_ACTIVATION_SUCCESS = 15,
  EVENT_CODE_DIGIT_PASSWORD = 16,
  EVENT_CODE_NO_PASSWORD = 17,
  EVENT_CODE_SALE_APPROVED = 18,
  EVENT_CODE_SALE_NOT_APPROVED = 19,
}

export interface PlugPagTransactionResult {
  message?: string;
  errorCode?: string;
  result?: PlugPagResult;
}

export enum PlugPagResult {
  RET_OK = 0,
}
