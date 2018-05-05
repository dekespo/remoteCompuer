package com.dekespo.javabluetoothserver;

import com.dekespo.javabluetoothserver.Bluetooth.ServerManagerThread;

public class JavaServer {
  public static void main(String[] args) {
    System.out.println("Running JavaServer...");
    Thread bluetoothServerThread = new Thread(new ServerManagerThread());
    bluetoothServerThread.start();
  }
}
