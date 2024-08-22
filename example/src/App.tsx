import * as React from 'react';

import {
  StyleSheet,
  View,
  Text,
  Button,
  DeviceEventEmitter,
  ToastAndroid,
} from 'react-native';
import { Sunmi, Paystore } from 'react-native-mesmart';

export default function App() {
  const [rfid, setRfid] = React.useState<string>('');

  const scanRfid = async () => {
    try {
      setRfid('Waiting card...');
      setRfid(await Sunmi.searchRfid());
      // setRfid(await Pax.searchRfid());
    } catch (e) {
      setRfid('Error!');
      console.error(e);
    }
  };

  const cancelScan = () => {
    try {
      Sunmi.cancelCardSearch();
      // Pax.cancelCardSearch();
      setRfid('');
    } catch (e) {
      console.error(e);
    }
  };

  const handlePrintCoupon = () => {
    try {
      Sunmi.setPrinterAlign('center');
      Sunmi.printBpLogo();
      Sunmi.printText('Comprovante de Recarga\n', 28, true, false);
      Sunmi.printText(`Dia 22/07/2024 às 11:30:23\n`);
      Sunmi.setPrinterAlign('left');
      Sunmi.printText('================================\n', 24, true);
      Sunmi.printText('Tipo de recarga: CARTÃO\n');
      Sunmi.printText('Cliente: IAN LANGKAMMER BATISTA\n');
      Sunmi.printText('Documento: 155.124.906-56\n');
      Sunmi.printText('Forma de pagamento: CRÉDITO\n');
      Sunmi.printText('RFID: 9999999999\n');
      Sunmi.printText('Valor: R$ 1,25\n');
      Sunmi.printText('================================\n', 24, true);
      Sunmi.printText('Saiba mais em: www.beerpassclub.com\n', 20, true);
      Sunmi.printEmptyLine();
      Sunmi.printEmptyLine();
      Sunmi.printEmptyLine();
      // Pax.printBpLogo();
      // Pax.printText('     Comprovante de Recarga\n');
      // Pax.printText(`   Dia 22/07/2024 as 11:30:23\n`);
      // Pax.printText('================================\n');
      // Pax.printText('Tipo de recarga: CARTAO\n');
      // Pax.printText('Cliente: IAN LANGKAMMER BATISTA\n');
      // Pax.printText('Documento: 155.124.906-56\n');
      // Pax.printText('Forma de pagamento: CREDITO\n');
      // Pax.printText('RFID: 9999999999\n');
      // Pax.printText('Valor: R$ 1,25\n');
      // Pax.printText('================================\n');
      // Pax.printText('Saiba mais em: www.beerpassclub.com\n\n\n', 20);
    } catch (error) {
      console.error(error);
    }
  };

  const payWithPaystore = () => {
    const subscription = DeviceEventEmitter.addListener(
      'onPayment',
      (paymentEvent) => {
        subscription.remove();
        console.log({ paymentEvent });
      }
    );
    Paystore.pay(
      '20.00',
      new Date().getTime().toString(),
      true,
      ['CREDIT', 'DEBIT', 'VOUCHER'],
      1
    )
      .then(() => {})
      .catch((error: Error) => {
        subscription.remove();
        const message = error.toString();
        ToastAndroid.showWithGravity(
          message,
          ToastAndroid.SHORT,
          ToastAndroid.BOTTOM
        );
      });
  };

  return (
    <View style={styles.container}>
      <Text style={styles.text}>RFID found: '{rfid}'</Text>
      <Button title="Search RFID card" onPress={scanRfid} />
      <Button title="Cancel card search" onPress={cancelScan} />
      <Button title="Use printer" onPress={handlePrintCoupon} />
      <Button title="Pay (Paystore)" onPress={payWithPaystore} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 10,
  },
  text: {
    fontSize: 16,
    marginBottom: 20,
  },
});
