package com.dekespo.remotecomputer.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Button;

import com.dekespo.remotecomputer.R;

import java.util.HashMap;
import java.util.Set;

public class BluetoothConnection {
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
  private Activity activity;
  private boolean isReceiverRegistered = false;
  private BluetoothAdapter adapter;
  private HashMap<String, String> pairedDevices;
  private ClientThread clientThread;
  private SocketManagerThread socketManagerThread;
  private Button connectionButton;

  public BluetoothConnection(Activity activity) {
    Log.i(TAG, this.getClass().getName() + " started!");
    this.adapter = BluetoothAdapter.getDefaultAdapter();
    if (this.adapter == null) {
      Log.e(TAG, "Device doesn't support Bluetooth");
    }

    this.activity = activity;
    this.connectionButton = this.activity.findViewById(R.id.bluetooth_button);
    if (!this.adapter.isEnabled()) {
      int requestActivityCode = 0;
      this.activity.startActivityForResult(
          new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), requestActivityCode);
    }

    this.pairedDevices = new HashMap<>();
    Set<BluetoothDevice> pairedDevices = this.adapter.getBondedDevices();
    if (pairedDevices.size() > 0) {
      // There are paired devices. Get the name and address of each paired device.
      for (BluetoothDevice device : pairedDevices) {
        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress();
        this.pairedDevices.put("ONLY_THIS_ONE", deviceHardwareAddress);
        Log.i(TAG, "Already paired device " + deviceName + " " + deviceHardwareAddress);
      }
    }

    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
    this.activity.startActivity(discoverableIntent);
  }

  public void disconnect() {
    if (isReceiverRegistered) {
      this.activity.unregisterReceiver(this.receiver);
      this.isReceiverRegistered = false;
      this.socketManagerThread.close();
      this.clientThread = null;
      this.socketManagerThread = null;
      this.connectionButton.setText(R.string.bluetooth_button_connect);
    }
  }

  public void connnect() {
    // Register for broadcasts when a device is discovered.
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    this.activity.registerReceiver(this.receiver, filter);
    this.isReceiverRegistered = true;

    // Cancel discovery because it otherwise slows down the connection.
    this.adapter.cancelDiscovery();

    this.socketManagerThread =
        new SocketManagerThread(this.adapter.getRemoteDevice(pairedDevices.get("ONLY_THIS_ONE")));
    this.socketManagerThread.start();
    this.clientThread = this.socketManagerThread.getClientThread();
    this.clientThread.send("Se"); // S of Screen
    this.connectionButton.setText(R.string.bluetooth_button_disconnect);
  }
}
