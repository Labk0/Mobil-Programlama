package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Set;

public class Bluetooth extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;

    Button bAc, bKapa, bList, bOpe, btnBack;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView lv;

    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        bAc = findViewById(R.id.btnBlueAc);
        bKapa = findViewById(R.id.btnBlueKapa);
        bList = findViewById(R.id.bntBlueList);
        bOpe = findViewById(R.id.btnBlueOpe);
        btnBack = findViewById(R.id.btnBack);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = findViewById(R.id.liste);

        if (BA == null){
            Toast.makeText(getApplicationContext(),"Bluetooth cihazda desteklenmiyor.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            checkAndRequestPermission();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void checkAndRequestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 ve sonrası için
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                }, REQUEST_BLUETOOTH_PERMISSION);
            } else {
                initializeBluetooth();
            }
        } else {
            // Android 11 ve öncesi için
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                }, REQUEST_BLUETOOTH_PERMISSION);
            } else {
                initializeBluetooth();
            }
        }
    }

    private void initializeBluetooth(){
        bAc.setOnClickListener(v -> on(v));
        bKapa.setOnClickListener(v -> off(v));
        bList.setOnClickListener(v -> list(v));
        bOpe.setOnClickListener(v -> makeVisible(v));
    }

    public void on(View v){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
            if (!BA.isEnabled()){
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);
                Toast.makeText(this, "Bluetooth açıldı", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth zaten açık", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bluetooth açma izni verilmedi", Toast.LENGTH_SHORT).show();
            checkAndRequestPermission();
        }
    }

    public void off(View v){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
            if (BA.isEnabled()){
                //Android 13 ten sonra bluetooth kapatılamıyor
                Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                Toast.makeText(this, "Lütfen Bluetooth'u manuel olarak kapatın", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Bluetooth zaten kapalı", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bluetooth kapatma izni verilmedi", Toast.LENGTH_SHORT).show();
            checkAndRequestPermission();
        }
    }


    public void list(View v){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){

            if (!BA.isEnabled()) {
                lv.setAdapter(null);
                Toast.makeText(this, "Bluetooth kapalı.", Toast.LENGTH_SHORT).show();
                return;
            }

            pairedDevices = BA.getBondedDevices();
            ArrayList<String> list  = new ArrayList<>();
            for (BluetoothDevice bt : pairedDevices){
                list.add(bt.getName() + "\n" + bt.getAddress());
            }

            if (list.size() > 0){
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
                lv.setAdapter(adapter);
            } else {
                lv.setAdapter(null);
                Toast.makeText(this, "Eşleştirilmiş cihaz yok", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bluetooth listesini görüntülemek için izin verilmedi", Toast.LENGTH_SHORT).show();
            checkAndRequestPermission();
        }
    }

    public void makeVisible(View v){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED){
            Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(getVisible, 0);
            Toast.makeText(this, "Cihaz görünür hale getirildi", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cihazı görünür yapmak için izin verilmedi", Toast.LENGTH_SHORT).show();
            checkAndRequestPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                initializeBluetooth();
            } else {
                Toast.makeText(this, "Bluetooth işlemi için gerekli izinler reddedildi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
