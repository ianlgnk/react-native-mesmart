import type { PlugpagAPI } from './plugpag';
import type { SunmiAPI } from './sunmi';
import type { PaxAPI } from './pax';
import type { PaystoreAPI } from './paystore';
import type { StoneAPI } from './stone';

declare module 'react-native' {
  interface NativeModulesStatic {
    Plugpag: PlugpagAPI;
    Sunmi: SunmiAPI;
    Pax: PaxAPI;
    Paystore: PaystoreAPI;
    Stone: StoneAPI;
  }
}

export type PaymentMethod =
  | 'debitCard'
  | 'creditCard'
  | 'pix'
  | 'voucher'
  | 'money'
  | 'bonus';

export * from './plugpag';
export * from './paystore';
export * from './sunmi';
export * from './pax';
export * from './stone';
