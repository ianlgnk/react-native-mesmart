export interface PaxAPI {
  searchRfid: () => Promise<string>;
  cancelCardSearch: () => void;
  printText: (str: string, fontSize?: number) => void;
  printBpLogo: () => void;
}
