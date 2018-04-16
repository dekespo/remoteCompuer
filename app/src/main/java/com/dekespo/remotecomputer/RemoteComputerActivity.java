package com.dekespo.remotecomputer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RemoteComputerActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_computer);

        Button button = findViewById(R.id.bluetooth_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            }
        });
    }
}
