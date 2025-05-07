package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnB = (Button) findViewById(R.id.btnBlue);
        Button btnC = (Button) findViewById(R.id.btnCam);
        Button btnW = (Button) findViewById(R.id.btnWifi);

        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gecisB = new Intent(MainActivity.this, Bluetooth.class);
                startActivity(gecisB);
            }
        });

        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gecisC = new Intent(MainActivity.this, Camera.class);
                startActivity(gecisC);
            }
        });

        btnW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gecisW = new Intent(MainActivity.this, Wifi.class);
                startActivity(gecisW);
            }
        });


    }
}