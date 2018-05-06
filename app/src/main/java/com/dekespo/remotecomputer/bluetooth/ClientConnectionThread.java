package com.dekespo.remotecomputer.bluetooth;

import android.util.Log;

import com.dekespo.commonclasses.ConnectionThread;
import com.dekespo.commonclasses.IStreamConnection;

public class ClientConnectionThread extends ConnectionThread {
  private final String TAG = "BLUETOOTH_CONNECT";

  public ClientConnectionThread(IStreamConnection connection) {
    super(connection);
    openStream();
  }

  @Override
  public void run() {

    if (!openStream()) return;

    while (true) {
      String data = receiveData();
      if (data == null) {
        Log.w(TAG, "The stream is disconnected");
        break;
      }
      data = processData(data);
      if (data != null) sendData(data);
    }

    closeStream();
  }

  @Override
  public String processData(String data) {
    Log.i(TAG, "Got this data " + data);
    return null;
  }
}
