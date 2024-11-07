export type PaystorePaymentTypes =
  | 'CREDIT'
  | 'DEBIT'
  | 'CREDIT_STORE'
  | 'ADMIN'
  | 'CREDIT_ADMIN'
  | 'VOUCHER'
  | 'DEBIT'
  | 'PRE_AUTHORIZATION'
  | 'PRE_AUTHORIZATION_CONFIRMATION';

export type PaystorePaymentStatus =
  | 'PENDING'
  | 'CONFIRMED'
  | 'CANCELLED'
  | 'REVERSED'
  | 'PROCESSING'
  | 'DENIED'
  | 'UNREACHABLE'
  | 'WAITING_VALIDATION'
  | 'WAITING_CAPTURE'
  | 'REFUNDED_DEVOLUTION'
  | 'REFUNDED'
  | 'APPROVED';

export type PaystoreCaptureType =
  | 'MANUAL'
  | 'MAGNETIC_STRIP'
  | 'CHIP'
  | 'FALLBACK_MANUAL'
  | 'FALLBACK_MAGNETIC_STRIP'
  | 'CHIP_CONTACTLESS'
  | 'MAGNETIC_STRIP_CONTACTLESS'
  | 'QR_CODE';

export type PaystoreCard = {
  bin: string;
  brand: string;
  panLast4Digits: string;
};

export type PaystoreReceipts = {
  clientVia: string;
  merchantVia: string;
};

export interface PaystorePaymentResult {
  acquirer: string;
  acquirerAuthorizationNumber: string;
  acquirerId: string;
  acquirerNsu: string;
  acquirerResponseCode: string;
  acquirerResponseDate: string;
  captureType: PaystoreCaptureType;
  card: PaystoreCard;
  installments: number;
  paymentDate: string;
  paymentId: string;
  paymentStatus: PaystorePaymentStatus;
  appTransactionId: string;
  paymentType: PaystorePaymentTypes;
  receipt: PaystoreReceipts;
  value: number;
}

export interface PaystoreAPI {
  init: () => Promise<null>;
  pay: (
    value: string,
    transactionId: string,
    showReceipt: boolean,
    paymentType: Array<'CREDIT' | 'DEBIT' | 'VOUCHER'>,
    installments: number
  ) => Promise<null>;
  isActivated: () => Promise<boolean>;
}
