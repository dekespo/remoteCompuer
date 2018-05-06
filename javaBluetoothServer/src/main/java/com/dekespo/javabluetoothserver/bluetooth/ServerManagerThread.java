package com.dekespo.javabluetoothserver.bluetooth;

import com.dekespo.commonclasses.IStreamConnection;
import com.dekespo.commonclasses.ManagerThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class ServerManagerThread extends ManagerThread {
  private StreamConnectionNotifier notifier;
  private StreamConnection streamConnection = null;

  public ServerManagerThread() {
    try {
      LocalDevice local = LocalDevice.getLocalDevice();
      local.setDiscoverable(DiscoveryAgent.GIAC);

      UUID uuid = new UUID(91201379); // "056f9f63-0000-1000-8000-00805f9b34fb"
      String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
      this.notifier = (StreamConnectionNotifier) Connector.open(url);
    } catch (Exception e) {
      System.out.println("Make sure that the machine has bluetooth and it is turned on.");
      e.printStackTrace();
      return;
    }

    System.out.println("waiting for connection...");
  }

  @Override
  public void run() {
    while (true) {
      try {
        this.streamConnection = this.notifier.acceptAndOpen();

        Thread processThread = new Thread(new ServerConnectionThread(JavaIOStreamConnection()));
        processThread.start();
      } catch (Exception e) {
        e.printStackTrace();
        try {
          this.streamConnection.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        } finally {
          this.streamConnection = null;
        }
        break;
      }
    }
  }

  @Override
  protected IStreamConnection JavaIOStreamConnection() {
    return new IStreamConnection() {
      @Override
      public InputStream openInputStream() throws IOException {
        return streamConnection.openInputStream();
      }

      @Override
      public OutputStream openOutputStream() throws IOException {
        return streamConnection.openOutputStream();
      }

      @Override
      public void closeStream() throws IOException {
        streamConnection.close();
      }

      @Override
      public boolean isConnected() {
        return streamConnection != null;
      }
    };
  }
}
