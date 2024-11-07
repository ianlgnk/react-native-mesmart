export interface PaxAPI {
  scanRfid: () => Promise<string>;
  cancelScan: () => Promise<null>;
  printerInit: () => Promise<null>;
  printerStart: () => Promise<null>;
  addText: (str: string, fontSize: number) => Promise<null>;
  addBpLogo: () => Promise<null>;
}
