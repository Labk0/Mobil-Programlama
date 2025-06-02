package com.example.sesuygulama.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AudioFileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAudioFile(AudioFileEntity audioFile);

    // profile ait tüm sesleri getir
    @Query("SELECT * FROM audio_files WHERE profileOwnerId = :profileId ORDER BY title ASC")
    LiveData<List<AudioFileEntity>> getAudioFilesForProfile(String profileId);

    // belirli bir kategorideki sesleri getir
    @Query("SELECT * FROM audio_files WHERE profileOwnerId = :profileId AND categoryIdFK = :categoryId ORDER BY title ASC")
    LiveData<List<AudioFileEntity>> getAudioFilesForCategory(String profileId, int categoryId);

    // bir kategori silindiğinde o kategoriye ait seslerin categoryId'sini güncellemek için
    @Query("UPDATE audio_files SET categoryIdFK = :newCategoryId WHERE categoryIdFK = :oldCategoryId AND profileOwnerId = :profileId")
    void updateCategoryIdForProfile(int oldCategoryId, Integer newCategoryId, String profileId); // newCategoryId null olabilir

    @Query("DELETE FROM audio_files WHERE audioId = :audioFileId AND profileOwnerId = :profileId")
    void deleteAudioFileByIdAndProfile(int audioFileId, String profileId);

    // Bir profile ait tüm sesleri silmek için
    @Query("DELETE FROM audio_files WHERE profileOwnerId = :profileId")
    void deleteAllAudioFilesForProfile(String profileId);
    @Query("SELECT * FROM audio_files WHERE categoryIdFK = :categoryId AND profileOwnerId = :profileId ORDER BY title ASC")
    LiveData<List<AudioFileEntity>> getAudioFilesByCategoryAndProfile(int categoryId, String profileId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAudioFiles(AudioFileEntity[] audioFiles);
}