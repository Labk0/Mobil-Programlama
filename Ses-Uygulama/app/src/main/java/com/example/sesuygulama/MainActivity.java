package com.example.sesuygulama;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final String[] kategoriIsimleri = {"Hepsi", "Hayvan Sesleri", "Mizah", "Doğa", "Özel"};
    private static final int DOSYA_SEC = 1;
    Spinner spinnerKategori;
    RecyclerView recyclerViewSesler;
    FloatingActionButton fabEkle;

    List<Ses> tumSesler = new ArrayList<>();
    SesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerKategori = findViewById(R.id.spinnerKategori);
        recyclerViewSesler = findViewById(R.id.recyclerViewSesler);
        fabEkle = findViewById(R.id.fabEkle);

        tumSesler.add(new Ses("Tatlı Kedi Miyavlama", 1, "android.resource://" + getPackageName() + "/" + R.raw.sweet_kitty_meow));
        tumSesler.add(new Ses("Kurt Uluma", 1, "android.resource://" + getPackageName() + "/" + R.raw.wolf_howl));
        tumSesler.add(new Ses("Kamp Ateşi", 3, "android.resource://" + getPackageName() + "/" + R.raw.campfire));
        tumSesler.add(new Ses("Hafif Yağmur", 3, "android.resource://" + getPackageName() + "/" + R.raw.light_rain));


        //kategoriler
        String[] kategoriler = kategoriIsimleri;
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, kategoriler);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerKategori.setAdapter(spinnerAdapter);

        // Gömülü raw dosyaları kopyala (sadece ilk kez)
        if (getFilesDir().listFiles() == null || getFilesDir().listFiles().length == 0) {
            kopyalaRawDosyasi("sweet_kitty_meow", R.raw.sweet_kitty_meow, 1);
            kopyalaRawDosyasi("wolf_howl", R.raw.wolf_howl, 1);
            kopyalaRawDosyasi("campfire", R.raw.campfire, 3);
            kopyalaRawDosyasi("light_rain", R.raw.light_rain, 3);
        }

        //recyclerview
        recyclerViewSesler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SesAdapter(this, tumSesler, 0);
        recyclerViewSesler.setAdapter(adapter);

        //kategori seçim
        spinnerKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                filtrele(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //ses ekleme
        fabEkle.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("audio/*");
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri sesUri = data.getData();
            getContentResolver().takePersistableUriPermission(sesUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            View view = getLayoutInflater().inflate(R.layout.dialog_input, null);
            EditText adEdit = view.findViewById(R.id.edtSesAdi);
            Spinner kategoriSpin = view.findViewById(R.id.spinnerDialogKategori);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, new String[]{"Hayvan Sesleri", "Mizah", "Doğa", "Özel"});
            kategoriSpin.setAdapter(adapter);

            int secili = spinnerKategori.getSelectedItemPosition();
            if (secili == 0) secili = 4;
            kategoriSpin.setSelection(secili - 1);

            new AlertDialog.Builder(this)
                    .setTitle("Yeni Ses Ekle")
                    .setView(view)
                    .setPositiveButton("Ekle", (d, w) -> {
                        String ad = adEdit.getText().toString().trim();
                        int katId = kategoriSpin.getSelectedItemPosition() + 1;
                        if (!ad.isEmpty()) {
                            File hedef = new File(getFilesDir(), ad + ".mp3");
                            try (InputStream in = getContentResolver().openInputStream(sesUri);
                                 OutputStream out = new FileOutputStream(hedef)) {
                                byte[] buf = new byte[4096];
                                int len;
                                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                            tumSesler.add(new Ses(ad, katId, hedef.getAbsolutePath()));
                            filtrele(spinnerKategori.getSelectedItemPosition());
                        }
                    })
                    .setNegativeButton("İptal", null)
                    .show();
        }
    }

    private void kopyalaRawDosyasi(String ad, int resId, int kategoriId) {
        File hedef = new File(getFilesDir(), ad + ".mp3");
        if (hedef.exists()) return;
        try (InputStream in = getResources().openRawResource(resId);
             OutputStream out = new FileOutputStream(hedef)) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        tumSesler.add(new Ses(ad, kategoriId, hedef.getAbsolutePath()));
    }

    private void filtrele(int kategoriId) {
        if (kategoriId == 0) {
            adapter.guncelle(tumSesler, kategoriId);
            return;
        }

        List<Ses> filtreli = new ArrayList<>();
        for (Ses s : tumSesler) {
            if (s.getKategoriId() == kategoriId) {
                filtreli.add(s);
            }
        }
        adapter.guncelle(filtreli, kategoriId);
    }
}
