package com.dekespo.javabluetoothserver.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.microedition.io.StreamConnection;

public class ServerConnectionThread extends Thread {
  private final int MAXIMUM_BYTE_LIMIT = 1024;
  private StreamConnection streamConnection;
  private InputStream inputStream;
  private OutputStream outputStream;
  private String deviceName = null;

  public ServerConnectionThread(StreamConnection connection) {
    this.streamConnection = connection;
  }

  @Override
  public void run() {

    if (!openStream()) return;

    boolean doOnce = true;

    while (true) {
      String data = receiveData();
      if (data == null) {
        System.out.println("The device \"" + this.deviceName + "\" is disconnected");
        break;
      }
      data = processData(data);
      if (data != null) sendData(data);

      if (doOnce && this.deviceName != null) {
        System.out.println("The device  \"" + this.deviceName + "\" is connected");
        doOnce = false;
      }
    }

    closeStream();
  }

  private String receiveData() {
    try {
      byte[] byteData = new byte[MAXIMUM_BYTE_LIMIT];
      int result = this.inputStream.read(byteData);
      if (result == -1) return null;
      return new String(byteData);
    } catch (IOException e) {
      e.printStackTrace();
      closeStream();
      return null;
    }
  }

  private void sendData(String data) {
    try {
      data.trim();
      this.outputStream.flush();
      byte[] bytesToSend = data.getBytes(Charset.defaultCharset());
      this.outputStream.write(bytesToSend);
    } catch (IOException e) {
      e.printStackTrace();
      closeStream();
    }
  }

  private String processData(String data) {
    String[] keyValue = data.split("\\|");
    switch (keyValue[0]) {
      case "HANDSHAKE":
        this.deviceName = keyValue[1];
        break;
      case "SCREEN":
        return "Cl";
      default:
        System.out.println("Unknown data received: " + data);
        break;
    }
    return null;
  }

  private boolean openStream() {
    try {
      this.inputStream = this.streamConnection.openInputStream();
      this.outputStream = this.streamConnection.openOutputStream();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private void closeStream() {
    try {
      this.streamConnection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
