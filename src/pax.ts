import { Pax } from './';

export const printClientReceiptInPax = async (
  date: string,
  rechargeType: string,
  name: string,
  document: string,
  paymentMethod: string,
  rfid: string,
  rechargeValue: string
): Promise<void> => {
  await Pax.printerInit();

  await Pax.addBpLogo();
  await Pax.addText(`     Comprovante de Recarga\n`);
  await Pax.addText(`   ${date}\n`);
  await Pax.addText('================================\n');
  await Pax.addText(`Tipo de recarga: ${rechargeType}\n`);
  await Pax.addText(`Cliente: ${name}\n`);
  await Pax.addText(`Documento: ${document}\n`);
  await Pax.addText(`Forma de pagamento: ${paymentMethod}\n`);
  await Pax.addText(`RFID: ${rfid}\n`);
  await Pax.addText(`Valor: ${rechargeValue}\n`);
  await Pax.addText('================================\n');
  await Pax.addText('Saiba mais em: www.beerpassclub.com\n\n\n', 20);

  await Pax.printerStart();
};
