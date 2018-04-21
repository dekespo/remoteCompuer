package com.dekespo.remotecomputer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class RemoteComputerActivity extends AppCompatActivity {
  ImageView screen;
  boolean isImageOn = false;
  BluetoothConnect bluetoothConnection;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_remote_computer);

    bluetoothConnection = new BluetoothConnect(this);

    this.screen = findViewById(R.id.screen);

    Button button = findViewById(R.id.bluetooth_button);
    button.setOnClickListener(
        new View.OnClickListener() {
          public void onClick(View v) {
            if (!isImageOn) {
              screen.setImageResource(R.drawable.ic_launcher_background);
              isImageOn = true;
            } else {
              screen.setImageResource(0);
              isImageOn = false;
              bluetoothConnection.disconnect();
            }
          }
        });
  }
}
