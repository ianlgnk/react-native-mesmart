import React from 'react';
import { StyleSheet, Text, ToastAndroid } from 'react-native';
import { Tectoy } from 'react-native-mesmart';

import { OptionsList } from '../optionsList';
import { QR_CODE_BASE64 } from './qrcode';

type Props = {};

export const TectoyExample: React.FC<Props> = ({}: Props) => {
  const [isScanning, setIsScanning] = React.useState(false);
  const [rfidReaded, setRfidReaded] = React.useState('');

  const scanRfid = async () => {
    try {
      setIsScanning(true);
      setRfidReaded('');
      const res = await Tectoy.scanRfid();
      setRfidReaded(res);
      console.log(`scanRfid() => ${res}`);
    } catch (e: any) {
      console.error(`scanRfid() catch => ${e}`);
    } finally {
      setIsScanning(false);
    }
  };

  const cancelScan = async () => {
    try {
      const res = await Tectoy.cancelScan();
      setIsScanning(false);
      console.log(`cancelScan() => ${res}`);
    } catch (e: any) {
      console.error(`cancelScan() catch => ${e}`);
    }
  };

  // Mesmo layout do printConsumptionCouponInSunmi do modulo-externo.
  const printConsumptionCoupon = async () => {
    try {
      await Tectoy.setPrinterAlign('center');
      await Tectoy.printBpLogo();
      await Tectoy.printText('Cupom de Consumo\n', 28, true, false);
      await Tectoy.printText('Dia 20/08/2025 às 13:30:00\n');
      await Tectoy.setPrinterAlign('left');
      await Tectoy.printText('================================\n', 24, true);
      await Tectoy.printText('Estabelecimento: Bar do Teste\n');
      await Tectoy.printText('Tap: Amstel - 03\n');
      await Tectoy.printText('Pagamento: PIX\n');
      await Tectoy.printText('Valor do Cupom: R$ 18,00\n', 28, true);
      await Tectoy.printText('Cupom: 35641247\n', 28, true);
      await Tectoy.setPrinterAlign('center');
      await Tectoy.printText(
        'Use no próximo autosserviço para consumir o saldo restante ou ganhar desconto em um copo.\n',
        20,
        false
      );
      await Tectoy.setPrinterAlign('left');
      await Tectoy.printText('================================\n', 24, true);
      await Tectoy.setPrinterAlign('center');
      await Tectoy.printText('Baixe nosso App!\n', 28, true, false);
      await Tectoy.printText('\n', 20);
      await Tectoy.printBitmap(QR_CODE_BASE64);
      await Tectoy.printText('Beerpass • www.beerpassclub.com\n', 20, true);
      await Tectoy.printEmptyLine();
      await Tectoy.printEmptyLine();
      await Tectoy.printEmptyLine();
      const res = await Tectoy.commitPrint();
      ToastAndroid.show('Cupom de consumo impresso.', ToastAndroid.SHORT);
      console.log(`printConsumptionCoupon() => ${res}`);
    } catch (e: any) {
      console.error(`printConsumptionCoupon() catch => ${e}`);
    }
  };

  // Mesmo layout do printClientReceiptInSunmi do modulo-externo.
  const printClientReceipt = async () => {
    try {
      await Tectoy.setPrinterAlign('center');
      await Tectoy.printBpLogo();
      await Tectoy.printText('Comprovante de Recarga\n', 28, true, false);
      await Tectoy.printText('Dia 20/08/2025 às 13:30:00\n');
      await Tectoy.setPrinterAlign('left');
      await Tectoy.printText('================================\n', 24, true);
      await Tectoy.printText('Cliente: FULANO DE TAL\n');
      await Tectoy.printText('Documento: 000.000.000-00\n');
      await Tectoy.printText('Tipo: APP\n');
      await Tectoy.printText('Pagamento: DÉBITO\n');
      await Tectoy.printText('RFID: 1839402837\n');
      await Tectoy.printText('Valor: R$ 50,00\n');
      await Tectoy.printText('================================\n', 24, true);
      await Tectoy.setPrinterAlign('center');
      await Tectoy.printText('Beerpass • www.beerpassclub.com\n', 20, true);
      await Tectoy.printEmptyLine();
      await Tectoy.printEmptyLine();
      await Tectoy.printEmptyLine();
      const res = await Tectoy.commitPrint();
      ToastAndroid.show('Via de recarga impressa.', ToastAndroid.SHORT);
      console.log(`printClientReceipt() => ${res}`);
    } catch (e: any) {
      console.error(`printClientReceipt() catch => ${e}`);
    }
  };

  return (
    <OptionsList
      options={[
        { text: 'scan rfid', action: scanRfid },
        { text: 'cancel scan', action: cancelScan },
        { text: 'print consumption coupon', action: printConsumptionCoupon },
        { text: 'print recharge via', action: printClientReceipt },
      ]}
      subtitle={
        <Text
          style={styles.subtitle}
        >{`Status da antena: ${!isScanning ? 'Ocioso' : 'Lendo cartão...'}\nRFID: ${rfidReaded || '?'}`}</Text>
      }
    />
  );
};

const styles = StyleSheet.create({
  subtitle: {
    fontSize: 16,
    color: 'black',
    textAlign: 'center',
  },
});
