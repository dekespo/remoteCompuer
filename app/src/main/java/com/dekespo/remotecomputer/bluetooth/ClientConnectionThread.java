package com.dekespo.remotecomputer.bluetooth;

import android.util.Log;

import com.dekespo.commonclasses.ConnectionThread;
import com.dekespo.commonclasses.DataMessage;
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
      DataMessage dataMessage = receiveData();
      if (dataMessage == null) {
        Log.w(TAG, "The stream is disconnected");
        break;
      }
      dataMessage = processData(dataMessage);
      if (dataMessage != null) sendData(dataMessage);
    }

    closeStream();
  }

  @Override
  public DataMessage processData(DataMessage dataMessage) {
    Log.i(TAG, "Got this data " + dataMessage.getData());
    return null;
  }
}
