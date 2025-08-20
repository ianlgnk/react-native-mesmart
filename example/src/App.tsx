import React, { useEffect, useRef, useCallback, useState } from 'react';
import { BackHandler, ScrollView, StyleSheet, View } from 'react-native';

import type { Modules } from './@types';

import { Header } from './components/header';
import { Body } from './components/body';

export default function App() {
  const [nativeModule, setNativeModule] = useState<'' | Modules>('');
  const historyRef = useRef<Array<'' | Modules>>([]);

  const setModuleWithHistory = useCallback((next: '' | Modules) => {
    setNativeModule((current) => {
      if (current === next) return current;
      historyRef.current.push(current);
      return next;
    });
  }, []);

  useEffect(() => {
    const onBackPress = () => {
      if (nativeModule !== '') {
        const prev = historyRef.current.pop();
        setNativeModule(prev ?? '');
        return true;
      }

      return false;
    };

    const sub = BackHandler.addEventListener('hardwareBackPress', onBackPress);
    return () => sub.remove();
  }, [nativeModule]);

  return (
    <View style={styles.container}>
      <Header nativeModule={nativeModule} />
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <Body
          nativeModule={nativeModule}
          onPressModule={(value) => setModuleWithHistory(value)}
        />
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  scrollContent: {},
});
