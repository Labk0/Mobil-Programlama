package com.example.sesuygulama;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sesuygulama.data.db.ProfileEntity;
import com.example.sesuygulama.data.preferences.ProfilePreferences;
import com.example.sesuygulama.ProfileViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ProfileCreationActivity extends AppCompatActivity {

    private TextInputLayout textFieldProfileName;
    private TextInputEditText editTextProfileName;
    private TextInputLayout textFieldProfilePin;
    private TextInputEditText editTextProfilePin;
    private Button buttonSaveProfile;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        textFieldProfileName = findViewById(R.id.textFieldProfileName);
        editTextProfileName = findViewById(R.id.editTextProfileName);
        textFieldProfilePin = findViewById(R.id.textFieldProfilePin);
        editTextProfilePin = findViewById(R.id.editTextProfilePin);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // kaydet butonunun başlangıç durumunu ayarla
        buttonSaveProfile.setEnabled(false);

        // girdi alanları için TextWatcher'lar ekleleme
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateInputs();
            }
        };

        editTextProfileName.addTextChangedListener(textWatcher);
        editTextProfilePin.addTextChangedListener(textWatcher);

        buttonSaveProfile.setOnClickListener(v -> {
            saveProfile();
        });
    }

    private void validateInputs() {
        String name = editTextProfileName.getText().toString().trim();
        String pin = editTextProfilePin.getText().toString().trim();

        boolean isNameValid = !name.isEmpty();
        boolean isPinValid = pin.length() == 4;

        if (!isNameValid) {
            textFieldProfileName.setError("Profil adı boş bırakılamaz");
        } else {
            textFieldProfileName.setError(null); // hatayı temizler
        }

        if (pin.length() > 0 && !isPinValid) { // kullanıcı PIN girmeye başladıysa ama 4 haneli değilse
            textFieldProfilePin.setError("PIN 4 haneli olmalıdır");
        } else {
            textFieldProfilePin.setError(null); // hatayı temizler
        }
        buttonSaveProfile.setEnabled(isNameValid && isPinValid);
    }

    private void saveProfile() {
        String name = editTextProfileName.getText().toString().trim();
        String pin = editTextProfilePin.getText().toString().trim();

        //pin hashlenebilir

        if (name.isEmpty()) {
            Toast.makeText(this, "Profil adı boş bırakılamaz", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pin.length() != 4) {
            textFieldProfilePin.setError("PIN 4 haneli olmalıdır");
            // Toast.makeText(this, "PIN 4 haneli olmalıdır", Toast.LENGTH_SHORT).show();
            return;
        }

        // isDefault her zaman false olacak, varsayılan profil LauncherActivity'de oluşturuluyor.
        ProfileEntity newProfile = new ProfileEntity(name, pin, false);

        profileViewModel.insertProfile(newProfile);

        ProfilePreferences.setActiveProfileId(this, newProfile.profileId);

        Toast.makeText(this, "Profil '" + name + "' oluşturuldu ve aktif hale getirildi.", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(ProfileCreationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }
}