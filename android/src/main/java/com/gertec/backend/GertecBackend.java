package com.gertec.backend;

/**
 * Contrato entre o modulo e o SDK Gertec "Neo" (libgedi 2.x), que roda num classloader isolado.
 * A interface e o callback sao carregados pelo classloader do app (pai) e so usam tipos da plataforma,
 * assim o adaptador carregado do dex da Neo consegue implementar sem depender de classes do React Native.
 */
public interface GertecBackend {
  /** Resolve com o UID do cartao (byte[]). */
  void scanRfid(GertecCallback callback);

  /** Resolve com Boolean.TRUE. */
  void cancelScan(GertecCallback callback);

  /** Resolve com Boolean.TRUE. */
  void scrollPrinterPaper(int lines, GertecCallback callback);

  /** Resolve com Boolean.TRUE. */
  void printHtml(String html, GertecCallback callback);
}
