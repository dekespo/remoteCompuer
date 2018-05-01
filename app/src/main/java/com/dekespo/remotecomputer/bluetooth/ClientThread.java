package com.dekespo.remotecomputer.bluetooth;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class ClientThread extends Thread {
  private static final String TAG = "BLUETOOTH_CONNECT";
  private static final int STREAM_BUFFER_LIMIT = 1024;
  private final SocketManagerThread socketManagerThread;
  private final InputStream inputStream;
  private final OutputStream outputStream;

  public ClientThread(SocketManagerThread socketManagerThread) {
    this.socketManagerThread = socketManagerThread;

    this.inputStream = this.socketManagerThread.getInputStream();
    this.outputStream = this.socketManagerThread.getOutputStream();
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

  public void send(String commandName) {
    try {
      byte[] bytesToSend = commandName.getBytes(Charset.defaultCharset());
      this.outputStream.write(bytesToSend);
    } catch (IOException e) {
      Log.e(TAG, "Error occurred when sending data", e);
      this.socketManagerThread.close();
    }
  }
}
