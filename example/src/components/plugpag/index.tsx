import React, { useEffect } from 'react';
import {
  DeviceEventEmitter,
  StyleSheet,
  Text,
  ToastAndroid,
} from 'react-native';
import { Plugpag, type PlugpagEventData } from 'react-native-mesmart';

import { OptionsList } from '../optionsList';

type Props = {};

export const PlugpagExample: React.FC<Props> = ({}: Props) => {
  const [plugpagEvent, setPlugpagEvent] = React.useState<PlugpagEventData>();
  const [SDKActivated, setSDKActivated] = React.useState(false);

  useEffect(() => {
    isActivated(false);

    const plugpagSubscribe = DeviceEventEmitter.addListener(
      'onPlugpagEventData',
      (event: PlugpagEventData) => {
        console.log(JSON.stringify(event, null, 2));
        setPlugpagEvent(event);
      }
    );

    return () => {
      plugpagSubscribe.remove();
    };
  }, []);

  const init = async () => {
    try {
      const res = await Plugpag.init('749879');
      ToastAndroid.showWithGravity(
        'Função init() executada com sucesso.',
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      setSDKActivated(true);
      console.log(`init () => ${res}`);
    } catch (err: any) {
      console.error(`init () catch => ${err}`);
    }
  };

  const destroy = async () => {
    try {
      const res = await Plugpag.destroy();
      ToastAndroid.showWithGravity(
        'Função destroy() executada com sucesso.',
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      setSDKActivated(false);
      console.log(`destroy () => ${res}`);
    } catch (err: any) {
      console.error(`destroy () catch => ${err}`);
    }
  };

  const pay = async () => {
    try {
      const res = await Plugpag.pay('500', 'creditCard');
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
      const res = await Plugpag.abortPayment();
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

  const isActivated = async (useToast = true) => {
    try {
      const res = await Plugpag.isActivated();
      setSDKActivated(res);
      if (useToast)
        ToastAndroid.showWithGravity(
          'Função isActivated() executada com sucesso.',
          ToastAndroid.SHORT,
          ToastAndroid.BOTTOM
        );
      console.log(`isActivated () => ${res}`);
    } catch (err: any) {
      console.error(`isActivated () catch => ${err}`);
    }
  };

  return (
    <OptionsList
      options={[
        { text: 'init', action: init },
        { text: 'destroy', action: destroy },
        { text: 'pay', action: pay },
        { text: 'abort payment', action: abortPayment },
        { text: 'is activated', action: isActivated },
      ]}
      subtitle={
        <Text
          style={styles.subtitle}
        >{`SDK Ativado?: ${SDKActivated}\nEvento do SDK:\n${JSON.stringify(plugpagEvent, null, 2) || '{}'}`}</Text>
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
