import type { PaymentMethod } from '.';

export interface PlugpagAPI {
  init: (code: string) => Promise<null>;
  destroy: () => Promise<null>;
  pay: (
    value: string,
    paymentMethod: PaymentMethod
  ) => Promise<PlugpagTransactionResult>;
  abortPayment: () => Promise<null>;
  resolveEvent: (data: PlugpagEventData) => Promise<string>;
  isActivated: () => Promise<boolean>;
}

export interface PlugpagEventData {
  customMessage?: string;
  eventCode: PlugpagEvent;
}

export enum PlugpagEvent {
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

export type PlugpagTransactionResult = {
  message: string | null;
  errorCode: string | null;
  result: PlugpagResult | null;
  transactionCode: string | null;
  transactionId: string | null;
  nsu: string | null;
  hostNsu: string | null;
  autoCode: string | null;
  userReference: string | null;
  appIdentification: string | null;
  typeTransaction: string | null;
  date: string | null;
  time: string | null;
  cardBrand: string | null;
  bin: string | null;
  holder: string | null;
  holderName: string | null;
  extendedHolderName: string | null;
  cardApplication: string | null;
  cardIssuerNationality: string | null;
  label: string | null;
  readerModel: string | null;
  terminalSerialNumber: string | null;
  amount: string | null;
  originalAmount: number | null;
  availableBalance: string | null;
  currentBalance: string | null;
  accumulatedValue: string | null;
  installments: string | null;
  paymentType: number | null;
  buyerName: string | null;
  consumerIdentification: string | null;
  consumerPhoneNumber: string | null;
  pixTxIdCode: string | null;
  preAutoDueDate: string | null;
  preAutoOriginalAmount: string | null;
  partialPayPartiallyAuthorizedAmount: string | null;
  partialPayRemainingAmount: string | null;
  cardHash: string | null;
  transactionReference: string | null;
  availableFieldsUnusedNote: string | null;
  clubePagScreensIds: string | null;
  userRegistered: number | null;
};

export enum PlugpagResult {
  RET_OK = 0,
}
