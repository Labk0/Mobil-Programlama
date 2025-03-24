package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Başlık metni");
        progress.setMessage("mesaj metni");
        progress.setCancelable(true);
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                try {
                    Thread.sleep(2000);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "işlem tamamlandı",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}