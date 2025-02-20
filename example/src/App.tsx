import * as React from 'react';
import { useEffect } from 'react';

import {
  Button,
  DeviceEventEmitter,
  StyleSheet,
  Text,
  ToastAndroid,
  View,
} from 'react-native';
import {
  Pax,
  Paystore,
  Plugpag,
  Stone,
  Sunmi,
  type PlugpagEventData,
  type StoneEventData,
  type StoneReceiptType,
  type StoneTransaction,
} from 'react-native-mesmart';

export default function App() {
  const [nativeModule, setNativeModule] = React.useState<
    '' | 'plugpag' | 'stone' | 'paystore' | 'sunmi' | 'pax'
  >('');
  const [isScanning, setIsScanning] = React.useState(false);
  const [isPaying, setIsPaying] = React.useState(false);
  const [plugpagEvent, setPlugpagEvent] = React.useState<PlugpagEventData>();
  const [stoneTransaction, setStoneTransaction] =
    React.useState<StoneTransaction>();

  useEffect(() => {
    const plugpagSubscribe = DeviceEventEmitter.addListener(
      'onPlugpagEventData',
      (event: PlugpagEventData) => {
        console.log(event);
        setPlugpagEvent(event);
      }
    );

    const stoneSubscribe = DeviceEventEmitter.addListener(
      'onStoneEventData',
      (event: StoneEventData) => {
        console.log(event);

        if (event.type === 'onSuccess') {
          setStoneTransaction(event.transaction);
        }
      }
    );

    return () => {
      plugpagSubscribe.remove();
      stoneSubscribe.remove();
    };
  }, []);

  const scanRfid = async (pos: 'sunmi' | 'pax') => {
    try {
      setIsScanning(true);
      let rfid = '';
      if (pos === 'sunmi') rfid = await Sunmi.scanRfid();
      if (pos === 'pax') rfid = await Pax.scanRfid();
      ToastAndroid.showWithGravity(
        `RFID: ${rfid}`,
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
    } catch (e: any) {
      console.error(e);
    } finally {
      setIsScanning(false);
    }
  };

  const cancelScan = async (pos: 'sunmi' | 'pax') => {
    try {
      if (pos === 'sunmi') await Sunmi.cancelScan();
      if (pos === 'pax') await Pax.cancelScan();

      ToastAndroid.showWithGravity(
        `Scan de RFID cancelado na ${pos}`,
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );

      setIsScanning(false);
    } catch (e: any) {
      console.error(e);
    }
  };

  const initPlugpag = async () => {
    try {
      await Plugpag.init('123');
      ToastAndroid.showWithGravity(
        `Plugpag iniciado!`,
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
    } catch (err: any) {
      console.error(err);
    }
  };

  const destroyPlugpag = async () => {
    try {
      await Plugpag.destroy();
      ToastAndroid.showWithGravity(
        `Plugpag destruído!`,
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
    } catch (err: any) {
      console.error(err);
    }
  };

  const payWithPlugpag = async () => {
    try {
      setIsPaying(true);
      const res = await Plugpag.pay('500', 'creditCard');
      console.log(
        `{"errorCode": ${res.errorCode}, "message": ${res.message}, "result": ${res.result}}`
      );
      ToastAndroid.showWithGravity(
        `{"errorCode": ${res.errorCode}, "message": ${res.message}, "result": ${res.result}}`,
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
    } catch (err: any) {
      console.error(err);
    } finally {
      setIsPaying(false);
    }
  };

  const abortPaymentPlugpag = async () => {
    try {
      await Plugpag.abortPayment();
      ToastAndroid.showWithGravity(
        `Pagamento com Plugpag abortado!`,
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      setIsPaying(false);
    } catch (err: any) {
      console.error(err);
    }
  };

  const printClientReceipt = async (pos: 'sunmi' | 'pax') => {
    try {
      const date = 'Dia 22/07/2024 às 11:30:23';
      const rechargeType = 'CARTÃO';
      const name = 'IAN LANGKAMMER BATISTA';
      const document = '155.124.906-56';
      const paymentMethod = 'CRÉDITO';
      const rfid = '9999999999';
      const rechargeValue = 'R$ 1,25';

      if (pos === 'sunmi')
        await Sunmi.printClientReceipt(
          date,
          rechargeType,
          name,
          document,
          paymentMethod,
          rfid,
          rechargeValue
        );
      if (pos === 'pax')
        await Pax.printClientReceipt(
          date,
          rechargeType,
          name,
          document,
          paymentMethod,
          rfid,
          rechargeValue
        );
    } catch (err: any) {
      console.error(err);
    }
  };

  const printQRCode = async () => {
    try {
      await Sunmi.setPrinterAlign('center');
      await Sunmi.printBitmap(
        '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'
      );
      await Sunmi.printEmptyLine();
      await Sunmi.printEmptyLine();
      await Sunmi.printEmptyLine();
    } catch (err: any) {
      console.error(err);
    }
  };

  const initPaystore = async () => {
    try {
      await Paystore.init();
      ToastAndroid.showWithGravity(
        `Paystore iniciado!`,
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
    } catch (err: any) {
      console.error(err);
    }
  };

  const payWithPaystore = async () => {
    setIsPaying(true);

    const subscription = DeviceEventEmitter.addListener(
      'onPayment',
      (paymentEvent) => {
        subscription.remove();
        console.log({ paymentEvent });
      }
    );

    Paystore.pay(
      '5.00',
      new Date().getTime().toString(),
      true,
      ['creditCard', 'debitCard', 'voucher'],
      1
    )
      .catch((error: Error) => console.error(error))
      .finally(() => {
        subscription.remove();
        setIsPaying(false);
      });
  };

  const isPaystoreActivated = async () => {
    try {
      const res = await Paystore.isActivated();
      ToastAndroid.showWithGravity(
        `Paystore ativado? ${res}`,
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
    } catch (err: any) {
      console.error(err);
    }
  };

  const initStone = async () => {
    try {
      await Stone.init(
        {
          providerId: 'providerId',
          authorization: 'authorization',
        },
        'App'
      );
      ToastAndroid.showWithGravity(
        `Stone iniciado!`,
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
    } catch (err: any) {
      console.error(err);
    }
  };

  const isStoneActivated = async () => {
    try {
      const res = await Stone.isActivated();
      ToastAndroid.showWithGravity(
        `Stone ativado? ${res}`,
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
    } catch (err: any) {
      console.error(err);
    }
  };

  const getStoneCode = async () => {
    try {
      ToastAndroid.showWithGravity(
        `Stonecode: ${await Stone.getStoneCode()}`,
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
    } catch (err: any) {
      console.error(err);
    }
  };

  const payWithStone = async () => {
    try {
      setIsPaying(true);
      await Stone.pay('500', 'creditCard', false);
    } catch (err: any) {
      console.error(err);
    } finally {
      setIsPaying(false);
    }
  };

  const abortPaymentStone = async () => {
    try {
      const res = await Stone.abortPayment();
      ToastAndroid.showWithGravity(
        res ? `Pagamento com Stone abortado!` : 'Operação falhou!',
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      setIsPaying(false);
    } catch (err: any) {
      console.error(err);
    }
  };

  const printStoneVia = async (type: StoneReceiptType) => {
    try {
      if (!stoneTransaction)
        return ToastAndroid.showWithGravity(
          `Nenhuma transação encontrada para imprimir!`,
          ToastAndroid.SHORT,
          ToastAndroid.BOTTOM
        );

      await Stone.printVia(stoneTransaction, type);
    } catch (err: any) {
      console.error(err);
    }
  };

  // const printStoneTransaction = (t: StoneTransaction) => {
  //   console.log(`Transaction {`);
  //   Object.keys(t).forEach((key) => {
  //     console.log(` ${key}: ${(t as any)[key]}`);
  //   });
  //   console.log(`}\n`);
  // };

  const captureStoneTransaction = async () => {
    try {
      if (!stoneTransaction)
        return ToastAndroid.showWithGravity(
          `Nenhuma transação encontrada para capturar!`,
          ToastAndroid.SHORT,
          ToastAndroid.BOTTOM
        );

      const res = await Stone.captureTransaction(stoneTransaction);
      console.log(
        `{\n  "responseCode": "${res.responseCode}",\n  "messageFromAuthorize": "${res.messageFromAuthorize}"\n}`
      );
      // printStoneTransaction(res.transaction);
      setStoneTransaction(res.transaction);
    } catch (err: any) {
      console.error(err);
    }
  };

  const cancelStoneTransaction = async () => {
    try {
      if (!stoneTransaction)
        return ToastAndroid.showWithGravity(
          `Nenhuma transação encontrada para cancelar!`,
          ToastAndroid.SHORT,
          ToastAndroid.BOTTOM
        );

      const res = await Stone.cancelTransaction(stoneTransaction);
      console.log(
        `{\n  "action": "${res.action}"\n  "actionCode": "${res.actionCode}",\n  "responseCode": "${res.responseCode}",\n  "messageFromAuthorize": "${res.messageFromAuthorize}"\n}`
      );
      // printStoneTransaction(res.transaction);
      setStoneTransaction(res.transaction);
    } catch (err: any) {
      console.error(err);
    }
  };

  return (
    <View style={styles.container}>
      {nativeModule === '' && (
        <>
          <Button title="Plugpag" onPress={() => setNativeModule('plugpag')} />
          <Button title="Sunmi" onPress={() => setNativeModule('sunmi')} />
          <Button title="Stone" onPress={() => setNativeModule('stone')} />
          <Button
            title="Paystore"
            onPress={() => setNativeModule('paystore')}
          />
          <Button title="Pax" onPress={() => setNativeModule('pax')} />
        </>
      )}
      {nativeModule === 'plugpag' && (
        <>
          <Text style={styles.text}>Plugpag</Text>
          <Button title="Init Plugpag" onPress={initPlugpag} />
          <Button title="Destroy Plugpag" onPress={destroyPlugpag} />
          <Button title="Pay" onPress={payWithPlugpag} />
          <Text style={styles.text}>
            {!plugpagEvent
              ? 'Evento de pagamento'
              : `{"customMessage": ${plugpagEvent.customMessage}, "eventCode": ${plugpagEvent.eventCode}}`}
          </Text>
          <Button title="Abort payment" onPress={abortPaymentPlugpag} />
        </>
      )}
      {nativeModule === 'sunmi' && (
        <>
          <Text style={styles.text}>Sunmi</Text>
          <Button
            title={isScanning ? 'Escanenado RFID...' : 'Scan RFID'}
            onPress={() => scanRfid('sunmi')}
          />
          <Button title="Cancel scan" onPress={() => cancelScan('sunmi')} />
          <Button
            title="Print client receipt"
            onPress={() => printClientReceipt('sunmi')}
          />
          <Button title="Print QRCode" onPress={printQRCode} />
        </>
      )}
      {nativeModule === 'stone' && (
        <>
          <Text style={styles.text}>Stone</Text>
          <Button title="Init Stone SDK" onPress={initStone} />
          <Button title="Is Stone activated?" onPress={isStoneActivated} />
          <Button title="Get Stonecode" onPress={getStoneCode} />
          <Button title="Pay" onPress={payWithStone} />
          <Button title="Abort payment" onPress={abortPaymentStone} />
          <Button
            title="Capture transaction"
            onPress={captureStoneTransaction}
          />
          <Button title="Cancel transaction" onPress={cancelStoneTransaction} />
          <Button
            title="Print merchant via"
            onPress={() => printStoneVia('MERCHANT')}
          />
          <Button
            title="Print client via"
            onPress={() => printStoneVia('CLIENT')}
          />
        </>
      )}
      {nativeModule === 'pax' && (
        <>
          <Text style={styles.text}>Pax</Text>
          <Button
            title={isScanning ? 'Escanenado RFID...' : 'Scan RFID'}
            onPress={() => scanRfid('pax')}
          />
          <Button title="Cancel scan" onPress={() => cancelScan('pax')} />
          <Button
            title="Print client receipt"
            onPress={() => printClientReceipt('pax')}
          />
        </>
      )}
      {nativeModule === 'paystore' && (
        <>
          <Text style={styles.text}>Pax</Text>
          <Button title="Init Paystore" onPress={initPaystore} />
          <Button title="Pay" onPress={payWithPaystore} />
          <Button
            title="Is Paystore activated?"
            onPress={isPaystoreActivated}
          />
        </>
      )}
      {nativeModule !== '' && (
        <Button
          title={'Voltar'}
          onPress={() => {
            if (isPaying)
              return ToastAndroid.showWithGravity(
                `Finalize ou aborte o pagamento antes de voltar!`,
                ToastAndroid.SHORT,
                ToastAndroid.BOTTOM
              );

            setNativeModule('');
            setPlugpagEvent(undefined);
            setStoneTransaction(undefined);
          }}
        />
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 20,
  },
  text: {
    fontSize: 16,
  },
});
