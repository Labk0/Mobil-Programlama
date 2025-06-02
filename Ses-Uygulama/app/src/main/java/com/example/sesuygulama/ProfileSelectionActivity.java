package com.example.sesuygulama;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log; // Hata ayıklama için eklendi
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull; // Eklendi
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sesuygulama.ProfileAdapter;
import com.example.sesuygulama.data.db.ProfileEntity;
import com.example.sesuygulama.data.preferences.ProfilePreferences;

public class ProfileSelectionActivity extends AppCompatActivity implements ProfileAdapter.OnProfileClickListener, ProfileAdapter.OnProfileDeleteListener {

    private RecyclerView recyclerView;
    private ProfileAdapter adapter;
    private ProfileViewModel profileViewModel;
    private Button btnCreateNewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_selection);

        btnCreateNewProfile = findViewById(R.id.btnCreateNewProfile);
        recyclerView = findViewById(R.id.recyclerViewProfiles);

        adapter = new ProfileAdapter(this, this);

        setupRecyclerView();

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.getAllProfiles().observe(this, profiles -> {
            if (profiles != null) {
                adapter.submitList(profiles);
            }
        });

        if (btnCreateNewProfile != null) {
            btnCreateNewProfile.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileSelectionActivity.this, ProfileCreationActivity.class);
                startActivity(intent);
            });
        }
    }
    private void setupRecyclerView() {
        if (recyclerView != null && adapter != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onProfileClick(@NonNull ProfileEntity profile) {
        showPinEntryDialog(profile);
    }

    @Override
    public void onDeleteClick(@NonNull ProfileEntity profile) {
        new AlertDialog.Builder(this)
                .setTitle("Profili Sil")
                .setMessage("\"" + profile.name + "\" adlı profili silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.")
                .setPositiveButton("Sil", (dialog, which) -> {
                    if (profileViewModel != null) {
                        profileViewModel.deleteProfileById(profile.profileId);
                        Toast.makeText(ProfileSelectionActivity.this, profile.name + " silindi.", Toast.LENGTH_SHORT).show();
                        // activeProfileId'yi de temizleme
                        String activeProfileId = ProfilePreferences.getActiveProfileId(this);
                        if (activeProfileId != null && activeProfileId.equals(profile.profileId)) {
                            ProfilePreferences.clearActiveProfileId(this);
                        }
                    } else {
                        Toast.makeText(this, "Silme işlemi yapılamadı.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("İptal", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showPinEntryDialog(@NonNull ProfileEntity profile) {
        String profileName = profile.name;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(profileName + " için PIN Girin");

        final EditText inputPin = new EditText(this);
        inputPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        inputPin.setHint("4 Haneli PIN");
        inputPin.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = (int) (16 * getResources().getDisplayMetrics().density); // dp'yi px'e çevir
        params.setMargins(margin, margin / 2, margin, margin / 2);
        layout.addView(inputPin, params); // EditText'i layout'a layout parametreleriyle ekle
        builder.setView(layout);

        builder.setPositiveButton("Giriş Yap", (dialog, which) -> {
            String enteredPin = inputPin.getText().toString();

            if (enteredPin.length() == 4) {
                // gerçek uygulamada girilen PIN'i hash'leyip saklanan hash ile karşılaştırılma eklenebilir
                if (profile.pin.equals(enteredPin)) {
                    Toast.makeText(this, profileName + " için giriş başarılı!", Toast.LENGTH_SHORT).show();
                    ProfilePreferences.setActiveProfileId(this, profile.profileId); // uygulama kapatılıp tekrar açıldığında direkt profil seçili olacak

                    Intent intent = new Intent(ProfileSelectionActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finishAffinity(); // Tüm geçmiş aktiviteleri temizle
                } else {
                    Toast.makeText(this, "Yanlış PIN!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "PIN 4 haneli olmalıdır!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("İptal", (dialog, which) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}