package com.dekespo.remotecomputer.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class ClientThread extends Thread {
  private static final String TAG = "BLUETOOTH_CONNECT";
  private final SocketManagerThread socketManagerThread;
  private final InputStream inputStream;
  private final OutputStream outputStream;

  public ClientThread(BluetoothDevice device) {
    this.socketManagerThread = new SocketManagerThread(device);
    this.socketManagerThread.start();
    this.socketManagerThread.waitForMe();

    this.inputStream = this.socketManagerThread.getInputStream();
    this.outputStream = this.socketManagerThread.getOutputStream();
  }

  @Override
  public void run() {
    while (this.socketManagerThread.isConnected()) {
      try {
        int command = this.inputStream.read();
        Log.i(TAG, "Got this characther " + (char) command);
      } catch (IOException e) {
        Log.e(TAG, "Input stream was disconnected", e);
        this.socketManagerThread.close();
        break;
      }
    }
  }

  public void send(String commandName) {
    try {
      byte[] bytesToSend = commandName.getBytes(Charset.defaultCharset());
      this.outputStream.write(bytesToSend);
    } catch (IOException e) {
      Log.e(TAG, "Error occurred when sending data", e);
      this.socketManagerThread.close();
    }
  }

  public void close() {
    this.socketManagerThread.close();
    try {
      this.socketManagerThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
