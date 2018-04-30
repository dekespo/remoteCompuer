package com.dekespo.javabluetoothserver;

import java.io.InputStream;

import javax.microedition.io.StreamConnection;

public class BluetoothServerCommandsThread extends Thread {
  // Constant that indicate command from devices
  private static final int EXIT_CMD = -1;
  private StreamConnection streamConnection;

  public BluetoothServerCommandsThread(StreamConnection connection) {
    this.streamConnection = connection;
  }

  @Override
  public void run() {
    try {
      // prepare to receive data
      InputStream inputStream = this.streamConnection.openInputStream();

      // TODO: Get some ID of the connected device
      System.out.println("A device is connected");

      while (true) {
        int command = inputStream.read();

        processCommand(command);

        if (command == EXIT_CMD) {
          System.out.println("A device is disconnected");
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void processCommand(int command) {
    System.out.println("This is the command " + Integer.toString(command));
  }
}
