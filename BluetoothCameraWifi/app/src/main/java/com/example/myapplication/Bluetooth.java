package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
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

        bAc = (Button) findViewById(R.id.btnBlueAc);
        bKapa = (Button) findViewById(R.id.btnBlueKapa);
        bList = (Button) findViewById(R.id.bntBlueList);
        bOpe = (Button) findViewById(R.id.btnBlueOpe);
        btnBack = (Button) findViewById(R.id.btnBack);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView) findViewById(R.id.liste);

        if(BA == null){
            Toast.makeText(getApplicationContext(),"Bluetooth cihazda desteklenmiyor.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            checkAndRequestPermission();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void checkAndRequestPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT
            }, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            initializeBluetooth();
        }
    }

    private void initializeBluetooth(){
        bAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on(v);
            }
        });
        bKapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                off(v);
            }
        });
        bList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list(v);
            }
        });
        bOpe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeVisible(v);
            }
        });
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
                BA.disable();
                Toast.makeText(this, "Bluetooth kapatıldı", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth zaten açık", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bluetooth kapatma izni verilmedi", Toast.LENGTH_SHORT).show();
            checkAndRequestPermission();
        }
    }
    public void list(View v){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
            pairedDevices = BA.getBondedDevices();
            ArrayList<String> list  = new ArrayList<>();
            for (BluetoothDevice bt : pairedDevices){
                list.add(bt.getName() + "\n" + bt.getAddress());
            }

            if(list.size() > 0){
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
                lv.setAdapter(adapter);
            } else {
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
            }else {
            Toast.makeText(this, "Cihazı görünür yapmak için izin verilmedi", Toast.LENGTH_SHORT).show();
            checkAndRequestPermission();
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeBluetooth();
            } else {
                Toast.makeText(this, "Bluetooth işlemi için izin verilmedi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
