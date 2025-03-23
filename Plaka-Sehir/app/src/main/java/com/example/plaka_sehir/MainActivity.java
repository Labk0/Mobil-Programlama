package com.example.plaka_sehir;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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
        Random rand = new Random();
        ListView listPlaka = (ListView) findViewById(R.id.tabloPlaka);
        ListView listSehir = (ListView) findViewById(R.id.tabloSehir);
        Button button = (Button) findViewById((R.id.button));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<Integer> randomIndexSet = new HashSet<>();

                while (randomIndexSet.size() < 10) {
                    randomIndexSet.add(rand.nextInt(turkiyeSehirleri.length));
                }

                // Set'teki indekslerden şehir ve plaka listesi oluştur
                List<String> randomSehirler = new ArrayList<>();
                List<String> randomPlakalar = new ArrayList<>();

                for (int index : randomIndexSet) {
                    randomSehirler.add(turkiyeSehirleri[index]);
                    randomPlakalar.add(String.valueOf(plakaKodlari[index]));
                }

                // ListView'e verileri aktarma
                ArrayAdapter<String> adapterSehir = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1, randomSehirler);
                ArrayAdapter<String> adapterPlaka = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1, randomPlakalar);

                listSehir.setAdapter(adapterSehir);
                listPlaka.setAdapter(adapterPlaka);
            }
        });
        listSehir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //2.ekran eklenip sehir doğruysa toastla doğru uyarısı yanlışsa yeni
                // ekranda doğrusuyla beraber gösterimi
            }
        });
    }
}