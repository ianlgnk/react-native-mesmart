import type { PaymentMethod } from '.';

export interface StoneAPI {
  init: (keys: StoneKeys, appName: string) => Promise<null>;
  isActivated: () => Promise<boolean>;
  getStoneCode: () => Promise<string>;
  pay: (
    value: string,
    paymentType: PaymentMethod,
    isCapture: boolean
  ) => Promise<null>;
  abortPayment: () => Promise<boolean>;
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
  transaction: StoneTransaction;
};

export type StoneOnErrorEvent = {
  type: 'onError';
  status: string;
  statusEnum: StoneTransactionStatus;
  action: string;
  actionCode: StoneActionCode;
  messageFromAuthorize: string;
  transaction: StoneTransaction;
  listOfErrors: Array<string>;
  listOfErrorsEnum: Array<StoneError>;
};

export type StoneOnStatusChanged = {
  type: 'onStatusChanged';
  action: string;
  actionEvent: StoneActionEvent;
  qrCode?: string;
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

export type StoneError =
  | 'CARD_BLOCKED'
  | 'CONNECTION_NOT_FOUND'
  | 'DEVICE_NOT_COMPATIBLE'
  | 'UNEXPECTED_STATUS_COMMAND'
  | 'GENERIC_ERROR'
  | 'IO_ERROR_WITH_PINPAD'
  | 'PINPAD_CONNECTION_NOT_FOUND'
  | 'TABLES_NOT_FOUND'
  | 'NEED_LOAD_TABLES'
  | 'TIME_OUT'
  | 'OPERATION_CANCELLED_BY_USER'
  | 'CARD_REMOVED_BY_USER'
  | 'CANT_READ_CARD_HOLDER_INFORMATION'
  | 'INVALID_TRANSACTION'
  | 'PASS_TARGE_WITH_CHIP'
  | 'NULL_RESPONSE'
  | 'ERROR_RESPONSE_COMMAND'
  | 'ACCEPTOR_REJECTION'
  | 'EMAIL_MESSAGE_ERROR'
  | 'INVALID_EMAIL_CLIENT'
  | 'EMAIL_EMPTY'
  | 'EMAIL_RECIPIENT_EMPTY'
  | 'INVALID_STONE_CODE_OR_UNKNOWN'
  | 'TRANSACTION_NOT_FOUND'
  | 'INVALID_TRANSACTION_STATUS'
  | 'INVALID_STONECODE'
  | 'USERMODEL_NOT_FOUND'
  | 'NO_PRINT_SUPPORT'
  | 'CANT_READ_CHIP_CARD'
  | 'PINPAD_WITHOUT_KEY'
  | 'PINPAD_WITHOUT_STONE_KEY'
  | 'PINPAD_ALREADY_CONNECTED'
  | 'CONNECTIVITY_ERROR'
  | 'SWIPE_INCORRECT'
  | 'TOO_MANY_CARDS'
  | 'UNKNOWN_ERROR'
  | 'INTERNAL_ERROR'
  | 'EMV_GENERIC_ERROR'
  | 'SWITCH_INTERFACE'
  | 'EMV_FAILED_CARD_CONN_ERROR'
  | 'EMV_NO_APP_ERROR'
  | 'EMV_INITIALIZATION_ERROR'
  | 'EMV_CAPK_ERROR'
  | 'EMV_TLV_ERROR'
  | 'EMV_NO_CAPK_ERROR'
  | 'EMV_AID_ERROR'
  | 'PRINTER_GENERIC_ERROR'
  | 'PRINTER_BUSY_ERROR'
  | 'PRINTER_INIT_ERROR'
  | 'PRINTER_LOW_ENERGY_ERROR'
  | 'PRINTER_OUT_OF_PAPER_ERROR'
  | 'PRINTER_UNSUPPORTED_FORMAT_ERROR'
  | 'PRINTER_INVALID_DATA_ERROR'
  | 'PRINTER_OVERHEATING_ERROR'
  | 'PED_PASS_GENERIC_ERROR'
  | 'PED_PASS_KEY_ERROR'
  | 'PED_PASS_USER_CANCELED_ERROR'
  | 'PED_PASS_NO_PIN_INPUT_ERROR'
  | 'PED_PASS_TIMEOUT_ERROR'
  | 'PED_PASS_INIT_ERROR'
  | 'PED_PASS_CRYPT_ERROR'
  | 'PED_PASS_NO_KEY_FOUND_ERROR'
  | 'TRANS_GENERIC_ERROR'
  | 'TRANS_APP_BLOCKED_ERROR'
  | 'TRANS_SELECT_TYPE_USER_CANCELED_ERROR'
  | 'TRANS_INVALID_AMOUNT_ERROR'
  | 'TRANS_PASS_MAG_BUT_IS_ICC_ERROR'
  | 'TRANS_NO_TRANS_TYPE_ERROR'
  | 'TRANS_WRONG_TRANS_TYPE_ERROR'
  | 'TRANS_APP_INVALID_ERROR'
  | 'TRANS_CVV_NOT_PROVIDED_ERROR'
  | 'TRANS_CVV_INVALID_ERROR'
  | 'TRANS_APP_INVALID_INDEX_ERROR'
  | 'TRANS_ONLINE_PROCESS_ERROR_ERROR'
  | 'CARD_GENERIC_ERROR'
  | 'CARD_READ_ERROR'
  | 'CARD_READ_TIMEOUT_ERROR'
  | 'CARD_READ_CANCELED_ERROR'
  | 'CARD_UNSUPPORTED_ERROR'
  | 'CARD_READ_MULTI_ERROR'
  | 'TRANSACTION_FALLBACK_STARTED'
  | 'TRANSACTION_FALLBACK_TIMEOUT'
  | 'TRANSACTION_FALLBACK_INVALID_CARD_MODE'
  | 'NO_ACTIVE_APPLICATION'
  | 'MULTI_INSTANCES_OF_PROVIDER_RUNNING'
  | 'UNKNOWN_TYPE_OF_USER'
  | 'TRANSACTION_OBJECT_NULL_ERROR'
  | 'INVALID_AMOUNT'
  | 'APPNAME_NOT_SET'
  | 'SDK_VERSION_OUTDATED'
  | 'PINPAD_CLOSED_CONNECTION'
  | 'NOT_STONE_POS_OR_POS_MISCONFIGURED'
  | 'COULD_NOT_ACTIVATE_ALL_STONE_CODES'
  | 'COULD_NOT_ACTIVATE_WITH_ACCEPTOR_CONFIGURATION_UPDATE_DATA_NULL'
  | 'NO_MIFARE_SUPPORT'
  | 'MIFARE_ABORTED'
  | 'MIFARE_DETECT_TIMEOUT'
  | 'MIFARE_WRONG_CARD_TYPE'
  | 'MIFARE_INVALID_KEY'
  | 'MIFARE_NOT_AUTHENTICATED'
  | 'MIFARE_INVALID_SECTOR_NUMBER'
  | 'MIFARE_INVALID_BLOCK_NUMBER'
  | 'MIFARE_INVALID_BLOCK_FORMAT'
  | 'MIFARE_MULTI_CARD_DETECTED'
  | 'DATA_CONTAINER_CONSTRAINT_ERROR'
  | 'DATA_CONTAINER_INTEGRATION_ERROR'
  | 'QRCODE_NOT_GENERATED'
  | 'QRCODE_EXPIRED';
