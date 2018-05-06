package com.dekespo.remotecomputer.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.dekespo.commonclasses.IStreamConnection;
import com.dekespo.commonclasses.ManagerThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class SocketManagerThread extends ManagerThread {
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
    }
    this.socket = mutableSocket;
  }

  @Override
  public void run() {
    try {
      this.socket.connect();
    } catch (IOException e) {
      Log.e(TAG, "Socket could not start", e);
    }
  }

  @Override
  protected IStreamConnection JavaIOStreamConnection() {
    return new IStreamConnection() {
      @Override
      public InputStream openInputStream() throws IOException {
        return socket.getInputStream();
      }

      @Override
      public OutputStream openOutputStream() throws IOException {
        return socket.getOutputStream();
      }

      @Override
      public void closeStream() throws IOException {
        socket.close();
      }

      @Override
      public boolean isConnected() {
        return socket.isConnected();
      }
    };
  }

  public void waitForMe() {
    while (!this.socket.isConnected()) {
      try {
        Thread.sleep(100);
        Log.w(TAG, "Waiting for the bluetooth socket to be connected");
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
