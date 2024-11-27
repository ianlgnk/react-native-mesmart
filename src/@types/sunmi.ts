export interface SunmiAPI {
  scanRfid: () => Promise<string>;
  cancelScan: () => Promise<null>;
  printText: (
    str: string,
    fontSize: number,
    isBold: boolean,
    isUnderline: boolean
  ) => Promise<null>;
  printEmptyLine: () => Promise<null>;
  printBitmap: (
    bitmapStr: string,
    width: number,
    height: number
  ) => Promise<null>;
  printBpLogo: () => Promise<null>;
  setPrinterAlign: (align: 'left' | 'center' | 'right') => Promise<null>;
}
