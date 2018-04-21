package com.dekespo.remotecomputer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnect {
  private static final int STREAM_BUFFER_LIMIT = 1024;
  private static final String TAG = "BLUETOOTH_CONNECT";
  private final BroadcastReceiver receiver =
      new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
          String action = intent.getAction();
          if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress(); // MAC address
            Log.i(TAG, "Founded a new device " + deviceName + " " + deviceHardwareAddress);
          }
        }
      };
  private boolean isReceiverRegistered = false;
  private Activity activity;
  private Handler handler;
  private BluetoothAdapter adapter;
  private ConnectedThread connectedThread;

  public BluetoothConnect(Activity activity) {
    Log.i(TAG, this.getClass().getName() + " started!");
    this.adapter = BluetoothAdapter.getDefaultAdapter();
    if (this.adapter == null) {
      Log.e(TAG, "Device doesn't support Bluetooth");
    }

    this.activity = activity;
    if (!this.adapter.isEnabled()) {
      int requestActivityCode = 0;
      this.activity.startActivityForResult(
          new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), requestActivityCode);
    }

    Set<BluetoothDevice> pairedDevices = this.adapter.getBondedDevices();
    if (pairedDevices.size() > 0) {
      // There are paired devices. Get the name and address of each paired device.
      for (BluetoothDevice device : pairedDevices) {
        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress(); // MAC address
        Log.i(TAG, "Already paired device " + deviceName + " " + deviceHardwareAddress);
      }
    }

    // Register for broadcasts when a device is discovered.
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    this.activity.registerReceiver(this.receiver, filter);
    this.isReceiverRegistered = true;

    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
    this.activity.startActivity(discoverableIntent);
  }

  public void disconnect() {
    if (isReceiverRegistered) {
      this.activity.unregisterReceiver(this.receiver);
      this.isReceiverRegistered = false;
    }
  }

  //  public void connnect() {
  //    (new ConnectedThread()).
  //  }

  private interface MessageConstants {
    int MESSAGE_READ = 0;
    int MESSAGE_WRITE = 1;
    int MESSAGE_TOAST = 2;
  }

  private class AcceptThread extends Thread {
    private final BluetoothServerSocket serverSocket;

    public AcceptThread() {
      // Use a temporary object that is later assigned to serverSocket
      // because serverSocket is final.
      BluetoothServerSocket tmp = null;
      try {
        // MY_UUID is the app's UUID string, also used by the client code.
        tmp = adapter.listenUsingRfcommWithServiceRecord("DEKE_REMOTE_COMPUTER", UUID.randomUUID());
      } catch (IOException e) {
        Log.e(TAG, "Socket's listen() method failed", e);
      }
      this.serverSocket = tmp;
    }

    @Override
    public void run() {
      BluetoothSocket socket = null;
      // Keep listening until exception occurs or a socket is returned.
      while (true) {
        try {
          socket = this.serverSocket.accept();
        } catch (IOException e) {
          Log.e(TAG, "Socket's accept() method failed", e);
          break;
        }

        if (socket != null) {
          // A connection was accepted. Perform work associated with
          // the connection in a separate thread.
          connectedThread = new ConnectedThread(socket);
          try {
            this.serverSocket.close();
          } catch (IOException e) {
            Log.e(TAG, e.toString());
          }
          break;
        }
      }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
      try {
        serverSocket.close();
      } catch (IOException e) {
        Log.e(TAG, "Could not close the connect socket", e);
      }
    }
  }

  private class ConnectThread extends Thread {
    private final BluetoothSocket socket;
    private final BluetoothDevice mmDevice;

    public ConnectThread(BluetoothDevice device) {
      // Use a temporary object that is later assigned to socket
      // because socket is final.
      BluetoothSocket tmp = null;
      mmDevice = device;

      try {
        // Get a BluetoothSocket to connect with the given BluetoothDevice.
        // MY_UUID is the app's UUID string, also used in the server code.
        // TODO, check how to add the correct UUID
        tmp = device.createRfcommSocketToServiceRecord(UUID.randomUUID());
      } catch (IOException e) {
        Log.e(TAG, "Socket's create() method failed", e);
      }
      this.socket = tmp;
    }

    public void run() {
      // Cancel discovery because it otherwise slows down the connection.
      adapter.cancelDiscovery();

      try {
        // Connect to the remote device through the socket. This call blocks
        // until it succeeds or throws an exception.
        this.socket.connect();
      } catch (IOException connectException) {
        // Unable to connect; close the socket and return.
        try {
          this.socket.close();
        } catch (IOException closeException) {
          Log.e(TAG, "Could not close the client socket", closeException);
        }
        return;
      }

      // The connection attempt succeeded. Perform work associated with
      // the connection in a separate thread.
      connectedThread = new ConnectedThread(this.socket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
      try {
        this.socket.close();
      } catch (IOException e) {
        Log.e(TAG, "Could not close the client socket", e);
      }
    }
  }

  private class ConnectedThread extends Thread {
    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private byte[] streamBuffer;

    public ConnectedThread(BluetoothSocket socket) {
      this.socket = socket;
      InputStream tmpIn = null;
      OutputStream tmpOut = null;

      try {
        tmpIn = socket.getInputStream();
      } catch (IOException e) {
        Log.e(TAG, "Error occurred when creating input stream", e);
      }
      try {
        tmpOut = socket.getOutputStream();
      } catch (IOException e) {
        Log.e(TAG, "Error occurred when creating output stream", e);
      }

      this.inputStream = tmpIn;
      this.outputStream = tmpOut;
    }

    @Override
    public void run() {
      this.streamBuffer = new byte[STREAM_BUFFER_LIMIT];

      while (true) {
        try {
          int numberOfBytesReturned;
          numberOfBytesReturned = this.inputStream.read(this.streamBuffer);
          Message readMsg =
              handler.obtainMessage(
                  MessageConstants.MESSAGE_READ, numberOfBytesReturned, -1, this.streamBuffer);
          readMsg.sendToTarget();
        } catch (IOException e) {
          Log.d(TAG, "Input stream was disconnected", e);
          break;
        }
      }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(byte[] bytes) {
      try {
        this.outputStream.write(bytes);

        Message writtenMsg =
            handler.obtainMessage(MessageConstants.MESSAGE_WRITE, -1, -1, this.streamBuffer);
        writtenMsg.sendToTarget();
      } catch (IOException e) {
        Log.e(TAG, "Error occurred when sending data", e);

        // Send a failure message back to the activity.
        Message writeErrorMsg = handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString("toast", "Couldn't send data to the other device");
        writeErrorMsg.setData(bundle);
        handler.sendMessage(writeErrorMsg);
      }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
      try {
        this.socket.close();
      } catch (IOException e) {
        Log.e(TAG, "Could not close the connect socket", e);
      }
    }
  }
}
