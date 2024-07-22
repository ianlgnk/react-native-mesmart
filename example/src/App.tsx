import * as React from 'react';

import { StyleSheet, View, Text, Button } from 'react-native';
import { Sunmi } from 'react-native-mesmart';

export default function App() {
  const [rfid, setRfid] = React.useState<string>('');

  const scanRfid = async () => {
    try {
      setRfid('Waiting card...');
      setRfid(await Sunmi.searchRfid());
    } catch (e) {
      setRfid('Error!');
      console.error(e);
    }
  };

  const cancelScan = () => {
    try {
      Sunmi.cancelCardSearch();
      setRfid('');
    } catch (e) {
      console.error(e);
    }
  };

  const handlePrintText = () => {
    try {
      Sunmi.printText(`TESTE`, 24, false, false);
      Sunmi.printText(`teste`, 24, true, false);
      Sunmi.printText(`TESTE`, 24, true, true);
      Sunmi.printText(`teste`, 24, false, true);
      Sunmi.printEmptyLine();
      Sunmi.printEmptyLine();
      Sunmi.printEmptyLine();
      Sunmi.printEmptyLine();
    } catch (error) {
      console.error(error);
    }
  };

  const handlePrintBpLogo = () => {
    Sunmi.printBpLogo();
  };

  const handlePrintCoupon = () => {
    Sunmi.setPrinterAlign('center');
    Sunmi.printBpLogo();
    Sunmi.printText('Cupom Fiscal\n', 28, true, false);
    Sunmi.printText(`Dia 22/07/2024 às 11:30:23\n`);
    Sunmi.setPrinterAlign('left');
    Sunmi.printText('================================\n', 24, true);
    Sunmi.printText('Tipo de recarga: Cartão\n');
    Sunmi.printText('Cliente: IAN LANGKAMMER BATISTA\n');
    Sunmi.printText('Documento: 155.124.906-56\n');
    Sunmi.printText('Forma de pagamento: CRÉDITO\n');
    Sunmi.printText('RFID: 9999999999\n');
    Sunmi.printText('================================\n', 24, true);
    Sunmi.printText('Saiba mais em: www.beerpassclub.com\n', 20, true);
    Sunmi.printEmptyLine();
    Sunmi.printEmptyLine();
    Sunmi.printEmptyLine();
  };

  return (
    <View style={styles.container}>
      <Text style={styles.text}>RFID found: '{rfid}'</Text>
      <Button title="Search RFID" onPress={scanRfid} />
      <Button title="Cancel card search" onPress={cancelScan} />
      <Button title="Print text" onPress={handlePrintText} />
      <Button title="Printar BP logo" onPress={handlePrintBpLogo} />
      <Button title="Printar cupom fiscal" onPress={handlePrintCoupon} />
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
