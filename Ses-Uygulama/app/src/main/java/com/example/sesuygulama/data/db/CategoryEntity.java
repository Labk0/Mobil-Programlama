package com.example.sesuygulama.data.db;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "categories",
        foreignKeys = @ForeignKey(entity = ProfileEntity.class,
                parentColumns = "profileId",
                childColumns = "profileOwnerId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "profileOwnerId")})
public class CategoryEntity {
    @PrimaryKey(autoGenerate = true)
    public int categoryId;

    @NonNull
    public String name;

    @NonNull
    public String profileOwnerId;

    public boolean isDeletable = true;

    public CategoryEntity(@NonNull String name, @NonNull String profileOwnerId, boolean isDeletable) {
        this.name = name;
        this.profileOwnerId = profileOwnerId;
        this.isDeletable = isDeletable;
    }
}