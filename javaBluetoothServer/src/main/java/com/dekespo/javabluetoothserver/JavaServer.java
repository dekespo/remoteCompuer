package com.dekespo.javabluetoothserver;

public class JavaServer {
  public static void main(String[] args) {
    System.out.println("Running JavaServer...");
    Thread bluetoothServerThread = new Thread(new BluetoothServerThread());
    bluetoothServerThread.start();
  }
}
