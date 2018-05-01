package com.dekespo.javabluetoothserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.microedition.io.StreamConnection;

public class BluetoothServerCommandsThread extends Thread {
  private StreamConnection streamConnection;
  private InputStream inputStream;
  private OutputStream outputStream;

  public BluetoothServerCommandsThread(StreamConnection connection) {
    this.streamConnection = connection;
  }

  @Override
  public void run() {
    try {

      if (!openStream()) return;

      // TODO: Get some ID of the connected device
      System.out.println("A device is connected");

      while (true) {
        int command = this.inputStream.read();
        if (command == -1) { // -1 means exit
          System.out.println("The device is disconnected");
          break;
        }
        processCommand(command);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    closeStream();
  }

  private void processCommand(int command) {
    switch ((char) command) {
      case 'S':
        System.out.println("Asked for the screen");
        byte[] serverBytes = "Cl".getBytes(Charset.defaultCharset());
        try {
          this.outputStream.write(serverBytes);
          System.out.println("Server sends bytes");
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      default:
        System.out.println("Unknown command received: " + (char) command);
    }
  }

  private boolean openStream() {
    try {
      this.inputStream = this.streamConnection.openInputStream();
      this.outputStream = this.streamConnection.openOutputStream();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private void closeStream() {
    try {
      this.inputStream.close();
      this.outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
