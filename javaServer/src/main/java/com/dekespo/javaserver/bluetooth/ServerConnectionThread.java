package com.dekespo.javaserver.bluetooth;

import com.dekespo.commonclasses.ConnectionThread;
import com.dekespo.commonclasses.DataMessage;
import com.dekespo.commonclasses.IStreamConnection;
import com.dekespo.commonclasses.MouseMessage;
import com.dekespo.javaserver.monitor.MouseManager;

public class ServerConnectionThread extends ConnectionThread {
  private String deviceName = null;
  private MouseManager mouseManager;

  public ServerConnectionThread(IStreamConnection connection) {
    super(connection);
    this.mouseManager = new MouseManager();
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
      case MOUSE:
        this.mouseManager.command(new MouseMessage(dataMessage.getData()));
        break;
      default:
        System.out.println("Unknown data received: " + dataMessage.getDataMessage());
        break;
    }
    return null;
  }
}
