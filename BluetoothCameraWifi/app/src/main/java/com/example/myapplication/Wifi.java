package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Wifi extends AppCompatActivity {
    private WifiManager modem;
    private ToggleButton btnToggle;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_wifi);

        modem = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Button btnBack = findViewById(R.id.btnBack);
        btnToggle = (ToggleButton) findViewById(R.id.toggleButton);

         if(checkWifiPermissions()){
            setInitialToggleState();
            setToggleListener();
        } else {
            requestWifiPermissions();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private boolean checkWifiPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {//android 12 and above
            return ContextCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) == PackageManager.PERMISSION_GRANTED;
        } else{
            int changeWifiStatePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE);
            int accessWifiStatePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
            return changeWifiStatePermission == PackageManager.PERMISSION_GRANTED && accessWifiStatePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestWifiPermissions(){
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions = new String[]{Manifest.permission.NEARBY_WIFI_DEVICES};
        } else {
            permissions = new String[]{Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE};
        }
        ActivityCompat.requestPermissions(this, permissions, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean permissionsGranted = true;
            if (grantResults.length > 0){
                for (int result : grantResults){
                    if (result != PackageManager.PERMISSION_GRANTED){
                        permissionsGranted = false;
                        break;
                    }
                }
            } else {
                permissionsGranted = false;
            }
            if (permissionsGranted){
                setInitialToggleState();
                setToggleListener();
            } else {
                Toast.makeText(this, "Wifi izni verilmedi", Toast.LENGTH_SHORT).show();
                btnToggle.setEnabled(false);
            }
        }
    }

    private void setInitialToggleState() {
        if (modem != null){
            btnToggle.setChecked(modem.isWifiEnabled());
        }
    }

    private void setToggleListener() {
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modem != null){
                    if (btnToggle.isChecked()){
                        wifiAc();
                    } else{
                        wifiKapa();
                    }
                }
            }
        });
    }
    private void wifiKapa(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ için
            Intent panelIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            startActivity(panelIntent);
            Toast.makeText(this, "Lütfen Wifi'yi manuel olarak kapatın", Toast.LENGTH_SHORT).show();
        } else {
            if (modem != null && modem.isWifiEnabled()){
                boolean success = modem.setWifiEnabled(false);
                if (!success) {
                    Toast.makeText(this, "Wifi kapatılamadı", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void wifiAc(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ için
            Intent panelIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            startActivity(panelIntent);
            Toast.makeText(this, "Lütfen Wifi'yi manuel olarak açın", Toast.LENGTH_SHORT).show();
        } else {
            if (modem != null && !modem.isWifiEnabled()){
                boolean success = modem.setWifiEnabled(true);
                if (!success) {
                    Toast.makeText(this, "Wifi açılamadı", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
