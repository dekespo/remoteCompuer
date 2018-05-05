package com.dekespo.javabluetoothserver.bluetooth;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class ServerManagerThread extends Thread {

  @Override
  public void run() {
    StreamConnectionNotifier notifier;

    try {
      LocalDevice local = LocalDevice.getLocalDevice();
      local.setDiscoverable(DiscoveryAgent.GIAC);

      UUID uuid = new UUID(91201379); // "056f9f63-0000-1000-8000-00805f9b34fb"
      String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
      notifier = (StreamConnectionNotifier) Connector.open(url);
    } catch (Exception e) {
      System.out.println("Make sure that the machine has bluetooth and it is turned on.");
      e.printStackTrace();
      return;
    }

    System.out.println("waiting for connection...");
    while (true) {
      try {
        StreamConnection connection = notifier.acceptAndOpen();

        Thread processThread = new Thread(new ServerConnectionThread(connection));
        processThread.start();
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
    }
  }
}
