package com.example.sesuygulama;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.sesuygulama.data.db.AppDatabase;
import com.example.sesuygulama.data.db.AudioFileDao;
import com.example.sesuygulama.data.db.AudioFileEntity;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class AudioViewModel extends AndroidViewModel {
    private AudioFileDao audioFileDao;
    private ExecutorService executorService;

    public AudioViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        audioFileDao = db.audioFileDao();
        executorService = AppDatabase.databaseWriteExecutor;
    }

    public LiveData<List<AudioFileEntity>> getAudioFilesForProfile(String profileId) {
        return audioFileDao.getAudioFilesForProfile(profileId);
    }

    public LiveData<List<AudioFileEntity>> getAudioFilesForCategory(String profileId, int categoryId) {
        return audioFileDao.getAudioFilesForCategory(profileId, categoryId);
    }

    public void insertAudioFile(AudioFileEntity audioFile) {
        executorService.execute(() -> audioFileDao.insertAudioFile(audioFile));
    }

    public void deleteAudioFile(int audioFileId, String profileId) {
        executorService.execute(() -> audioFileDao.deleteAudioFileByIdAndProfile(audioFileId, profileId));
    }

    // kategori silindiğinde seslerin kategorisini güncellemek için
    public void recategorizeAudios(int oldCategoryId, Integer newCategoryId, String profileId) {
        executorService.execute(() -> audioFileDao.updateCategoryIdForProfile(oldCategoryId, newCategoryId, profileId));
    }
}