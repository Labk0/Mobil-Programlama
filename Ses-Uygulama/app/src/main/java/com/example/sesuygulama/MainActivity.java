package com.example.sesuygulama;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerKategori;
    RecyclerView recyclerViewSesler;
    FloatingActionButton fabEkle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerKategori = findViewById(R.id.spinnerKategori);
        recyclerViewSesler = findViewById(R.id.recyclerViewSesler);
        fabEkle = findViewById(R.id.fabEkle);

        String[] kategoriler = {"Hepsi", "Hayvan Sesleri", "Mizah", "Doğa", "Özel"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, kategoriler
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKategori.setAdapter(adapter);
    }
}