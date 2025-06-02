package com.example.sesuygulama;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.sesuygulama.data.db.AppDatabase;
import com.example.sesuygulama.data.db.CategoryDao;
import com.example.sesuygulama.data.db.CategoryEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class CategoryViewModel extends AndroidViewModel {

    private CategoryDao categoryDao;
    private ExecutorService executorService;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        categoryDao = db.categoryDao();
        executorService = AppDatabase.databaseWriteExecutor;
    }

    public LiveData<List<CategoryEntity>> getCategoriesForProfile(String profileId) {
        return categoryDao.getCategoriesForProfile(profileId);
    }

    public void insertCategory(CategoryEntity category) {
        executorService.execute(() -> {
            // Aynı isimde kategori var mı diye kontrol
            CategoryEntity existing = categoryDao.getCategoryByNameAndProfile(category.name, category.profileOwnerId);
            if (existing == null) {
                categoryDao.insert(category);
            } else {
                Toast.makeText(getApplication(), "Bu isimde kategori bulunmakta.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteCategory(int categoryId, String profileId) {
        executorService.execute(() -> {
            categoryDao.deleteCategoryByIdAndProfile(categoryId, profileId);
        });
    }

    public LiveData<CategoryEntity> getCategoryById(int categoryId, String profileId) {
        return categoryDao.getCategoryByIdAndProfileLiveData(categoryId, profileId);
    }

    public void updateCategory(CategoryEntity category) {
        executorService.execute(() -> {
            categoryDao.updateCategory(category);
        });
    }
}