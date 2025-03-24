package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button button1 = (Button) findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder pencere = new AlertDialog.Builder(MainActivity.this);
                pencere.setTitle("UYARI MESAJI");
                pencere.setMessage("Hayır veya Evet butonuna basın!");
                pencere.setCancelable(true); // seçim yapmadan çıkılabilir olsun mu olmasın mı

                pencere.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Evet butonuna bastınız.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                pencere.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Hayır butonuna bastınız.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog goster = pencere.create();
                goster.show();
            }
        });
    }
}