export interface SunmiAPI {
  searchRfid: () => Promise<string>;
  cancelCardSearch: () => void;
  printText: (
    str: string,
    fontSize?: number,
    isBold?: boolean,
    isUnderline?: boolean
  ) => void;
  printEmptyLine: () => void;
  printBpLogo: () => void;
  setPrinterAlign: (align: number) => void;
}
