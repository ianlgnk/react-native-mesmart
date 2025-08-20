import React from 'react';
import { StyleSheet, Text, View } from 'react-native';

import type { Modules } from '../../@types';

type Props = {
  nativeModule: '' | Modules;
};

export const Header: React.FC<Props> = ({ nativeModule }: Props) => {
  const resolveTitle = () => {
    if (!nativeModule) return 'MESmartExample';
    return `${nativeModule.toLocaleUpperCase()}`;
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>{resolveTitle()}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    alignItems: 'center',
    marginBottom: 7.5,
    paddingVertical: 15,
    borderBottomWidth: 2,
    borderBottomColor: 'grey',
  },
  title: {
    fontSize: 32,
    fontWeight: '700',
    color: 'black',
  },
});
