export type PaymentMethod =
  | 'debitCard'
  | 'creditCard'
  | 'pix'
  | 'voucher'
  | 'money'
  | 'bonus';

export type * from './plugpag';
export type * from './paystore';
export type * from './sunmi';
export type * from './pax';
export type * from './stone';
