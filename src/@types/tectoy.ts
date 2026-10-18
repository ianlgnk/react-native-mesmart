import type { SunmiAPI } from './sunmi';

export interface TectoyAPI extends Omit<SunmiAPI, 'cancelScan'> {
  cancelScan: () => Promise<true>;
  /** Imprime tudo que foi acumulado desde o ultimo commit num job so. */
  commitPrint: () => Promise<true>;
}
