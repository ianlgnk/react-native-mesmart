import React, { useEffect } from 'react';
import { Image, StyleSheet, Text, ToastAndroid } from 'react-native';
import { Gertec } from 'react-native-mesmart';

import { OptionsList } from '../optionsList';

type Props = {};

export const GertecExample: React.FC<Props> = ({}: Props) => {
  const [isScanning, setIsScanning] = React.useState(false);
  const [rfidReaded, setRfidReaded] = React.useState('');

  useEffect(() => {}, []);

  const scanRfid = async () => {
    try {
      setIsScanning(true);
      setRfidReaded('');
      const res = await Gertec.scanRfid();
      setRfidReaded(res);
      console.log(`scanRfid() => ${res}`);
    } catch (e: any) {
      console.error(`scanRfid() catch => ${e}`);
    } finally {
      setIsScanning(false);
    }
  };

  const cancelScan = async () => {
    try {
      const res = await Gertec.cancelScan();
      setIsScanning(false);
      console.log(`cancelScan() => ${res}`);
    } catch (e: any) {
      console.error(`cancelScan() catch => ${e}`);
    }
  };

  const printConsumptionCode = async () => {
    try {
      const W = 390; // 58mm. Em 80mm use 576.
      const QR = 250; // tamanho do QR (ajuste 280–340 conforme seu layout)
      const { uri } = Image.resolveAssetSource(
        require('../../assets/logo_black.png')
      );
      // Pouco confiável: <img src="${uri}" />

      const html = `
        <!doctype html>
        <html>
          <head>
            <meta charset="utf-8" />
            <meta
              name="viewport"
              content="width=device-width,initial-scale=1"
            />
            <style>
              body {
                width: ${W}px;
                margin: 0 auto;
                color: #000;
                font-family: Arial, sans-serif;
              }
              .wrap { padding: 8px; }
              .center { text-align: center; }
              .logo {
                display: block;
                max-width: 240px;
                max-height: 90px;
                margin: 0 auto;
              }
              .title { font-size: 20px; font-weight: 800; margin-bottom: 2px; }
              .date { font-size: 12px; opacity: .9; }
              .rule { height: 2px; background: #000; margin: 15px 0px; }
              .kv {
                font-size: 14px;
                margin: 5px 0;
                display: flex;
                justify-content: space-between;
              }
              .label { opacity: .9; }
              /* QR grande e nítido */
              .qr {
                display: block;
                margin: 10px auto 0;
                width: ${QR}px;
                height: ${QR}px;
                image-rendering: pixelated;
                image-rendering: crisp-edges;
              }
              .footer {
                margin-top: 10px;
                text-align: center;
                font-size: 12px;
              }
            </style>
          </head>

          <body>
            <div class="wrap">
              <img class="logo" src="${uri}" />

              <div class="center">
                <div class="title">Cupom de Consumo</div>
                <div class="date">Dia 20/08/2025 às 13:30</div>
              </div>

              <div class="rule"></div>

              <div class="kv">
                <span class="label">Tap</span>
                <span>03 - Amstel</span>
              </div>
              <div class="kv">
                <span class="label">Quantidade</span>
                <span>500 mls</span>
              </div>
              <div class="kv">
                <span class="label">Pagamento</span>
                <span>PIX</span>
              </div>

              <div class="kv" style="font-size: 18px; font-weight: 800; margin-top: 8px;">
                <span>Valor em Chopp</span>
                <span>R$ 18,00</span>
              </div>

              <div class="rule"></div>

              <div class="center">
                <div class="title">QRCode para consumo</div>
              </div>

              ${
                true
                  ? `<img
                      class="qr"
                      src="data:image/png;base64,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"
                      width="${QR}"
                      height="${QR}"
                    />`
                  : ''
              }

              <div class="footer">Código para consumo: <b>35641247</b></div>
              <div class="footer"><b>Beerpass</b> • www.beerpassclub.com</div>
            </div>
          </body>
        </html>
      `;
      const res = await Gertec.printHtml(html);
      await Gertec.scrollPrinterPaper(60);
      ToastAndroid.showWithGravity(
        'Função printConsumptionCode() executada com sucesso.',
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      console.log(`printConsumptionCode () => ${res}`);
    } catch (err: any) {
      console.error(`printConsumptionCode () catch => ${err}`);
    }
  };

  const printClientReceipt = async () => {
    try {
      const W = 390;
      const { uri } = Image.resolveAssetSource(
        require('../../assets/logo_black.png')
      );

      const html = `
        <!doctype html>
        <html>
          <head>
            <meta charset="utf-8" />
            <meta
              name="viewport"
              content="width=device-width,initial-scale=1"
            />
            <style>
              body {
                width: ${W}px;
                margin: 0 auto;
                color: #000;
                font-family: Arial, sans-serif;
              }
              .wrap { padding: 8px; }
              .center { text-align: center; }
              .logo {
                display: block;
                max-width: 240px;
                max-height: 90px;
                margin: 0 auto;
              }
              .title { font-size: 20px; font-weight: 800; margin-bottom: 2px; }
              .date { font-size: 12px; opacity: .9; }
              .rule { height: 2px; background: #000; margin: 15px 0px; }
              .kv {
                font-size: 14px;
                margin: 5px 0;
                display: flex;
                justify-content: space-between;
              }
              .label { opacity: .9; }
              .footer {
                margin-top: 10px;
                text-align: center;
                font-size: 12px;
              }
            </style>
          </head>

          <body>
            <div class="wrap">
              <img class="logo" src="${uri}" />

              <div class="center">
                <div class="title">Comprovante de Recarga</div>
                <div class="date">Dia 20/08/2025 às 13:30</div>
              </div>

              <div class="rule"></div>

              <div class="kv"><span class="label">Cliente</span><span>IAN LANGKAMMER BATISTA</span></div>
              <div class="kv"><span class="label">Documento</span><span>155.124.906-56</span></div>
              <div class="kv"><span class="label">Tipo</span><span>APP</span></div>
              <div class="kv"><span class="label">Pagamento</span><span>DÉBITO</span></div>
              <div class="kv"><span class="label">RFID</span><span>1839402837</span></div>
              <div class="kv" style="font-size: 18px; font-weight: 800; margin-top: 8px;">
                <span>Total</span>
                <span>R$ 50,00</span>
              </div>

              <div class="rule"></div>

              <div class="footer"><b>Beerpass</b> • www.beerpassclub.com</div>
            </div>
          </body>
        </html>
      `;
      const res = await Gertec.printHtml(html);
      await Gertec.scrollPrinterPaper(60);
      ToastAndroid.showWithGravity(
        'Função printClientReceipt() executada com sucesso.',
        ToastAndroid.SHORT,
        ToastAndroid.BOTTOM
      );
      console.log(`printClientReceipt () => ${res}`);
    } catch (err: any) {
      console.error(`printClientReceipt () catch => ${err}`);
    }
  };

  return (
    <OptionsList
      options={[
        { text: 'scan rfid', action: scanRfid },
        { text: 'cancel scan', action: cancelScan },
        { text: 'print consumption coupon', action: printConsumptionCode },
        { text: 'print recharge via', action: printClientReceipt },
      ]}
      subtitle={
        <Text
          style={styles.subtitle}
        >{`Status da antena: ${!isScanning ? 'Ocioso' : 'Lendo cartão...'}\nRFID: ${rfidReaded || '?'}`}</Text>
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
