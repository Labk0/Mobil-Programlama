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

        Button buton1=(Button) findViewById(R.id.button);
        AlertDialog.Builder pencere = new AlertDialog.Builder(MainActivity.this);
        pencere.setTitle("mesaj başlığı");
        pencere.setIcon(R.drawable.arkaplan);
        CharSequence[] items = {"galatasaray", "beşiktaş", "fenerbahçe", "trabzonspor"};
        pencere.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_SHORT).show();
            }
        });

        buton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pencere.show();
            }
        });
    }
}