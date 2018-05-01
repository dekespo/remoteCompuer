package com.dekespo.javabluetoothserver;

import java.io.InputStream;

import javax.microedition.io.StreamConnection;

public class BluetoothServerCommandsThread extends Thread {
  private static final int EXIT_COMMAND = -1;
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

        if (command == EXIT_COMMAND) {
          System.out.println("The device is disconnected");
          break;
        } else if (command == (int) 'S') System.out.println("Asked for the screen");
        else System.out.println("Unknown command sent: " + (char) command);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void processCommand(int command) {
    System.out.println("This is the command " + Integer.toString(command));
  }
}
