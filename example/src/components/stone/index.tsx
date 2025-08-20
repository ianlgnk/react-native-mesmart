import {
  DeviceEventEmitter,
  StyleSheet,
  Text,
  ToastAndroid,
} from 'react-native';
import { OptionsList } from '../optionsList';
import React, { useEffect } from 'react';
import {
  Stone,
  type StoneEventData,
  type StoneReceiptType,
  type StoneTransaction,
} from 'react-native-mesmart';

type Props = {};

export const StoneExample: React.FC<Props> = ({}: Props) => {
  const [stoneEvent, setStoneEvent] = React.useState<StoneEventData>();
  const [stoneTransaction, setStoneTransaction] =
    React.useState<StoneTransaction>();
  const [SDKActivated, setSDKActivated] = React.useState(false);
  const [stoneCode, setStoneCode] = React.useState('');

  useEffect(() => {
    isActivated(false).then((res) => {
      if (res) getStoneCode(false);
    });

    const stoneSubscribe = DeviceEventEmitter.addListener(
      'onStoneEventData',
      (event: StoneEventData) => {
        console.log(JSON.stringify(event, null, 2));
        setStoneEvent(event);

        if (event.type === 'onSuccess') {
          setStoneTransaction(event.transaction);
        }
      }
    );

    return () => {
      stoneSubscribe.remove();
    };
  }, []);

  const init = async () => {
    try {
      const res = await Stone.init(
        {
          providerId: 'providerId',
          authorization: 'authorization',
        },
        'App'
      );
      ToastAndroid.showWithGravity(
        'Função init() executada com sucesso.',
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      setSDKActivated(true);
      console.log(`init () => ${res}`);
      getStoneCode(false);
    } catch (err: any) {
      console.error(`init () catch => ${err}`);
    }
  };

  const isActivated = async (useToast = true) => {
    try {
      const res = await Stone.isActivated();
      setSDKActivated(res);
      if (useToast)
        ToastAndroid.showWithGravity(
          'Função isActivated() executada com sucesso.',
          ToastAndroid.SHORT,
          ToastAndroid.BOTTOM
        );
      console.log(`isActivated () => ${res}`);
      return res;
    } catch (err: any) {
      console.error(`isActivated () catch => ${err}`);
      return false;
    }
  };

  const getStoneCode = async (useToast = true) => {
    try {
      const res = await Stone.getStoneCode();
      if (useToast)
        ToastAndroid.showWithGravity(
          'Função getStoneCode() executada com sucesso.',
          ToastAndroid.SHORT,
          ToastAndroid.BOTTOM
        );
      setStoneCode(res || '?');
      console.log(`getStoneCode () => ${res}`);
    } catch (err: any) {
      console.error(`getStoneCode () catch => ${err}`);
    }
  };

  const pay = async () => {
    try {
      const res = await Stone.pay('500', 'creditCard', false);
      ToastAndroid.showWithGravity(
        'Função pay() executada com sucesso.',
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      console.log(`pay () => ${JSON.stringify(res, null, 2)}`);
    } catch (err: any) {
      console.error(`pay () catch => ${err}`);
    }
  };

  const abortPayment = async () => {
    try {
      const res = await Stone.abortPayment();
      ToastAndroid.showWithGravity(
        'Função abortPayment() executada com sucesso.',
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      console.log(`abortPayment () => ${res}`);
    } catch (err: any) {
      console.error(`abortPayment () catch => ${err}`);
    }
  };

  const printVia = async (type: StoneReceiptType) => {
    try {
      if (!stoneTransaction)
        return ToastAndroid.showWithGravity(
          `Nenhuma transação encontrada para imprimir!`,
          ToastAndroid.SHORT,
          ToastAndroid.BOTTOM
        );

      const res = await Stone.printVia(stoneTransaction, type);
      ToastAndroid.showWithGravity(
        'Função printVia() executada com sucesso.',
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      console.log(`printVia () => ${res}`);
    } catch (err: any) {
      console.error(`printVia () catch => ${err}`);
    }
  };

  // const printStoneTransaction = (t: StoneTransaction) => {
  //   console.log(`Transaction {`);
  //   Object.keys(t).forEach((key) => {
  //     console.log(` ${key}: ${(t as any)[key]}`);
  //   });
  //   console.log(`}\n`);
  // };

  const captureTransaction = async () => {
    try {
      if (!stoneTransaction)
        return ToastAndroid.showWithGravity(
          `Nenhuma transação encontrada para capturar!`,
          ToastAndroid.SHORT,
          ToastAndroid.BOTTOM
        );

      const res = await Stone.captureTransaction(stoneTransaction);
      ToastAndroid.showWithGravity(
        'Função captureTransaction() executada com sucesso.',
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      // printStoneTransaction(res.transaction);
      setStoneTransaction(res.transaction);
      console.log(`captureTransaction () => ${JSON.stringify(res, null, 2)}`);
    } catch (err: any) {
      console.error(`captureTransaction () catch => ${err}`);
    }
  };

  const cancelTransaction = async () => {
    try {
      if (!stoneTransaction)
        return ToastAndroid.showWithGravity(
          `Nenhuma transação encontrada para cancelar!`,
          ToastAndroid.SHORT,
          ToastAndroid.BOTTOM
        );

      const res = await Stone.cancelTransaction(stoneTransaction);
      ToastAndroid.showWithGravity(
        'Função cancelTransaction() executada com sucesso.',
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      // printStoneTransaction(res.transaction);
      setStoneTransaction(res.transaction);
      console.log(`cancelTransaction () => ${JSON.stringify(res, null, 2)}`);
    } catch (err: any) {
      console.error(`cancelTransaction () catch => ${err}`);
    }
  };

  return (
    <OptionsList
      options={[
        { text: 'init', action: init },
        { text: 'is activated', action: isActivated },
        { text: 'pay', action: pay },
        { text: 'abort payment', action: abortPayment },
        { text: 'capture transaction', action: captureTransaction },
        { text: 'cancel transaction', action: cancelTransaction },
        { text: 'print via merchant', action: () => printVia('MERCHANT') },
        { text: 'print via client', action: () => printVia('CLIENT') },
        { text: 'get stone code', action: getStoneCode },
      ]}
      subtitle={
        <Text
          style={styles.subtitle}
        >{`SDK Ativado?: ${SDKActivated}\nStone Code: ${stoneCode || '?'}\nEvento do SDK:\n${JSON.stringify({ type: stoneEvent?.type, actionEvent: (stoneEvent as any)?.actionEvent, action: stoneEvent?.action }, null, 2)}`}</Text>
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
