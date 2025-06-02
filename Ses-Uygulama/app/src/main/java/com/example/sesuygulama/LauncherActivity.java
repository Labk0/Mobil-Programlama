package com.example.sesuygulama;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sesuygulama.data.db.AppDatabase;
import com.example.sesuygulama.data.db.AudioFileDao;
import com.example.sesuygulama.data.db.AudioFileEntity;
import com.example.sesuygulama.data.db.CategoryDao;
import com.example.sesuygulama.data.db.CategoryEntity;
import com.example.sesuygulama.data.db.ProfileEntity;
import com.example.sesuygulama.data.preferences.ProfilePreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LauncherActivity extends AppCompatActivity {
    private static final String TAG = "LauncherActivity";
    public static final String DEFAULT_PROFILE_NAME = "Varsayılan Profil";
    public static final String DEFAULT_PROFILE_PIN = "1234";
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getDatabase(getApplicationContext());
        checkAndSetupDefaultProfile();
    }

    private void checkAndSetupDefaultProfile() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                ProfileEntity defaultProfile = db.profileDao().getDefaultProfile();
                
                if (defaultProfile == null) {
                    defaultProfile = new ProfileEntity(
                            DEFAULT_PROFILE_NAME,
                            DEFAULT_PROFILE_PIN,
                            true
                    );
                    db.profileDao().insertProfile(defaultProfile);
                    addDefaultContentForProfile(defaultProfile.profileId);
                    
                    if (ProfilePreferences.getActiveProfileId(getApplicationContext()) == null) {
                        ProfilePreferences.setActiveProfileId(getApplicationContext(), defaultProfile.profileId);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Varsayılan profil oluşturma hatası", e);
            } finally {
                navigateToNextScreen();
                executor.shutdown();
            }
        });
    }

    private void addDefaultContentForProfile(String profileId) {
        try {
            CategoryDao categoryDao = db.categoryDao();
            AudioFileDao audioFileDao = db.audioFileDao();

            // Kategorileri toplu ekle
            List<CategoryEntity> categories = new ArrayList<>();
            categories.add(new CategoryEntity("Hayvan Sesleri", profileId, true));
            categories.add(new CategoryEntity("Doğa", profileId, true));
            
            long[] categoryIds = categoryDao.insertCategories(categories.toArray(new CategoryEntity[0]));
            
            // Ses dosyalarını hazırla
            List<AudioFileEntity> audioFiles = new ArrayList<>();
            
            // Hayvan sesleri
            String kittyUri = "android.resource://" + getPackageName() + "/" + R.raw.sweet_kitty_meow;
            String wolfUri = "android.resource://" + getPackageName() + "/" + R.raw.wolf_howl;
            audioFiles.add(new AudioFileEntity("Tatlı Kedi Miyavlama", kittyUri, (int)categoryIds[0], profileId));
            audioFiles.add(new AudioFileEntity("Kurt Uluma", wolfUri, (int)categoryIds[0], profileId));
            
            // Doğa sesleri
            String campfireUri = "android.resource://" + getPackageName() + "/" + R.raw.campfire;
            String rainUri = "android.resource://" + getPackageName() + "/" + R.raw.light_rain;
            audioFiles.add(new AudioFileEntity("Kamp Ateşi", campfireUri, (int)categoryIds[1], profileId));
            audioFiles.add(new AudioFileEntity("Hafif Yağmur", rainUri, (int)categoryIds[1], profileId));
            
            // Sesleri toplu ekle
            audioFileDao.insertAudioFiles(audioFiles.toArray(new AudioFileEntity[0]));
            
        } catch (Exception e) {
            Log.e(TAG, "Varsayılan içerik ekleme hatası", e);
        }
    }

    private File copyRawResourceToFile(int resourceId, String targetFileName) {
        File targetFile = new File(getFilesDir(), targetFileName);

        try {
            if (targetFile.exists()) {
                targetFile.delete();
            }

            try (InputStream in = getResources().openRawResource(resourceId);
                 OutputStream out = new FileOutputStream(targetFile)) {

                byte[] buffer = new byte[8192];
                int read;
                long total = 0;

                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                    total += read;
                }
                
                out.flush();

                Log.d(TAG, "Ses dosyası başarıyla kopyalandı: " + targetFileName + 
                          " (Boyut: " + total + " bytes)");

                // dosyanın başarıyla oluşturulduğunu ve okunabilir olduğunun kontrolü
                if (targetFile.exists() && targetFile.canRead()) {
                    Log.d(TAG, "Dosya kontrolü başarılı: " + targetFile.getAbsolutePath());
                    return targetFile;
                } else {
                    Log.e(TAG, "Dosya oluşturuldu ama okunamıyor: " + targetFile.getAbsolutePath());
                    return null;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Ses dosyası kopyalama hatası: " + targetFileName, e);
            // Hata durumunda dosyayı temizle
            if (targetFile.exists()) {
                targetFile.delete();
            }
            return null;
        }
    }

    private void navigateToNextScreen() {
        runOnUiThread(() -> {
            String activeProfileId = ProfilePreferences.getActiveProfileId(getApplicationContext());

            if (activeProfileId != null) {
                startActivity(new Intent(LauncherActivity.this, ProfileSelectionActivity.class));
            } else {
                startActivity(new Intent(LauncherActivity.this, ProfileSelectionActivity.class));
            }
            finish();
        });
    }
}