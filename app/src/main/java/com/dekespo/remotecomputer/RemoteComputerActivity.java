package com.dekespo.remotecomputer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.dekespo.commonclasses.MouseMessage;
import com.dekespo.remotecomputer.bluetooth.BluetoothActivity;

public class RemoteComputerActivity extends AppCompatActivity {
  private final String TAG = "REMOTE_COMPUTER";
  private ImageView screen;
  private BluetoothActivity bluetoothActivity;
  private Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_remote_computer);

    this.bluetoothActivity = new BluetoothActivity(this);
    this.screen = findViewById(R.id.screen);
    this.context = this.getApplicationContext();

    Button button = findViewById(R.id.bluetooth_button);
    button.setOnClickListener(
        new View.OnClickListener() {
          boolean isImageOn = false;

          @SuppressLint("ClickableViewAccessibility")
          @Override
          public void onClick(View v) {
            if (!this.isImageOn) {
              screen.setImageResource(R.drawable.ic_launcher_background);
              this.isImageOn = true;
              bluetoothActivity.connnect();
              final GestureDetector detector =
                  new GestureDetector(context, new GestureTap(bluetoothActivity));
              screen.setOnTouchListener(
                  new View.OnTouchListener() {
                    float firstXPosition = 0.f;
                    float firstYPosition = 0.f;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                      int index = event.getActionIndex();
                      int action = event.getActionMasked();
                      int pointerId = event.getPointerId(index);

                      detector.onTouchEvent(event);

                      switch (action) {
                        case MotionEvent.ACTION_DOWN:
                          this.firstXPosition = event.getX(pointerId);
                          this.firstYPosition = event.getY(pointerId);
                          break;
                        case MotionEvent.ACTION_MOVE:
                          bluetoothActivity.sendMouseCommand(
                              MouseMessage.MouseCommand.MOVE,
                              event.getX(pointerId) - this.firstXPosition,
                              event.getY(pointerId) - this.firstYPosition,
                              false);
                          break;
                        case MotionEvent.ACTION_UP:
                          bluetoothActivity.sendMouseCommand(
                              MouseMessage.MouseCommand.MOVE,
                              event.getX(pointerId) - this.firstXPosition,
                              event.getY(pointerId) - this.firstYPosition,
                              true);
                          break;
                        default:
                          Log.d(TAG, "Undefined MotionEvent value " + Integer.toString(action));
                      }
                      return true;
                    }
                  });

            } else {
              screen.setImageResource(0);
              screen.setOnTouchListener(null);
              this.isImageOn = false;
              bluetoothActivity.disconnect();
            }
          }
        });
  }

  class GestureTap extends GestureDetector.SimpleOnGestureListener {
    BluetoothActivity blueToothActivtiy = null;

    GestureTap(BluetoothActivity blueToothActivtiy) {
      this.blueToothActivtiy = blueToothActivtiy;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
      this.blueToothActivtiy.sendMouseCommand(
          MouseMessage.MouseCommand.DOUBLE_CLICK, 0.f, 0.f, false);
      return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
      this.blueToothActivtiy.sendMouseCommand(MouseMessage.MouseCommand.CLICK, 0.f, 0.f, false);
      return true;
    }
  }
}
