package com.example.myapplication;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class Wifi extends AppCompatActivity {
    private WifiManager modem;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_wifi);

        modem = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        final ToggleButton btn = (ToggleButton) findViewById(R.id.toggleButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn.isChecked()){
                    wifiAc();
                }else{
                    wifiKapa();
                }
            }
        });
    }
    private void wifiKapa(){
        if (modem.isWifiEnabled()){
            modem.setWifiEnabled(false);
            Toast.makeText(Wifi.this, "Wifi kapalı", Toast.LENGTH_SHORT).show();
        }
    }

    private void wifiAc(){
        if (modem.isWifiEnabled()){
            modem.setWifiEnabled(true);
            Toast.makeText(Wifi.this, "Wifi açık", Toast.LENGTH_SHORT).show();
        }
    }
}
