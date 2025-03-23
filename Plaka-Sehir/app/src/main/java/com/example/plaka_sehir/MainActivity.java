package com.example.plaka_sehir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private List<String> randomSehirler;
    private List<String> randomPlakalar;
    private String[] turkiyeSehirleri = {
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
    private int[] plakaKodlari = {
            1, 2, 3, 4, 68, 5, 6, 7, 75, 8,
            9, 10, 74, 72, 69, 11, 12, 13, 14, 15,
            16, 17, 18, 19, 20, 21, 81, 22, 23, 24,
            25, 26, 27, 28, 29, 30, 31, 76, 32, 34,
            35, 46, 78, 70, 36, 37, 38, 71, 39, 40,
            79, 41, 42, 43, 44, 45, 47, 33, 48, 49,
            50, 51, 52, 80, 53, 54, 55, 56, 57, 58,
            63, 73, 59, 60, 61, 62, 64, 65, 77, 66, 67
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Random rand = new Random();
        ListView listPlaka = findViewById(R.id.tabloPlaka);
        ListView listSehir = findViewById(R.id.tabloSehir);
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> indexList = new ArrayList<>();
                for (int i = 0; i < turkiyeSehirleri.length; i++) {
                    indexList.add(i);
                }

                randomSehirler = new ArrayList<>();
                randomPlakalar = new ArrayList<>();

                for (int i = 0; i < 10; i++) {
                    Collections.shuffle(indexList);
                    int index = indexList.get(i);
                    randomSehirler.add(turkiyeSehirleri[index]);

                    Collections.shuffle(indexList);
                    index = indexList.get(i);
                    randomPlakalar.add(String.valueOf(plakaKodlari[index]));
                }

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
                int sehirIndex = randomSehirler.indexOf(randomSehirler.get(position));

                // Sehir bilgilerini ve plaka kodunu intent ile geç
                Intent intent = new Intent(MainActivity.this, com.example.plaka_sehir.SehirInfoActivity.class);
                intent.putExtra("sehir", randomSehirler.get(position));
                intent.putExtra("plaka", Integer.parseInt(randomPlakalar.get(position)));
                intent.putExtra("sehirIndex", sehirIndex);
                startActivity(intent);
            }
        });
    }
}