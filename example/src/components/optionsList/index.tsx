import React, { type ReactNode } from 'react';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';

type Props = {
  options: Array<{
    text: string;
    action: (payload?: any) => void;
    payload?: any;
  }>;
  subtitle?: ReactNode;
};

export const OptionsList: React.FC<Props> = ({ options, subtitle }: Props) => {
  return (
    <View style={styles.container}>
      {subtitle}
      <View style={styles.grid}>
        {options.map((item) => (
          <TouchableOpacity
            key={item.text}
            style={styles.button}
            onPress={() => item.action(item.payload)}
          >
            <Text style={styles.btnTitle}>{item.text.toUpperCase()}</Text>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    gap: 15,
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
    paddingHorizontal: 15,
  },
  button: {
    width: '48%',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 15,
    borderWidth: 2,
    borderColor: 'grey',
    borderRadius: 15,
    marginBottom: 15,
  },
  btnTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: 'black',
  },
});
