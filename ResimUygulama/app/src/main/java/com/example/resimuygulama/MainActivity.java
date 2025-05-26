package com.example.resimuygulama;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WallpaperAdapter.WallpaperClickListener{

    private RecyclerView recyclerView;
    private WallpaperAdapter adapter;
    private List<String> wallpaperUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        wallpaperUrls = new ArrayList<>();
        adapter = new WallpaperAdapter(this, wallpaperUrls, this);
        recyclerView.setAdapter(adapter);

        loadWallpapers();
    }
    private void loadWallpapers() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("wallpapers");

        listRef.listAll().addOnSuccessListener(ListResult ->{
            for (StorageReference item : ListResult.getItems()){
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    wallpaperUrls.add(uri.toString());
                    adapter.notifyDataSetChanged();
                });
            }
        }).addOnFailureListener(e ->{
        });
    }
    @Override
    public void onWallpaperClick(String url){
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
                try {
                    wallpaperManager.setBitmap(bitmap);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                e.printStackTrace();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }
}