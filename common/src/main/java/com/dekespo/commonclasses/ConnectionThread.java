package com.dekespo.commonclasses;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public abstract class ConnectionThread extends Thread {
  private final int MAXIMUM_BYTE_LIMIT = 1024;
  private IStreamConnection streamConnection;
  private InputStream inputStream;
  private OutputStream outputStream;

  public ConnectionThread(IStreamConnection streamConnection) {
    this.streamConnection = streamConnection;
  }

  public abstract void run();

  public DataMessage receiveData() {
    try {
      byte[] byteData = new byte[MAXIMUM_BYTE_LIMIT];
      int result = this.inputStream.read(byteData);
      if (result == -1) return null;
      return new DataMessage(new String(byteData));
    } catch (IOException e) {
      e.printStackTrace();
      closeStream();
      return null;
    }
  }

  public void sendData(DataMessage dataMessage) {
    try {
      this.outputStream.flush();
      byte[] bytesToSend = dataMessage.getDataMessage().getBytes(Charset.defaultCharset());
      this.outputStream.write(bytesToSend);
    } catch (IOException e) {
      e.printStackTrace();
      closeStream();
    }
  }

  public abstract DataMessage processData(DataMessage dataMessage);

  public boolean openStream() {
    try {
      this.inputStream = this.streamConnection.openInputStream();
      this.outputStream = this.streamConnection.openOutputStream();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public void closeStream() {
    try {
      this.streamConnection.closeStream();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
