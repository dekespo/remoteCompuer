package com.dekespo.javabluetoothserver;

import java.io.InputStream;

import javax.microedition.io.StreamConnection;

public class BluetoothServerCommandsThread extends Thread {
  // Constant that indicate command from devices
  private static final int EXIT_CMD = -1;
  private static final int KEY_RIGHT = 1;
  private static final int KEY_LEFT = 2;
  private StreamConnection mConnection;

  public BluetoothServerCommandsThread(StreamConnection connection) {
    mConnection = connection;
  }

  @Override
  public void run() {
    try {
      // prepare to receive data
      InputStream inputStream = mConnection.openInputStream();

      System.out.println("A device is connected");

      //      while (true) {
      //        int command = inputStream.read();
      //
      //        if (command == EXIT_CMD) {
      //          System.out.println("finish process");
      //          break;
      //        }
      //        processCommand(command);
      //      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //  /**
  //   * Process the command from client
  //   *
  //   * @param command the command code
  //   */
  //  private void processCommand(int command) {
  //    try {
  //      Robot robot = new Robot();
  //      switch (command) {
  //        case KEY_RIGHT:
  //          robot.keyPress(KeyEvent.VK_RIGHT);
  //          System.out.println("Right");
  //          break;
  //        case KEY_LEFT:
  //          robot.keyPress(KeyEvent.VK_LEFT);
  //          System.out.println("Left");
  //          break;
  //      }
  //    } catch (Exception e) {
  //      e.printStackTrace();
  //    }
  //  }
}
