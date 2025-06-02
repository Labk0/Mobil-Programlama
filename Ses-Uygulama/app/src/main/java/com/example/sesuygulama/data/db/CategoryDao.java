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
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(CategoryEntity category);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertCategoryAndGetId(CategoryEntity category);

    @Update
    void updateCategory(CategoryEntity category);

    @Delete
    void deleteCategory(CategoryEntity category);

    @Query("SELECT * FROM categories WHERE profileOwnerId = :profileId ORDER BY name ASC")
    LiveData<List<CategoryEntity>> getCategoriesForProfile(String profileId);

    @Query("DELETE FROM categories WHERE categoryId = :categoryId AND profileOwnerId = :profileId")
    void deleteCategoryByIdAndProfile(int categoryId, String profileId);

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId AND profileOwnerId = :profileId")
    LiveData<CategoryEntity> getCategoryByIdAndProfileLiveData(int categoryId, String profileId);

    @Query("SELECT * FROM categories WHERE name = :name AND profileOwnerId = :profileId LIMIT 1")
    CategoryEntity getCategoryByNameAndProfile(String name, String profileId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertCategories(CategoryEntity[] categories);
}