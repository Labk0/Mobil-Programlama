package com.example.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button btn_add, btn_show, btn_delete, btn_update;
    EditText txt_name, txt_surname, txt_age, txt_city;
    ListView kayitlar;
    private Veritabani v1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        v1 = new Veritabani(this);
        btn_add = findViewById(R.id.btn_add);
        btn_show = findViewById(R.id.btn_show);
        btn_delete = findViewById(R.id.btn_delete);
        btn_update = findViewById(R.id.btn_update);

        txt_name = findViewById(R.id.txt_name);
        txt_surname = findViewById(R.id.txt_surname);
        txt_age = findViewById(R.id.txt_age);
        txt_city = findViewById(R.id.txt_city);

        kayitlar = findViewById(R.id.kayitlar);

        btn_add.setOnClickListener(view -> {
            String ad = txt_name.getText().toString();
            String soyad = txt_surname.getText().toString();
            String yas = txt_age.getText().toString();
            String sehir = txt_city.getText().toString();

            Log.d(TAG, "Kayıt ekle butonuna basıldı.");

            KayitEkle(ad, soyad, yas, sehir);

            txt_name.setText("");
            txt_surname.setText("");
            txt_age.setText("");
            txt_city.setText("");
        });

        btn_show.setOnClickListener(view -> {
            Log.d(TAG, "Kayıtları göster butonuna basıldı.");
            KayitlariGoster(KayitlariGetir());
        });

        btn_delete.setOnClickListener(view -> {
            Log.d(TAG, "Kayıt sil butonuna basıldı.");
            String ad = txt_name.getText().toString();
            KayitSil(ad);
        });

        btn_update.setOnClickListener(view -> {
            Log.d(TAG, "Kayıt güncelle butonuna basıldı.");
            String ad = txt_name.getText().toString();
            String soyad = txt_surname.getText().toString();
            String yas = txt_age.getText().toString();
            String sehir = txt_city.getText().toString();
            KayitGuncelle(ad, soyad, yas, sehir);
        });
    }

    private String[] sutunlar = {"ad", "soyad", "yas", "sehir"};
    private Cursor KayitlariGetir() {
        SQLiteDatabase db = v1.getWritableDatabase();
        return db.query("OgrenciBilgi", sutunlar, null, null, null, null, null);
    }
    private void KayitlariGoster(Cursor goster){
        ArrayList<String> kayitlarList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try{
            while(goster.moveToNext()){
                sb.setLength(0);
                int columnIndexAd = goster.getColumnIndexOrThrow("ad");
                int columnIndexSoyad = goster.getColumnIndexOrThrow("soyad");
                int columnIndexYas = goster.getColumnIndexOrThrow("yas");
                int columnIndexSehir = goster.getColumnIndexOrThrow("sehir");

                String ad = goster.getString(columnIndexAd);
                String soyad = goster.getString(columnIndexSoyad);
                String yas = goster.getString(columnIndexYas);
                String sehir = goster.getString(columnIndexSehir);

                sb.append("Ad: ").append(ad).append("\n");
                sb.append("Soyad: ").append(soyad).append("\n");
                sb.append("Yas: ").append(yas).append("\n");
                sb.append("Sehir: ").append(sehir).append("\n");
                kayitlarList.add(sb.toString());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, kayitlarList);
            kayitlar.setAdapter(adapter);
        }catch (Exception e) {
            Log.d(TAG, "Kayıtları gösterme sırasında hata oluştu: " + e.getMessage());
        }finally {
            goster.close();
        }
    }
    private void KayitEkle(String ad, String soyad, String yas, String sehir) {
        try{
            SQLiteDatabase db = v1.getWritableDatabase();
            ContentValues veriler = new ContentValues();
            veriler.put("ad", ad);
            veriler.put("soyad", soyad);
            veriler.put("yas", Integer.parseInt(yas));
            veriler.put("sehir", sehir);
            db.insertOrThrow("OgrenciBilgi", null, veriler);
            db.close();
            Log.d(TAG, "Kayıtlar eklendi.");
        } catch (Exception e){
            Log.d(TAG, "Kayıt ekleme sırasında hata oluştu: " + e.getMessage());
        }
    }
    private void KayitSil(String ad){
        try{
            SQLiteDatabase db = v1.getWritableDatabase();
            int rows = db.delete("OgrenciBilgi", "ad=?", new String[]{ad});
            db.close();
            Log.d(TAG, rows + " kayıt silindi.");
        }catch (Exception e){
            Log.d(TAG, "Kayıt silme sırasında hata oluştu: " + e.getMessage());
        }
    }
    private void KayitGuncelle(String ad, String soyad, String yas, String sehir){
        try{
            SQLiteDatabase db = v1.getWritableDatabase();
            ContentValues cvGuncelle = new ContentValues();
            cvGuncelle.put("soyad", soyad);
            cvGuncelle.put("yas", Integer.parseInt(yas));
            cvGuncelle.put("sehir", sehir);
            int rows = db.update("OgrenciBilgi", cvGuncelle, "ad=?", new String[]{ad});
            db.close();
            Log.d(TAG, rows + " kayıt güncellendi.");
        }catch (Exception e){
            Log.d(TAG, "Kayıt güncelleme sırasında hata oluştu: " + e.getMessage());
        }
    }
}