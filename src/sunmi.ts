import { Sunmi } from './';

export const printClientReceiptInSunmi = async (
  date: string,
  rechargeType: string,
  name: string,
  document: string,
  paymentMethod: string,
  rfid: string,
  rechargeValue: string
): Promise<void> => {
  await Sunmi.setPrinterAlign('center');
  await Sunmi.printBpLogo();
  await Sunmi.printText('Comprovante de Recarga\n', 28, true, false);
  await Sunmi.printText(`${date}\n`);
  await Sunmi.setPrinterAlign('left');
  await Sunmi.printText('================================\n', 24, true);
  await Sunmi.printText(`Tipo de recarga: ${rechargeType}\n`);
  await Sunmi.printText(`Cliente: ${name.toUpperCase()}\n`);
  await Sunmi.printText(`Documento: ${document}\n`);
  await Sunmi.printText(`Forma de pagamento: ${paymentMethod}\n`);
  await Sunmi.printText(`RFID: ${rfid}\n`);
  await Sunmi.printText(`Valor: ${rechargeValue}\n`);
  await Sunmi.printText('================================\n', 24, true);
  await Sunmi.printText('Saiba mais em: www.beerpassclub.com\n', 20, true);
  await Sunmi.printEmptyLine();
  await Sunmi.printEmptyLine();
  await Sunmi.printEmptyLine();
};
