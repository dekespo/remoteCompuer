package com.dekespo.remotecomputer.bluetooth;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class ClientThread extends Thread {
  private static final String TAG = "BLUETOOTH_CONNECT";
  private static final int STREAM_BUFFER_LIMIT = 1024;
  private final SocketManagerThread socketManagerThread;
  private final InputStream inputStream;

  public ClientThread(SocketManagerThread socketManagerThread) {
    this.socketManagerThread = socketManagerThread;

    InputStream mutableInputStream = this.socketManagerThread.getInputStream();
    //    OutputStream mutableOutputStream = this.socketManagerThread.getOutputStream();

    this.inputStream = mutableInputStream;
    //    this.outputStream = mutableOutputStream;
  }

  @Override
  public void run() {
    byte[] streamBuffer = new byte[STREAM_BUFFER_LIMIT];

    while (true) {
      try {
        int numberOfBytesReturned = this.inputStream.read(streamBuffer);
        Log.i(TAG, "I got these many bytes = " + Integer.toString(numberOfBytesReturned));
      } catch (IOException e) {
        Log.e(TAG, "Input stream was disconnected", e);
        this.socketManagerThread.close();
        break;
      }
    }
  }
}
