package com.maximdim.conconj;

public class ConstConClientException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ConstConClientException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public ConstConClientException(String message) {
    super(message);
  }

  
}

