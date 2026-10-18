# react-native-mesmart

Módulos nativos (Android) para POS usadas pela Beerpass: leitura de cartão RFID (Mifare), impressão térmica e SDKs de pagamento.

| Módulo | Para que serve | Onde roda |
|---|---|---|
| [`Plugpag`](#plugpag-pagbank) | Pagamento PagBank (PlugPag) | POS PagBank |
| [`Stone`](#stone) | Pagamento Stone (SDK de providers) | POS Stone |
| [`Paystore`](#paystore) | Pagamento Paystore | POS com Paystore |
| [`Sunmi`](#sunmi-p2-b--p2-a11) | RFID e impressão | Sunmi P2-B, P2-A11 |
| [`Tectoy`](#tectoy-t8) | RFID e impressão | Tectoy T8 |
| [`Gertec`](#gertec-gpos780--gpos780s) | RFID e impressão | Gertec GPOS780, GPOS780S |
| [`Pax`](#pax-a910) | RFID e impressão | Pax A910 |

Referências dos SDKs: [PlugPag](https://developer.pagbank.com.br/v1.0/reference/smart-pos-introducao) · [Stone](https://sdkandroid.stone.com.br/reference/bem-vindo-sdk-android) · [Paystore](http://177.69.97.18:6655/)

> Só Android. Importar no iOS lança `IOS_ERROR`.

## Sumário

- [Compatibilidade](#compatibilidade)
- [Instalação](#instalação)
- [Configuração do app Android](#configuração-do-app-android)
- [Qual APK para qual POS](#qual-apk-para-qual-pos)
- [API](#api)
- [Eventos](#eventos)
- [Formato do RFID](#formato-do-rfid)
- [App de exemplo](#app-de-exemplo)
- [Migrando da 2.x para a 3.x](#migrando-da-2x-para-a-3x)
- [Publicando uma versão](#publicando-uma-versão)

## Compatibilidade

| react-native-mesmart | SDK Stone | React Native | Toolchain Android |
|---|---|---|---|
| **3.x** | 4.20.16 | **≥ 0.80** (arquitetura antiga, `newArchEnabled=false`) | AGP ≥ 8.6, Gradle 8.14, compileSdk 35, JDK 17, Kotlin ≥ 2.1, minSdk 24 |
| 2.x | 4.12.0 | 0.72 | AGP 7.4, compileSdk 33, JDK 11 |

A 3.x exige RN ≥ 0.80 porque o SDK Stone 4.20 traz dependências compiladas em Kotlin 2.2, que só um compilador Kotlin 2.1 consegue ler. O RN 0.80 é o primeiro com Kotlin 2.1.

## Instalação

```sh
yarn add react-native-mesmart
```

ou

```sh
npm install react-native-mesmart
```

## Configuração do app Android

O [app de exemplo](example/android) está configurado assim e serve de referência completa.

### 1. Repositórios Maven

O SDK da Stone e o SDK da Sunmi vêm do repositório privado da Stone no packagecloud. O token é fornecido pela Stone. Coloque-o no `android/local.properties` do app, que não vai para o git:

```properties
packageCloudReadToken=SEU_TOKEN
```

Também é aceito pela variável de ambiente `PACKAGECLOUD_READ_TOKEN`, útil em CI.

`android/build.gradle`:

```gradle
buildscript {
  ext {
    // ...
    compileSdkVersion = 35
    stone_sdk_version = "4.20.16" // lido pela lib e pelos flavors do app

    localProp = new Properties()
    localProp.load(new FileInputStream(rootProject.file('local.properties')))
    stoneToken = System.env.PACKAGECLOUD_READ_TOKEN ?: (localProp["packageCloudReadToken"] ?: "")
  }
}

allprojects {
  repositories {
    mavenCentral()
    // O GitHub responde 504 para artefatos que não existem e o Gradle aborta;
    // por isso este repo fica restrito ao grupo do PlugPag.
    maven {
      url 'https://github.com/pagseguro/PlugPagServiceWrapper/raw/master'
      content { includeGroupByRegex 'br\\.com\\.uol\\.pagseguro.*' }
    }
    maven { url "https://packagecloud.io/priv/${stoneToken}/stone/pos-android/maven2" }
    google()
  }
}
```

### 2. Um flavor por POS (HAL da Stone)

Cada fabricante tem um HAL (driver) próprio no SDK da Stone. Na 4.20, os HALs da Sunmi e da Tectoy usam versões incompatíveis da mesma biblioteca e **não cabem no mesmo APK**. A lib não embute nenhum HAL: o app escolhe por product flavor, que é o modelo que a [documentação da Stone](https://sdkandroid.stone.com.br/reference/processo-de-build-de-aplicação.md) adota.

`android/app/build.gradle`:

```gradle
react {
  // Com flavors, as variantes debug passam a ter o nome do flavor.
  debuggableVariants = ["pagbankDebug", "sunmiDebug", "tectoyDebug"]
}

android {
  defaultConfig {
    ndk { abiFilters "armeabi-v7a" } // todas as POS suportadas são 32 bits
  }

  flavorDimensions = ["pos"]
  productFlavors {
    pagbank {} // PagBank: sem HAL da Stone
    sunmi {}   // Stone em Sunmi P2-B / P2-A11
    tectoy {}  // Stone em Tectoy T8
  }

  compileOptions {
    coreLibraryDesugaringEnabled true // exigido pelo SDK Stone 4.20
  }

  packagingOptions {
    jniLibs { useLegacyPackaging true }
  }
}

dependencies {
  coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

  sunmiImplementation("br.com.stone:stone-sdk-posandroid-sunmi:${rootProject.ext.stone_sdk_version}")
  tectoyImplementation("br.com.stone:stone-sdk-posandroid-tectoy:${rootProject.ext.stone_sdk_version}")

  // AARs de fabricante que a lib usa mas não distribui (ver passo 4)
  implementation fileTree(dir: 'libs', include: ['*.aar'])
}
```

Build de cada APK: `./gradlew :app:assembleTectoyRelease`, `:app:assembleSunmiRelease`, `:app:assemblePagbankRelease`.

### 3. AndroidManifest

Uma dependência do SDK Stone declara `allowBackup`; sem o `tools:replace` o merge do manifest falha:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:tools="http://schemas.android.com/tools">

  <!-- Sunmi: leitor RFID -->
  <uses-permission android:name="com.sunmi.perm.CONTACTLESS_CARD" />
  <!-- PlugPag -->
  <uses-permission android:name="br.com.uol.pagseguro.permission.MANAGE_PAYMENTS" />
  <!-- Pax -->
  <uses-permission android:name="com.pax.permission.PICC" />
  <uses-permission android:name="com.pax.permission.PRINTER" />
  <!-- Paystore -->
  <uses-permission android:name="br.com.phoebus.android.payments.provider.READ_PERMISSION" />

  <application
    android:allowBackup="false"
    tools:replace="android:allowBackup">
    ...
  </application>
</manifest>
```

Inclua só as permissões dos módulos que o app usa. Stone, Tectoy e Gertec não pedem permissão no app; os HALs trazem as deles.

### 4. Arquivos de fabricante no app

A lib compila contra esses arquivos, mas quem os empacota é o app. Copie do [`example/android/app`](example/android/app) para os mesmos caminhos no seu app:

| Módulo | Arquivo | Destino |
|---|---|---|
| Gertec | `android-easy-layer-gpos780-2.1.4-debug.aar`, `libgedi-1.16.21-gpos780-payment-release.aar` | `android/app/libs/` |
| Paystore | `paystore-api-v2.11.7.31.aar` | `android/app/libs/` |
| Pax | `libDeviceConfig.so` | `android/app/src/main/jniLibs/armeabi-v7a/` |

## Qual APK para qual POS

| POS | Adquirente | Flavor | Pagamento | RFID e impressão |
|---|---|---|---|---|
| Sunmi P2-B / P2-A11 | Stone | `sunmi` | `Stone` | `Sunmi` |
| Tectoy T8 | Stone | `tectoy` | `Stone` | `Tectoy` |
| Gertec GPOS780 / GPOS780S | PagBank | `pagbank` | `Plugpag` | `Gertec` |
| Sunmi P2-B / P2-A11 | PagBank | `pagbank` | `Plugpag` | `Sunmi` |

O SDK de pagamento ativado no app tem que bater com o flavor instalado: o APK `pagbank` não tem HAL da Stone, então `Stone.pay` não funciona nele.

## API

```ts
import { Plugpag, Stone, Paystore, Sunmi, Tectoy, Gertec, Pax } from 'react-native-mesmart';
```

Todos os métodos retornam `Promise`. Em erro, a promise é rejeitada com a mensagem do SDK. Valores monetários de **Stone e PlugPag são em centavos, como string** (`'500'` = R$ 5,00).

`PaymentMethod` = `'debitCard' | 'creditCard' | 'pix' | 'voucher' | 'money' | 'bonus'`. Cada SDK aceita só uma parte deles (ver em cada módulo).

### Plugpag (PagBank)

Requer o PlugPagService instalado na POS (POS PagBank).

| Método | Retorno | Descrição |
|---|---|---|
| `init(activationCode)` | `null` | Ativa o terminal com o código de ativação da conta PagBank. |
| `isActivated()` | `boolean` | Se o `init` já rodou nesta sessão do app. |
| `pay(value, paymentMethod)` | `PlugpagTransactionResult` | Venda à vista. `value` em centavos. Métodos: `debitCard`, `creditCard`, `voucher`, `pix`. |
| `voidPayment({ transactionCode, transactionId, printReceipt?, voidType? })` | `PlugpagTransactionResult` | Estorno. `voidType`: `'payment'` (padrão) ou `'qrcode'`/`'pix'`. |
| `abortPayment()` | `null` | Cancela a transação em andamento. |
| `resolveEvent(eventData)` | `string` | Traduz um evento de `onPlugpagEventData` em mensagem para o cliente. |
| `destroy()` | `null` | Desativa o terminal. |

```ts
import { Plugpag, PlugpagResult } from 'react-native-mesmart';

await Plugpag.init('123456');
const result = await Plugpag.pay('1500', 'creditCard'); // R$ 15,00
if (result.result === PlugpagResult.RET_OK) {
  // aprovado: guarde result.transactionCode e result.transactionId para estorno
}
```

O progresso da transação chega pelo evento [`onPlugpagEventData`](#eventos).

### Stone

Funciona nas POS Stone (flavors `sunmi` e `tectoy`).

| Método | Retorno | Descrição |
|---|---|---|
| `init({ providerId, authorization }, appName)` | `null` | Inicializa o SDK. As chaves são as de QR Code/PIX fornecidas pela Stone (`authorization` inclui o `Bearer`). `appName` é obrigatório. |
| `isActivated()` | `boolean` | Se o `init` já rodou. |
| `getStoneCode()` | `string` | Stone Code do usuário ativado na POS. |
| `pay(value, paymentMethod, isCapture = true)` | `null` | Inicia a venda. `value` em centavos. Métodos: `debitCard`, `creditCard`, `voucher`, `pix`. A resolução da promise **não** é o resultado: ele chega pelo evento [`onStoneEventData`](#eventos). |
| `abortPayment()` | `boolean` | Aborta a venda em andamento (`false` se não havia nenhuma). |
| `printVia(transaction, 'MERCHANT' \| 'CLIENT')` | `null` | Imprime a via padrão da Stone. |
| `captureTransaction(transaction)` | `StoneCaptureResponse` | Captura uma venda feita com `isCapture = false`. |
| `cancelTransaction(transaction)` | `StoneCancelResponse` | Cancela (estorna) uma venda. |

```ts
await Stone.init({ providerId, authorization }, 'BeerPass');
await Stone.pay('1500', 'pix');
// acompanhe onStoneEventData: onStatusChanged (com qrCode no PIX), onSuccess ou onError
```

`transaction` é o `StoneTransaction` recebido no evento `onSuccess`.

### Paystore

| Método | Retorno | Descrição |
|---|---|---|
| `init()` | `null` | Inicializa o client da Paystore. |
| `isActivated()` | `boolean` | Se o `init` já rodou. |
| `pay(value, transactionId, showReceipt, paymentMethods, installments)` | `void` | Inicia o pagamento. `value` em reais com ponto (`'15.00'`). `paymentMethods` aceita `creditCard`, `debitCard` e `voucher`. `installments = 0` é à vista. |

O pagamento é confirmado automaticamente e o resultado (`PaystorePaymentResult`) chega pelo evento [`onPayment`](#eventos). A promise de `pay` resolve assim que o fluxo começa.

### Sunmi (P2-B / P2-A11)

Usa o SDK da própria Sunmi (paylib e serviço de impressora), não o da Stone. Funciona em qualquer flavor.

| Método | Retorno | Descrição |
|---|---|---|
| `scanRfid()` | `string` | Lê um cartão Mifare. Ver [formato do RFID](#formato-do-rfid). |
| `cancelScan()` | `null` | Cancela a leitura. |
| `setPrinterAlign('left' \| 'center' \| 'right')` | `null` | Alinhamento das próximas impressões. |
| `printText(text, fontSize = 24, isBold = false, isUnderline = false)` | `null` | Imprime texto. A quebra de linha vem do `\n` no texto. Com tamanho 24 cabem 32 colunas. |
| `printEmptyLine()` | `null` | Avança uma linha. |
| `printBitmap(base64, width = 200, height = 200)` | `null` | Imprime imagem PNG/JPEG em base64 (por exemplo, QR Code). |
| `printBpLogo()` | `null` | Imprime o logo da Beerpass. |
| `printClientReceipt(date, rechargeType, name, document, paymentMethod, rfid, rechargeValue)` | `void` | Comprovante de recarga pronto. |

Cada chamada imprime na hora.

```ts
await Sunmi.setPrinterAlign('center');
await Sunmi.printBpLogo();
await Sunmi.printText('Cupom de Consumo\n', 28, true);
await Sunmi.setPrinterAlign('left');
await Sunmi.printText('================================\n', 24, true);
await Sunmi.printText('Valor: R$ 18,00\n');
await Sunmi.printEmptyLine();
```

### Tectoy (T8)

Usa o SDK da Stone com o HAL da Tectoy, então precisa do flavor `tectoy`. Inicializa o SDK Stone sozinho, sem depender do `Stone.init`.

| Método | Retorno | Descrição |
|---|---|---|
| `scanRfid()` | `string` | Lê um cartão Mifare (espera até 30 s). Ver [formato do RFID](#formato-do-rfid). |
| `cancelScan()` | `true` | Cancela a leitura; a promise do `scanRfid` é rejeitada. |
| `setPrinterAlign`, `printText`, `printEmptyLine`, `printBitmap`, `printBpLogo` | `null` | Mesmas assinaturas da [`Sunmi`](#sunmi-p2-b--p2-a11), mas **acumulam** o cupom em vez de imprimir na hora. |
| `commitPrint()` | `true` | **Imprime** tudo que foi acumulado, num único job. Obrigatório no fim de cada cupom. |

```ts
await Tectoy.setPrinterAlign('center');
await Tectoy.printBpLogo();
await Tectoy.printText('Cupom de Consumo\n', 28, true);
await Tectoy.setPrinterAlign('left');
await Tectoy.printText('================================\n', 24, true);
await Tectoy.printText('Valor: R$ 18,00\n');
await Tectoy.commitPrint(); // sem isso nada sai
```

Por que é diferente da Sunmi:

- **Cupom num job só:** o HAL da Tectoy avança o papel de corte ao fim de cada impressão. Imprimir linha a linha deixaria um espaço de corte entre as linhas.
- **Sem HTML:** o firmware Stone da T8 bloqueia o processo da WebView, então impressão via HTML (como a da Gertec) não funciona. O texto é desenhado em bitmap com a mesma métrica da Sunmi (32 colunas no tamanho 24), e os templates da P2 servem sem ajuste, bastando chamar `commitPrint()` no final.

### Gertec (GPOS780 / GPOS780S)

Usa o SDK da Gertec (easylayer e GEDI). Precisa das AARs da Gertec no app ([passo 4](#4-arquivos-de-fabricante-no-app)).

| Método | Retorno | Descrição |
|---|---|---|
| `scanRfid()` | `string` | Lê um cartão Mifare (espera até 30 s). |
| `cancelScan()` | `true` | Cancela a leitura. |
| `printHtml(html)` | `true` | Imprime um HTML (largura útil de 384 px). |
| `scrollPrinterPaper(lines)` | `true` | Avança o papel. |

```ts
await Gertec.printHtml(`<html><body style="width:384px">...</body></html>`);
await Gertec.scrollPrinterPaper(60);
```

### Pax (A910)

Usa a NeptuneLite (já inclusa na lib). Precisa do `libDeviceConfig.so` no app ([passo 4](#4-arquivos-de-fabricante-no-app)). A lib só inicializa leitor e impressora quando o modelo é **A910**.

| Método | Retorno | Descrição |
|---|---|---|
| `scanRfid()` | `string` | Lê um cartão Mifare. |
| `cancelScan()` | `null` | Cancela a leitura. |
| `printerInit()` | `null` | Começa um cupom. |
| `addText(text, fontSize = 24)` | `null` | Adiciona texto ao cupom. |
| `addBpLogo()` | `null` | Adiciona o logo da Beerpass. |
| `printerStart()` | `null` | Imprime o cupom montado. |
| `printClientReceipt(date, rechargeType, name, document, paymentMethod, rfid, rechargeValue)` | `void` | Comprovante de recarga pronto. |

## Eventos

Os eventos chegam pelo `DeviceEventEmitter`:

| Evento | Payload | Quando |
|---|---|---|
| `onPlugpagEventData` | `PlugpagEventData` (`eventCode`, `customMessage`) | Etapas da transação PlugPag (aguardando cartão, senha, aprovada…). |
| `onStoneEventData` | `StoneEventData` (`type`: `onStatusChanged`, `onSuccess` ou `onError`) | Progresso e resultado de `Stone.pay`. No PIX, `onStatusChanged` traz o `qrCode` em base64. |
| `onPayment` | `PaystorePaymentResult` | Resultado de `Paystore.pay`, já confirmado. |

```ts
import { DeviceEventEmitter } from 'react-native';
import {
  Plugpag,
  type PlugpagEventData,
  type StoneEventData,
  type PaystorePaymentResult,
} from 'react-native-mesmart';

useEffect(() => {
  const subs = [
    DeviceEventEmitter.addListener('onPlugpagEventData', async (event: PlugpagEventData) => {
      const message = await Plugpag.resolveEvent(event); // texto para mostrar ao cliente
    }),
    DeviceEventEmitter.addListener('onStoneEventData', (event: StoneEventData) => {
      if (event.type === 'onSuccess') { /* event.transaction */ }
      if (event.type === 'onError') { /* event.listOfErrors */ }
    }),
    DeviceEventEmitter.addListener('onPayment', (payment: PaystorePaymentResult) => {}),
  ];
  return () => subs.forEach((s) => s.remove());
}, []);
```

Todos os tipos (`PlugpagTransactionResult`, `StoneTransaction`, `StoneEventData`, `PaystorePaymentResult`, `SunmiAPI`, `TectoyAPI`…) são exportados pelo pacote.

## Formato do RFID

Todos os módulos devolvem o mesmo formato: os bytes do UID do cartão em ordem invertida, convertidos para decimal e completados com zeros à esquerda até 10 dígitos (ex.: `1839402837`). Assim, o mesmo cartão tem o mesmo número em qualquer POS.

## App de exemplo

O [`example/`](example) tem uma tela por módulo (PLUGPAG, STONE, SUNMI, GERTEC e TECTOY) para testar na POS.

1. `yarn` na raiz do repositório.
2. Crie `example/android/local.properties` com `sdk.dir` e `packageCloudReadToken`.
3. Build com **JDK 17** e o flavor da POS:

   ```sh
   cd example/android && ./gradlew :app:assembleTectoyDebug
   ```

   O APK sai em `example/android/app/build/outputs/apk/tectoy/debug/`.

4. Metro e instalação:

   ```sh
   cd example && yarn start
   adb install -r android/app/build/outputs/apk/tectoy/debug/app-tectoy-debug.apk
   adb reverse tcp:8081 tcp:8081
   ```

Se a POS reconectar no USB, o `adb reverse` se perde e precisa ser refeito.

## Migrando da 2.x para a 3.x

1. Atualize o app para **RN ≥ 0.80**, com AGP ≥ 8.6, Gradle 8.14, compileSdk 35 e JDK 17. Mantenha `newArchEnabled=false`.
2. Crie os **flavors por POS** e mova o HAL da Stone para eles ([passo 2](#2-um-flavor-por-pos-hal-da-stone)). Defina `stone_sdk_version = "4.20.16"`.
3. Ative o **core library desugaring** (`desugar_jdk_libs` 2.1.5).
4. Adicione `tools:replace="android:allowBackup"` no manifest.
5. Restrinja o repositório do PlugPag no GitHub com o `content { includeGroupByRegex ... }`.
6. O SDK da Sunmi agora vem da própria lib: não dependa mais do HAL Sunmi da Stone para ter `Sunmi` em APKs PagBank.
7. Nas Tectoy T8, imprima com os templates da P2 + `Tectoy.commitPrint()`.

## Publicando uma versão

Para mantenedores. O bump de versão é feito pelo `release-it`, nunca à mão.

1. Faça o merge na `main`.
2. Commit, tag e release no GitHub (passe a versão explícita: sem ela, o `release-it` calcula pelos commits):

   ```sh
   GITHUB_TOKEN=$(gh auth token) yarn release-it X.Y.Z --no-npm.publish
   ```

   `--npm.publish=false` **não** desliga a publicação; use `--no-npm.publish`.

3. Publique no npm. A conta exige 2FA por chave de segurança/Touch ID, e o CLI abre o navegador para confirmar:

   ```sh
   npm publish
   ```

## License

MIT
