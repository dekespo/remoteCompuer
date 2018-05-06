package com.dekespo.javaserver;

import com.dekespo.javaserver.bluetooth.ServerManagerThread;

public class JavaServer {
  public static void main(String[] args) {
    System.out.println("Running JavaServer...");
    Thread bluetoothServerThread = new Thread(new ServerManagerThread());
    bluetoothServerThread.start();
  }
}
