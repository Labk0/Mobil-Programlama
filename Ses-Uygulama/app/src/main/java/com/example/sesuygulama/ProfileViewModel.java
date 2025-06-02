package com.example.sesuygulama;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sesuygulama.data.db.AppDatabase;
import com.example.sesuygulama.data.db.ProfileDao;
import com.example.sesuygulama.data.db.ProfileEntity;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {
    private ProfileDao profileDao;
    private LiveData<List<ProfileEntity>> allProfiles;
    public ProfileViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        profileDao = database.profileDao();
        allProfiles = profileDao.getAllProfiles();
    }
    public LiveData<List<ProfileEntity>> getAllProfiles() {
        return allProfiles;
    }

    public void insertProfile(ProfileEntity profile) {
        AppDatabase.databaseWriteExecutor.execute(() -> profileDao.insertProfile(profile));
    }

    public void updateProfile(ProfileEntity profile) {
        AppDatabase.databaseWriteExecutor.execute(() -> profileDao.updateProfile(profile));
    }

    public void deleteProfile(ProfileEntity profile) {
        AppDatabase.databaseWriteExecutor.execute(() -> profileDao.deleteProfile(profile));
    }

    public void deleteProfileById(String profileId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            profileDao.deleteProfileById(profileId);
        });
    }

    public LiveData<ProfileEntity> getProfileById(String profileId) {
        return profileDao.getProfileByIdLiveData(profileId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}