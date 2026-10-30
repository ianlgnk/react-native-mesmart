package com.gertec.backend;

public interface GertecCallback {
  void resolve(Object value);

  void reject(String code, String message, Throwable cause);
}
