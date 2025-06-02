package com.example.sesuygulama.data.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
// ileride gercekten bir pinleme icin sifreleme eklenicek

import java.util.UUID;

@Entity(tableName = "profiles")
public class ProfileEntity {
    @PrimaryKey
    @NonNull
    public String profileId;

    @NonNull
    public String name;

    @NonNull
    public String pin;
    //guvensiz

    public long createdAt;
    public boolean isDefault;

    public ProfileEntity(@NonNull String name, @NonNull String pin, boolean isDefault) {
        this.profileId = UUID.randomUUID().toString();
        this.name = name;
        this.pin = pin;
        this.isDefault = isDefault;
        this.createdAt = System.currentTimeMillis();
    }
}