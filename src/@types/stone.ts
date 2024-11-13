import type { PaymentMethod } from '.';

export interface StoneAPI {
  init: (keys: StoneKeys, appName: string) => Promise<null>;
  isActivated: () => Promise<boolean>;
  getStoneCode: () => Promise<string>;
  pay: (
    value: string,
    paymentType: PaymentMethod,
    isCapture: boolean
  ) => Promise<StoneTransaction>;
  abortPayment: () => Promise<null>;
  printVia: (
    transaction: StoneTransaction,
    type: StoneReceiptType
  ) => Promise<null>;
  captureTransaction: (
    transaction: StoneTransaction
  ) => Promise<StoneCaptureResponse>;
  cancelTransaction: (
    transaction: StoneTransaction
  ) => Promise<StoneCancelResponse>;
}

export type StoneKeys = {
  providerId: string;
  authorization: string;
};

export type StoneEventData =
  | StoneOnSuccessEvent
  | StoneOnErrorEvent
  | StoneOnStatusChanged;

export type StoneOnSuccessEvent = {
  type: 'onSuccess';
  status: string;
  statusEnum: StoneTransactionStatus;
  action: string;
  actionCode: StoneActionCode;
  messageFromAuthorize: string;
  authorizationCode: string;
};

export type StoneOnErrorEvent = {
  type: 'onError';
  status: string;
  statusEnum: StoneTransactionStatus;
  action: string;
  actionCode: StoneActionCode;
  messageFromAuthorize: string;
  listOfErrors: Array<string>;
  listOfErrorsEnum: Array<string>;
};

export type StoneOnStatusChanged =
  | {
      type: 'onStatusChanged';
      action: string;
      actionEvent: StoneActionEvent;
    }
  | {
      type: 'onStatusChanged';
      action: string;
      actionEvent: 'TRANSACTION_WAITING_QRCODE_SCAN';
      qrCode: string;
    };

export type StoneCaptureResponse = {
  responseCode: StoneResponseCode | '';
  messageFromAuthorize: string;
  transaction: StoneTransaction;
};

export type StoneCancelResponse = {
  action: string;
  actionCode: StoneActionCode | '';
  responseCode: StoneResponseCode | '';
  messageFromAuthorize: string;
  transaction: StoneTransaction;
};

export type StoneTransaction = {
  idFromBase: number;
  amount: string | null;
  // Deprecated
  requestId: string | null;
  emailSent: string | null;
  timeToPassTransaction: string | null;
  initiatorTransactionKey: string;
  acquirerTransactionKey: string | null;
  cardHolderNumber: string | null;
  cardHolderName: string | null;
  date: string;
  time: string;
  aid: string | null;
  arcq: string | null;
  authorizationCode: string | null;
  iccRelatedData: string | null;
  transactionReference: string | null;
  actionCode: StoneActionCode | '' | null;
  commandActionCode: string | null;
  pinpadUsed: string | null;
  saleAffiliationKey: string;
  cne: string | null;
  cvm: string | null;
  balance: string | null;
  serviceCode: string | null;
  subMerchantCategoryCode: string | null;
  entryMode: StoneEntryMode;
  // Deprecated
  cardBrand: StoneCardBrand;
  cardBrandName: string | null;
  cardBrandId: number | null;
  instalmentTransaction: string;
  transactionStatus: StoneTransactionStatus;
  instalmentType: StoneInstalmentType;
  typeOfTransaction: StoneTypeOfTransaction;
  signatureBitmap: string | null;
  cancellationDate: string | null;
  capture: boolean;
  shortName: string | null;
  subMerchantAddress: string | null;
  userModel: StoneUserModel;
  cvv: string | null;
  isFallbackTransaction: boolean;
  subMerchantCity: string | null;
  subMerchantTaxIdentificationNumber: string | null;
  subMerchantRegisteredIdentifier: string | null;
  subMerchantPostalAddress: string | null;
  appLabel: string | null;
  cardExpireDate: string | null;
  cardSequenceNumber: string | null;
  externalId: string | null;
  qrcodeBitmap: string | null;
};

export type StoneUserModel = {
  merchantName: string | null;
  saleAffiliationKey: string | null;
  affiliationKey: string | null;
  merchantPassword: string | null;
  merchantDocumentNumber: string | null;
  stoneCode: string | null;
  currencyCode: string | null;
  merchantAddress: StoneAddress | null;
  merchantUserPhone: StoneUserPhone | null;
  tableVersion: string | null;
};

export type StoneAddress = {
  distric: string | null;
  street: string | null;
  neighborhood: string | null;
  doorNumber: string | null;
  city: string | null;
};

export type StoneUserPhone = {
  merchantAndroidVersion: string | null;
  merchantPhoneModel: string | null;
  merchantPhoneFactory: string | null;
};

export type StoneEntryMode =
  | 'MAGNETIC_STRIPE'
  | 'CHIP_N_PIN'
  | 'CONTACTLESS'
  | 'CONTACTLESS_MAG'
  | 'QRCODE'
  | 'UNKNOWN';

export type StoneReceiptType = 'MERCHANT' | 'CLIENT';

export type StoneCardBrand =
  | 'UNKNOWN'
  | 'VISA'
  | 'MASTERCARD'
  | 'AMEX'
  | 'EN_ROUTE'
  | 'DINERS_CLUB'
  | 'TICKET'
  | 'ELO'
  | 'ALELO'
  | 'VR'
  | 'HIPER'
  | 'SODEXO'
  | 'HIPERCARD'
  | 'COOPER'
  | 'SENFF'
  | 'VERDECARD'
  | 'CABAL';

export type StoneTransactionStatus =
  | 'UNKNOWN'
  | 'APPROVED'
  | 'DECLINED'
  | 'DECLINED_BY_CARD'
  | 'CANCELLED'
  | 'TECHNICAL_ERROR'
  | 'REJECTED'
  | 'WITH_ERROR'
  | 'PENDING_REVERSAL'
  | 'PENDING'
  | 'REVERSED';

export type StoneInstalmentType = 'None' | 'Merchant' | 'Issuer';

export type StoneTypeOfTransaction =
  | 'DEBIT'
  | 'CREDIT'
  | 'VOUCHER'
  | 'INSTANT_PAYMENT'
  | 'PIX';

export type StoneActionCode =
  | '1000'
  | '1001'
  | '1002'
  | '1003'
  | '1004'
  | '1005'
  | '1006'
  | '1007'
  | '1008'
  | '1009'
  | '1010'
  | '1011'
  | '1012'
  | '1014'
  | '1015'
  | '1016'
  | '1017'
  | '1019'
  | '1020'
  | '1021'
  | '1022'
  | '1023'
  | '1024'
  | '1025'
  | '1026'
  | '1027'
  | '1028'
  | '1029'
  | '1030'
  | '1032'
  | '1035'
  | '1036'
  | '1037'
  | '1039'
  | '1041'
  | '1042'
  | '1045'
  | '1047'
  | '1048'
  | '1057'
  | '1060'
  | '1061'
  | '1062'
  | '1063'
  | '1064'
  | '1065'
  | '2000'
  | '2001'
  | '2002'
  | '2003'
  | '2004'
  | '2005'
  | '2006'
  | '2007'
  | '2008'
  | '2009'
  | '2010'
  | '2011'
  | '2012'
  | '9100'
  | '9102'
  | '9103'
  | '9105'
  | '9107'
  | '9108'
  | '9109'
  | '9110'
  | '9111'
  | '9112'
  | '9113'
  | '9114'
  | '9116'
  | '9117'
  | '9118'
  | '9119'
  | '9120'
  | '9121'
  | '9122'
  | '9123'
  | '9124'
  | '9125'
  | '9132'
  | '9133'
  | '9350'
  | '9999'
  | 'UNPR'
  | 'IMSG'
  | 'PARS'
  | 'SECU'
  | 'INTP'
  | 'RCPP'
  | 'DPMG'
  | 'VERS'
  | 'MSGT';

export type StoneResponseCode =
  | 'Declined'
  | 'Approved'
  | 'TechnicalError'
  | 'Default';

export type StoneActionEvent =
  | 'TRANSACTION_WAITING_CARD'
  | 'TRANSACTION_WAITING_SWIPE_CARD'
  | 'TRANSACTION_WAITING_QRCODE_SCAN'
  | 'TRANSACTION_WAITING_PASSWORD'
  | 'TRANSACTION_SENDING'
  | 'TRANSACTION_REMOVE_CARD'
  | 'REVERSING_TRANSACTION_WITH_ERROR'
  | 'TRANSACTION_CARD_REMOVED'
  | 'TRANSACTION_TYPE_SELECTION'
  | 'TRANSACTION_REQUIRES_CARDHOLDER_TO_CHECK_DEVICE'
  | 'SWITCH_INTERFACE';
