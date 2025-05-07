package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_acc).setOnClickListener(v -> startActivity(new Intent(this, AccelerometerActivity.class)));
        findViewById(R.id.btn_gyr).setOnClickListener(v -> startActivity(new Intent(this, GyroscopeActivity.class)));
        findViewById(R.id.btn_lig).setOnClickListener(v -> startActivity(new Intent(this, LightActivity.class)));
        findViewById(R.id.btn_hum).setOnClickListener(v -> startActivity(new Intent(this, HumidityActivity.class)));
        findViewById(R.id.btn_com).setOnClickListener(v -> startActivity(new Intent(this, CompassActivity.class)));
        findViewById(R.id.btn_mag).setOnClickListener(v -> startActivity(new Intent(this, MagnetometerActivity.class)));
        findViewById(R.id.btn_pre).setOnClickListener(v -> startActivity(new Intent(this, PressureActivity.class)));
        findViewById(R.id.btn_pro).setOnClickListener(v -> startActivity(new Intent(this, ProximityActivity.class)));
        findViewById(R.id.btn_the).setOnClickListener(v -> startActivity(new Intent(this, TemperatureActivity.class)));
    }
}
