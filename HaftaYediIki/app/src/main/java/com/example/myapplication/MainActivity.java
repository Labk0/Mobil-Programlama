package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> stringList = new ArrayList<>();
    private String[] turkiyeSehirleri = {
            "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir",
            "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli",
            "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari",
            "Hatay", "Isparta", "Mersin", "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
            "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir",
            "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop", "Sivas", "Tekirdağ", "Tokat",
            "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman",
            "Kırıkkale", "Batman", "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye",
            "Düzce"
    };
    private long startTime = 0;
    private int plaka = 0;
    private long elapsedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.start_layout);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = System.currentTimeMillis();
                setContentView(R.layout.activity_main);

                SeekBar sb = (SeekBar) findViewById(R.id.seekBar);
                sb.setMin(1);
                sb.setMax(81);
                TextView tx1 = (TextView) findViewById(R.id.textView);
                Button onayla = (Button) findViewById(R.id.button2);
                EditText txt = (EditText) findViewById(R.id.editTextText);

                sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        plaka = progress;
                        tx1.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                onayla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long endTime = System.currentTimeMillis();
                        elapsedTime = endTime - startTime;
                        if (turkiyeSehirleri[plaka - 1].contains(txt.getText().toString())) {
                            String liste = txt.getText().toString() + " - " + elapsedTime + "ms";
                            startTime = 0;

                            stringList.add(liste);
                            setContentView(R.layout.layout);

                            Button backButton = (Button) findViewById(R.id.button3);

                            backButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    recreate();
                                }
                            });

                            RecyclerView recyclerView = findViewById(R.id.deneme);
                            recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(MainActivity.this));
                            recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                                @Override
                                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    TextView textView = new TextView(parent.getContext());
                                    textView.setPadding(20, 20, 20, 20);
                                    textView.setTextSize(18);
                                    return new RecyclerView.ViewHolder(textView) {
                                    };
                                }

                                @Override
                                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                                    ((TextView) holder.itemView).setText(stringList.get(position));
                                }

                                @Override
                                public int getItemCount() {
                                    return stringList.size();
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Yanlış şehir girdiniz.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}