package com.example.sesuygulama;

import com.example.sesuygulama.data.db.AudioFileEntity;
import com.example.sesuygulama.data.db.CategoryEntity;
import com.example.sesuygulama.data.preferences.ProfilePreferences;
import com.example.sesuygulama.data.db.ProfileEntity;
import com.example.sesuygulama.ProfileViewModel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import android.widget.SeekBar;
import android.widget.ImageButton;
import android.widget.CheckBox;
import android.widget.AutoCompleteTextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static final String[] kategoriIsimleri = {"Hepsi", "Hayvan Sesleri", "Mizah", "Doğa", "Özel"};
    private static final int DOSYA_SEC = 1;
    private static final int KATEGORI_YOK_ID = -1; // "hepsi" icin ozel id
    private static final String TUM_SESLER_ETIKETI = "Hepsi";
    private AutoCompleteTextView spinnerKategori;
    RecyclerView recyclerViewSesler;
    FloatingActionButton fabEkle;
    List<AudioFileEntity> tumSesler = new ArrayList<>();
    AudioFileAdapter audioAdapter;
    Toolbar toolbarMain;
    TextView toolbar_title;
    private ProfileViewModel profileViewModel;
    private CategoryViewModel categoryViewModel;
    private  AudioViewModel audioViewModel;
    private String currentProfileId;
    private List<CategoryEntity> kategoriListesiSpinner = new ArrayList<>();
    private ArrayAdapter<String> kategoriSpinnerAdapter;
    private List<String> kategoriAdlariSpinner = new ArrayList<>();
    private CategoryEntity seciliKategoriSpinner;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar_title = findViewById(R.id.toolbar_title);

        spinnerKategori = findViewById(R.id.spinnerKategori);
        recyclerViewSesler = findViewById(R.id.recyclerViewSesler);
        fabEkle = findViewById(R.id.fabEkle);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        audioViewModel = new ViewModelProvider(this).get(AudioViewModel.class);

        profileViewModel.getProfileById(ProfilePreferences.getActiveProfileId(this)).observe(this, profileEntity -> {
            toolbar_title.setText(profileEntity.name);
        });

        currentProfileId = ProfilePreferences.getActiveProfileId(this);
        Log.d(TAG, "onCreate - currentProfileId: " + currentProfileId);

        if (currentProfileId != null) {
            setupKategoriSpinner();
            setupRecyclerViewSesler();
            observeKategoriler();
            // Profile girişinde "Hepsi" seçili olsun ve tüm sesler listelensin
            seciliKategoriSpinner = null;
            observeTumSesler();
        } else {
            Log.e(TAG, "onCreate - Aktif profil ID'si bulunamadı!");
            return;
        }

        fabEkle.setOnClickListener(v -> {
            openAddSoundDialog();
        });
    }

    private void openAddSoundDialog() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        try {
            startActivityForResult(intent, DOSYA_SEC);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Lütfen bir dosya yöneticisi uygulaması yükleyin.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DOSYA_SEC && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri sesUri = data.getData();
            try {
                getContentResolver().takePersistableUriPermission(sesUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                Toast.makeText(this, "Dosya erişim izni alınamadı.", Toast.LENGTH_SHORT).show();
                return;
            }
            showEnterSoundDetailsDialog(sesUri);
        }
    }

    private void showEnterSoundDetailsDialog(Uri sesUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_sound_details, null);
        builder.setView(dialogView);

        EditText editTextSesAdi = dialogView.findViewById(R.id.editTextNewSoundName);
        Spinner spinnerDialogKategori = dialogView.findViewById(R.id.spinnerDialogNewSoundCategory);

        // Spinner için kategori listesini hazırlama
        List<String> dialogKategoriAdlari = new ArrayList<>();
        List<CategoryEntity> dialogKategoriListesi = new ArrayList<>();

        if (kategoriListesiSpinner != null && !kategoriListesiSpinner.isEmpty()){
            dialogKategoriListesi.addAll(kategoriListesiSpinner); // MainActivity'deki filtrelenmemiş liste
            for(CategoryEntity cat : dialogKategoriListesi){
                dialogKategoriAdlari.add(cat.name);
            }
        } else {
            dialogKategoriAdlari.add("Kategori Yok");
        }


        ArrayAdapter<String> dialogSpinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, dialogKategoriAdlari);
        dialogSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialogKategori.setAdapter(dialogSpinnerAdapter);

        builder.setTitle("Ses Detaylarını Girin");
        builder.setPositiveButton("Ekle", (dialog, which) -> {
            String sesAdi = editTextSesAdi.getText().toString().trim();
            if (android.text.TextUtils.isEmpty(sesAdi)) {
                Toast.makeText(this, "Ses adı boş olamaz!", Toast.LENGTH_SHORT).show();
                return;
            }

            int secilenKategoriPozisyonu = spinnerDialogKategori.getSelectedItemPosition();
            Integer secilenKategoriId = null;

            if (!dialogKategoriListesi.isEmpty() && secilenKategoriPozisyonu >= 0 && secilenKategoriPozisyonu < dialogKategoriListesi.size()) {
                secilenKategoriId = dialogKategoriListesi.get(secilenKategoriPozisyonu).categoryId;
            }

            String dosyaAdi = sesAdi.replaceAll("[^a-zA-Z0-9.-]", "_") + "_" + System.currentTimeMillis() + ".mp3"; // Benzersiz bir ad
            File hedefDosya = copyUriToInternalStorage(sesUri, dosyaAdi);

            if (hedefDosya != null) {
                AudioFileEntity newAudioFile = new AudioFileEntity(sesAdi, hedefDosya.getAbsolutePath(), secilenKategoriId, currentProfileId);
                audioViewModel.insertAudioFile(newAudioFile);
                Toast.makeText(this, "'" + sesAdi + "' eklendi.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ses dosyası kopyalanamadı.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("İptal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private File copyUriToInternalStorage(Uri uri, String targetFileName) {
        File targetFile = new File(getFilesDir(), targetFileName);
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(targetFile)) {
            if (inputStream == null) return null;
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            return targetFile;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(MainActivity.this, ProfileSelectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if(id == R.id.action_manage_categories){
            Intent intent = new Intent(MainActivity.this, ManageCategoriesActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupKategoriSpinner() {
        kategoriSpinnerAdapter = new ArrayAdapter<>(this,
                R.layout.item_dropdown_menu, kategoriAdlariSpinner);
        
        spinnerKategori = findViewById(R.id.spinnerKategori);
        spinnerKategori.setAdapter(kategoriSpinnerAdapter);
        
        // Başlangıçta "Hepsi" seçeneğini ekle
        kategoriAdlariSpinner.clear();
        kategoriAdlariSpinner.add(TUM_SESLER_ETIKETI);
        kategoriSpinnerAdapter.notifyDataSetChanged();
        spinnerKategori.setText(TUM_SESLER_ETIKETI, false);
        
        spinnerKategori.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) { // "Hepsi" seçildi
                seciliKategoriSpinner = null;
                observeTumSesler();
            } else {
                // "Hepsi" öğesi nedeniyle pozisyondan 1 çıkartılıyor ve kategoriListesiSpinner'dan alınıyor
                if (!kategoriListesiSpinner.isEmpty() && (position - 1) < kategoriListesiSpinner.size()) {
                    seciliKategoriSpinner = kategoriListesiSpinner.get(position - 1);
                    observeSeslerByKategori(seciliKategoriSpinner.categoryId);
                } else {
                    seciliKategoriSpinner = null;
                    observeTumSesler();
                }
            }
            if (audioAdapter != null) {
                audioAdapter.updateAudioFiles(tumSesler, new ArrayList<>(kategoriListesiSpinner), seciliKategoriSpinner != null ? seciliKategoriSpinner.categoryId : null);
            }
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setupRecyclerViewSesler() {
        Log.d(TAG, "setupRecyclerViewSesler çağrıldı");
        recyclerViewSesler.setLayoutManager(new LinearLayoutManager(this));
        
        // Oynatıcı kontrolleri
        View playerBar = findViewById(R.id.playerBar);
        TextView txtPlayerTitle = findViewById(R.id.txtPlayerTitle);
        TextView txtDuration = findViewById(R.id.txtDuration);
        SeekBar seekBar = findViewById(R.id.seekBar);
        ImageButton btnPlayPause = findViewById(R.id.btnPlayPause);
        ImageButton btnLoop = findViewById(R.id.btnLoop);
        
        audioAdapter = new AudioFileAdapter(this, new ArrayList<>(tumSesler), new ArrayList<>(kategoriListesiSpinner), 
            (audioFileId, audioFileName) -> {
                new AlertDialog.Builder(this)
                        .setTitle("Sesi Sil")
                        .setMessage("'" + audioFileName + "' adlı sesi silmek istediğinizden emin misiniz?")
                        .setPositiveButton("Sil", (dialog, which) -> {
                            audioViewModel.deleteAudioFile(audioFileId, currentProfileId);
                            Toast.makeText(this, "'" + audioFileName + "' silindi.", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("İptal", null)
                        .show();
            }, null);

        // Ses çalma durumunu dinle
        audioAdapter.setOnPlaybackListener(new AudioFileAdapter.OnPlaybackListener() {
            @Override
            public void onPlaybackStarted(String title, int duration) {
                playerBar.setVisibility(View.VISIBLE);
                txtPlayerTitle.setText(title);
                seekBar.setMax(duration);
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                updateDurationText(txtDuration, duration, 0);
            }

            @Override
            public void onPlaybackPaused() {
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            }

            @Override
            public void onPlaybackStopped() {
                playerBar.setVisibility(View.GONE);
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            }

            @Override
            public void onPlaybackProgress(int progress) {
                seekBar.setProgress(progress);
                updateDurationText(txtDuration, seekBar.getMax(), progress);
            }
        });

        // Oynatıcı kontrolleri için click listeners
        btnPlayPause.setOnClickListener(v -> {
            if (audioAdapter.isPlaying()) {
                audioAdapter.pauseSound();
            } else {
                audioAdapter.resumeSound();
            }
        });

        btnLoop.setOnClickListener(v -> {
            boolean isLooping = !audioAdapter.isLooping();
            audioAdapter.setLooping(isLooping);
            btnLoop.setImageResource(isLooping ? R.drawable.ic_repeat_on : R.drawable.ic_repeat_off);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioAdapter.seekTo(progress);
                    updateDurationText(txtDuration, seekBar.getMax(), progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        recyclerViewSesler.setAdapter(audioAdapter);
        Log.d(TAG, "RecyclerView adapter ayarlandı");
    }

    private void updateDurationText(TextView txtDuration, int totalDuration, int currentPosition) {
        String durationText = String.format("%s / %s",
            formatDuration(currentPosition),
            formatDuration(totalDuration));
        txtDuration.setText(durationText);
    }

    private String formatDuration(int durationMs) {
        int seconds = (durationMs / 1000) % 60;
        int minutes = (durationMs / (1000 * 60)) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void observeKategoriler() {
        Log.d(TAG, "observeKategoriler (Spinner için) çağrıldı. Mevcut Profil ID: " + currentProfileId);
        if (currentProfileId == null) {
            return;
        }

        categoryViewModel.getCategoriesForProfile(currentProfileId).observe(this, kategorilerDb -> {
            Log.d(TAG, "Kategoriler (Spinner için) LiveData güncellendi. Gelen kategori sayısı: " + (kategorilerDb != null ? kategorilerDb.size() : "null"));

            kategoriAdlariSpinner.clear();
            kategoriListesiSpinner.clear();

            kategoriAdlariSpinner.add(TUM_SESLER_ETIKETI); // "Hepsi" her zaman başta

            if (kategorilerDb != null && !kategorilerDb.isEmpty()) {
                kategoriListesiSpinner.addAll(kategorilerDb);
                for (CategoryEntity kategori : kategorilerDb) {
                    kategoriAdlariSpinner.add(kategori.name);
                }
            }

            kategoriSpinnerAdapter.notifyDataSetChanged();
            Log.d(TAG, "Kategori Spinner Adapter güncellendi. Yeni eleman sayısı: " + kategoriSpinnerAdapter.getCount());

            if (seciliKategoriSpinner != null) {
                boolean bulundu = false;
                for (int i = 0; i < kategoriListesiSpinner.size(); i++) {
                    if (kategoriListesiSpinner.get(i).categoryId == seciliKategoriSpinner.categoryId) {
                        ((AutoCompleteTextView) spinnerKategori).setText(kategoriListesiSpinner.get(i).name, false);
                        bulundu = true;
                        break;
                    }
                }
                if (!bulundu) {
                    ((AutoCompleteTextView) spinnerKategori).setText(TUM_SESLER_ETIKETI, false);
                }
            } else {
                ((AutoCompleteTextView) spinnerKategori).setText(TUM_SESLER_ETIKETI, false);
            }

            if (audioAdapter != null) {
                Integer filteredCategoryId = seciliKategoriSpinner != null ? seciliKategoriSpinner.categoryId : null;
                audioAdapter.updateAudioFiles(tumSesler, new ArrayList<>(kategoriListesiSpinner), filteredCategoryId);
            }
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void observeTumSesler() {
        Log.d(TAG, "observeTumSesler çağrıldı");
        audioViewModel.getAudioFilesForProfile(currentProfileId).observe(this, audioFiles -> {
            Log.d(TAG, "Tüm ses dosyaları alındı - Dosya sayısı: " + (audioFiles != null ? audioFiles.size() : 0));
            if (audioFiles != null && !audioFiles.isEmpty()) {
                Log.d(TAG, "İlk ses dosyası başlığı: " + audioFiles.get(0).title);
                tumSesler.clear();
                tumSesler.addAll(audioFiles);
                Integer filteredCategoryId = seciliKategoriSpinner != null ? seciliKategoriSpinner.categoryId : null;
                Log.d(TAG, "Adapter güncelleniyor - Filtre kategorisi: " + filteredCategoryId + ", Ses sayısı: " + tumSesler.size());
                audioAdapter.updateAudioFiles(new ArrayList<>(tumSesler), new ArrayList<>(kategoriListesiSpinner), filteredCategoryId);
            } else {
                tumSesler.clear();
                Log.d(TAG, "Ses dosyası bulunamadı veya boş liste geldi");
                audioAdapter.updateAudioFiles(new ArrayList<>(tumSesler), new ArrayList<>(kategoriListesiSpinner), null);
            }
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void observeSeslerByKategori(int categoryId) {
        Log.d(TAG, "observeSeslerByKategori çağrıldı - categoryId: " + categoryId);
        audioViewModel.getAudioFilesForCategory(currentProfileId, categoryId).observe(this, audioFiles -> {
            Log.d(TAG, "Kategori için ses dosyaları alındı - Dosya sayısı: " + (audioFiles != null ? audioFiles.size() : 0));
            if (audioFiles != null) {
                tumSesler.clear();
                tumSesler.addAll(audioFiles);
                Integer filteredCategoryId = seciliKategoriSpinner != null ? seciliKategoriSpinner.categoryId : null;
                Log.d(TAG, "Adapter güncelleniyor - Filtre kategorisi: " + filteredCategoryId);
                audioAdapter.updateAudioFiles(tumSesler, new ArrayList<>(kategoriListesiSpinner), filteredCategoryId);
            } else {
                tumSesler.clear();
                audioAdapter.updateAudioFiles(tumSesler, new ArrayList<>(kategoriListesiSpinner), null);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (audioAdapter != null) {
            audioAdapter.releaseMediaPlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioAdapter != null) {
            audioAdapter.releaseMediaPlayer();
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onResume() {
        super.onResume();
        String activeProfileIdOnResume = ProfilePreferences.getActiveProfileId(this);
        Log.d(TAG, "onResume - activeProfileIdOnResume: " + activeProfileIdOnResume);

        if (currentProfileId == null || !currentProfileId.equals(activeProfileIdOnResume)) {
            Log.d(TAG, "onResume - Profil ID değişti veya ilk kez ayarlanıyor. Eski: " + currentProfileId + ", Yeni: " + activeProfileIdOnResume);
            currentProfileId = activeProfileIdOnResume;
            if (currentProfileId != null) {
                observeKategoriler();
            } else {
                Log.e(TAG, "onResume - Aktif profil ID'si hala bulunamadı!");
            }
        } else {
            Log.d(TAG, "onResume - Profil ID değişmedi: " + currentProfileId);
        }
    }
}
