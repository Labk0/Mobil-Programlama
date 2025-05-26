package com.example.resimuygulama;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperViewHolder> {
    private Context context;
    private List<String> wallpaperUrls;
    private WallpaperClickListener listener;

    public WallpaperAdapter(Context context, List<String> wallpaperUrls, WallpaperClickListener listener) {
        this.context = context;
        this.wallpaperUrls = wallpaperUrls;
        this.listener = listener;
    }
    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.wallpaper_item, parent, false);
        return new WallpaperViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position){
        String url = wallpaperUrls.get(position);
        Picasso.get().load(url).into(holder.imageView);
        holder.itemView.setOnClickListener(v -> listener.onWallpaperClick(url));
    }
    @Override
    public int getItemCount(){
        return wallpaperUrls.size();
    }
    public class WallpaperViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public WallpaperViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
    public interface WallpaperClickListener{
        void onWallpaperClick(String url);
    }
}
