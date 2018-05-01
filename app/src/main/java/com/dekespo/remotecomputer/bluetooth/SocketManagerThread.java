package com.dekespo.remotecomputer.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class SocketManagerThread extends Thread {
  private static final String TAG = "BLUETOOTH_CONNECT";
  private final BluetoothSocket socket;

  public SocketManagerThread(BluetoothDevice device) {
    Log.i(TAG, "Starting SocketManagerThread");

    BluetoothSocket mutableSocket = null;
    try {
      mutableSocket =
          device.createRfcommSocketToServiceRecord(
              UUID.fromString("056f9f63-0000-1000-8000-00805f9b34fb"));
    } catch (IOException e) {
      Log.e(TAG, "Socket's create() method failed", e);
      close();
    }
    this.socket = mutableSocket;
  }

  @Override
  public void run() {
    try {
      this.socket.connect();
    } catch (IOException e) {
      Log.e(TAG, "Socket could not start", e);
      return;
    }
  }

  public void close() {
    try {
      Log.w(TAG, "Bluetooth socket is being closed");
      this.socket.close();
    } catch (IOException closeException) {
      Log.e(TAG, "Could not close the client socket", closeException);
    }
  }

  public InputStream getInputStream() {
    try {
      return this.socket.getInputStream();
    } catch (IOException e) {
      e.printStackTrace();
      close();
      return null;
    }
  }

  public OutputStream getOutputStream() {
    try {
      return this.socket.getOutputStream();
    } catch (IOException e) {
      e.printStackTrace();
      close();
      return null;
    }
  }

  public boolean isConnected() {
    return this.socket.isConnected();
  }

  public void waitForMe() {
    while (!isConnected()) {
      try {
        Thread.sleep(100);
        Log.w(TAG, "Waiting for the bluetooth socket to be connected");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
