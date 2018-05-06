package com.dekespo.commonclasses;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IStreamConnection {
  InputStream openInputStream() throws IOException;

  OutputStream openOutputStream() throws IOException;

  void closeStream() throws IOException;

  boolean isConnected();
}
