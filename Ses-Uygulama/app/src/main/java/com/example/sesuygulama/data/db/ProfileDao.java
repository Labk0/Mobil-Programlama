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
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProfile(ProfileEntity profile);

    @Update
    void updateProfile(ProfileEntity profile);

    @Delete
    void deleteProfile(ProfileEntity profile);

    @Query("DELETE FROM profiles WHERE profileId = :profileId")
    void deleteProfileById(String profileId);

    @Query("SELECT * FROM profiles ORDER BY name ASC")
    LiveData<List<ProfileEntity>> getAllProfiles();

    @Query("SELECT * FROM profiles WHERE profileId = :id")
    ProfileEntity getProfileById(String id);

    @Query("SELECT COUNT(*) FROM profiles")
    int getProfileCount();

    @Query("SELECT * FROM profiles")
    List<ProfileEntity> getAllProfilesSynchronous();

    @Query("SELECT * FROM profiles WHERE profileId = :id")
    LiveData<ProfileEntity> getProfileByIdLiveData(String id);

    @Query("SELECT * FROM profiles WHERE isDefault = 1 LIMIT 1")
    ProfileEntity getDefaultProfile();
}
