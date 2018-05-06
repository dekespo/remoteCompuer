package com.dekespo.javaserver.bluetooth;

import com.dekespo.commonclasses.ConnectionThread;
import com.dekespo.commonclasses.IStreamConnection;

public class ServerConnectionThread extends ConnectionThread {
  private String deviceName = null;

  public ServerConnectionThread(IStreamConnection connection) {
    super(connection);
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
    System.out.println("waiting for connection...");
  }

  @Override
  public String processData(String data) {
    String[] keyValue = data.split("\\|");
    switch (keyValue[0]) {
      case "HANDSHAKE":
        this.deviceName = keyValue[1];
        break;
      case "SCREEN":
        return "Hey Client!";
      default:
        System.out.println("Unknown data received: " + data);
        break;
    }
    return null;
  }
}
