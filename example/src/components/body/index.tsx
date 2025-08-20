import React from 'react';
import { StyleSheet, Text } from 'react-native';

import { OptionsList } from '../optionsList';
import { PlugpagExample } from '../plugpag';
import { StoneExample } from '../stone';
import { SunmiExample } from '../sunmi';
import { GertecExample } from '../gertec';

import { MODULES, type Modules } from '../../@types';

type Props = {
  nativeModule: '' | Modules;
  onPressModule: (payload: '' | Modules) => void;
};

export const Body: React.FC<Props> = ({
  nativeModule,
  onPressModule,
}: Props) => {
  const HIDE: Modules[] = ['paystore', 'pax'];

  switch (nativeModule) {
    case 'plugpag':
      return <PlugpagExample />;
    case 'stone':
      return <StoneExample />;
    case 'sunmi':
      return <SunmiExample />;
    case 'gertec':
      return <GertecExample />;
    case '':
      return (
        <OptionsList
          options={MODULES.filter((m): m is Modules => !HIDE.includes(m)).map(
            (module) => ({
              text: module.toUpperCase(),
              action: (payload) => onPressModule(payload),
              payload: module,
            })
          )}
          subtitle={
            <Text style={styles.subtitle}>
              Selecione uma NativeModule abaixo:
            </Text>
          }
        />
      );
    default:
      return null;
  }
};

const styles = StyleSheet.create({
  subtitle: {
    fontSize: 16,
    color: 'black',
    textAlign: 'center',
  },
});
