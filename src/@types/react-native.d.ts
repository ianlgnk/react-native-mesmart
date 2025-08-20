import type { PlugpagAPI } from './plugpag';
import type { SunmiAPI } from './sunmi';
import type { PaxAPI } from './pax';
import type { PaystoreAPI } from './paystore';
import type { StoneAPI } from './stone';
import type { GertecAPI } from './gertec';

declare module 'react-native' {
  interface NativeModulesStatic {
    Plugpag: PlugpagAPI;
    Sunmi: SunmiAPI;
    Pax: PaxAPI;
    Paystore: PaystoreAPI;
    Stone: StoneAPI;
    Gertec: GertecAPI;
  }
}
