package com.example.plaka_sehir;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SehirInfoActivity extends AppCompatActivity {

    private String sehir;
    private int plaka;
    private int sehirIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sehirinfo);

        // TextView'leri tanımla
        TextView textPlaka = findViewById(R.id.textPlaka);
        TextView textSehir = findViewById(R.id.textSehir);

        sehir = getIntent().getStringExtra("sehir");
        plaka = getIntent().getIntExtra("plaka", -1);
        sehirIndex = getIntent().getIntExtra("sehirIndex", -1);

        textSehir.setText("Sehir: " + sehir);
        textPlaka.setText("Plaka: " + plaka);

        // Doğru plaka kodunu kontrol et
        if (kontrol(sehirIndex, plaka)) {
            Toast.makeText(this, "Doğru!", Toast.LENGTH_SHORT).show();
        } else {
            int dogruPlaka= getDogruPlaka(sehirIndex);
            Toast.makeText(this, "Yanlış! Doğru plaka: " + dogruPlaka, Toast.LENGTH_LONG).show();
        }

        Button btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> finish());
    }

    private boolean kontrol(int sehirIndex, int plaka) {
        return getDogruPlaka(sehirIndex) == plaka;
    }

    private int getDogruPlaka(int sehirIndex) {

        String[] turkiyeSehirleri = {
                "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Aksaray",
                "Amasya", "Ankara", "Antalya", "Ardahan", "Artvin",
                "Aydın", "Balıkesir", "Bartın", "Batman", "Bayburt",
                "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur",
                "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
                "Diyarbakır", "Düzce", "Edirne", "Elazığ", "Erzincan",
                "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane",
                "Hakkari", "Hatay", "Iğdır", "Isparta", "İstanbul",
                "İzmir", "Kahramanmaraş", "Karabük", "Karaman", "Kars",
                "Kastamonu", "Kayseri", "Kırıkkale", "Kırklareli", "Kırşehir",
                "Kilis", "Kocaeli", "Konya", "Kütahya", "Malatya",
                "Manisa", "Mardin", "Mersin", "Muğla", "Muş",
                "Nevşehir", "Niğde", "Ordu", "Osmaniye", "Rize",
                "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas",
                "Şanlıurfa", "Şırnak", "Tekirdağ", "Tokat", "Trabzon",
                "Tunceli", "Uşak", "Van", "Yalova", "Yozgat", "Zonguldak"
        };

        int[] plakaKodlari = {
                1, 2, 3, 4, 68, 5, 6, 7, 75, 8,
                9, 10, 74, 72, 69, 11, 12, 13, 14, 15,
                16, 17, 18, 19, 20, 21, 81, 22, 23, 24,
                25, 26, 27, 28, 29, 30, 31, 76, 32, 34,
                35, 46, 78, 70, 36, 37, 38, 71, 39, 40,
                79, 41, 42, 43, 44, 45, 47, 33, 48, 49,
                50, 51, 52, 80, 53, 54, 55, 56, 57, 58,
                63, 73, 59, 60, 61, 62, 64, 65, 77, 66, 67
        };

        for (int i = 0; i < turkiyeSehirleri.length; i++) {
            if (turkiyeSehirleri[i].equals(sehir)) {
                return plakaKodlari[i];
            }
        }

        return -1;
    }
}
