export interface GertecAPI {
  scanRfid: () => Promise<string>;
  cancelScan: () => Promise<true>;
  scrollPrinterPaper: (lines: number) => Promise<true>;
  printHtml: (html: string) => Promise<true>;
}
