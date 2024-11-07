import { NativeModules } from 'react-native';
import type { PaymentMethod } from './';

const getPaystorePaymentMethods = (
  paymentMethods: PaymentMethod[]
): Array<'CREDIT' | 'DEBIT' | 'VOUCHER'> => {
  const paymentMethodsResolved: Array<'CREDIT' | 'DEBIT' | 'VOUCHER'> = [];

  paymentMethods.forEach((pm) => {
    if (pm === 'creditCard') paymentMethodsResolved.push('CREDIT');
    if (pm === 'debitCard') paymentMethodsResolved.push('DEBIT');
    if (pm === 'voucher') paymentMethodsResolved.push('VOUCHER');
  });

  return paymentMethodsResolved;
};

export const payWithPaystore = async (
  value: string,
  transactionId: string,
  showReceipt: boolean,
  paymentMethods: PaymentMethod[],
  installments: number
): Promise<void> => {
  await NativeModules.Paystore.pay(
    value,
    transactionId,
    showReceipt,
    getPaystorePaymentMethods(paymentMethods),
    installments
  );
};
