package com.example.sesuygulama.data.db;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "audio_files",
        foreignKeys = {
                @ForeignKey(entity = ProfileEntity.class,
                        parentColumns = "profileId",
                        childColumns = "profileOwnerId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = CategoryEntity.class,
                        parentColumns = "categoryId",
                        childColumns = "categoryIdFK",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "profileOwnerId"), @Index(value = "categoryIdFK")})
public class AudioFileEntity {
    @PrimaryKey(autoGenerate = true)
    public int audioId;

    @NonNull
    public String title;

    @NonNull
    public String filePath;

    public Integer categoryIdFK;

    @NonNull
    public String profileOwnerId;

    public AudioFileEntity(@NonNull String title, @NonNull String filePath, Integer categoryIdFK, @NonNull String profileOwnerId) {
        this.title = title;
        this.filePath = filePath;
        this.categoryIdFK = categoryIdFK;
        this.profileOwnerId = profileOwnerId;
    }
}