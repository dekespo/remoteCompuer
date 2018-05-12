package com.dekespo.javaserver.bluetooth;

import com.dekespo.commonclasses.ConnectionThread;
import com.dekespo.commonclasses.DataMessage;
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
      DataMessage dataMessage = receiveData();
      if (dataMessage == null) {
        System.out.println("The device \"" + this.deviceName + "\" is disconnected");
        break;
      }
      dataMessage = processData(dataMessage);
      if (dataMessage != null) sendData(dataMessage);

      if (doOnce && this.deviceName != null) {
        System.out.println("The device  \"" + this.deviceName + "\" is connected");
        doOnce = false;
      }
    }

    closeStream();
    System.out.println("waiting for connection...");
  }

  @Override
  public DataMessage processData(DataMessage dataMessage) {
    switch (dataMessage.getTag()) {
      case HANDSHAKE:
        this.deviceName = dataMessage.getData();
        break;
      case SCREEN:
        return new DataMessage(DataMessage.Tag.HANDSHAKE, "Hey Client!");
      default:
        System.out.println("Unknown data received: " + dataMessage.getDataMessage());
        break;
    }
    return null;
  }
}
