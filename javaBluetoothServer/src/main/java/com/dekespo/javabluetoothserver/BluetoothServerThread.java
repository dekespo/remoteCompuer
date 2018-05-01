package com.dekespo.javabluetoothserver;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BluetoothServerThread extends Thread {

  @Override
  public void run() {
    StreamConnectionNotifier notifier;

    // setup the server to listen for connection
    try {
      // retrieve the local Bluetooth device object
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

    while (true) {
      try {
        System.out.println("waiting for connection...");
        StreamConnection connection = notifier.acceptAndOpen();

        Thread processThread = new Thread(new BluetoothServerCommandsThread(connection));
        processThread.start();
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
    }
  }
}
