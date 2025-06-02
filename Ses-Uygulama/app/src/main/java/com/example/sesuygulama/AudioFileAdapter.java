package com.example.sesuygulama;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sesuygulama.data.db.AudioFileEntity;
import com.example.sesuygulama.data.db.CategoryEntity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioFileAdapter extends RecyclerView.Adapter<AudioFileAdapter.AudioViewHolder> {
    private static final String TAG = "AudioFileAdapter";
    private final Context context;
    private List<AudioFileEntity> audioFiles;
    private List<CategoryEntity> allCategoriesList;
    private Integer currentFilteredCategoryId;
    private MediaPlayer mediaPlayer;
    private final OnAudioFileDeleteListener deleteListener;
    private OnPlaybackListener playbackListener;
    private String currentPlayingTitle;
    private boolean isLooping = false;
    private Handler handler;
    private Runnable progressRunnable;

    public interface OnAudioFileDeleteListener {
        void onDeleteAudioFile(int audioFileId, String audioFileName);
    }

    public interface OnPlaybackListener {
        void onPlaybackStarted(String title, int duration);
        void onPlaybackPaused();
        void onPlaybackStopped();
        void onPlaybackProgress(int progress);
    }

    public void setOnPlaybackListener(OnPlaybackListener listener) {
        this.playbackListener = listener;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void pauseSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if (playbackListener != null) {
                playbackListener.onPlaybackPaused();
            }
        }
    }

    public void resumeSound() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            if (playbackListener != null) {
                playbackListener.onPlaybackStarted(currentPlayingTitle, mediaPlayer.getDuration());
                startProgressUpdates();
            }
        }
    }

    public boolean isLooping() {
        return isLooping;
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(looping);
        }
    }

    public void seekTo(int progress) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }

    private void startProgressUpdates() {
        if (handler == null) {
            handler = new Handler();
        }
        stopProgressUpdates();
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying() && playbackListener != null) {
                    playbackListener.onPlaybackProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 200);
                }
            }
        };
        handler.post(progressRunnable);
    }

    private void stopProgressUpdates() {
        if (handler != null && progressRunnable != null) {
            handler.removeCallbacks(progressRunnable);
        }
    }

    private void playSound(String filePath, String title) {
        releaseMediaPlayer();
        mediaPlayer = new MediaPlayer();
        try {
            if (filePath.startsWith("android.resource://")) {
                // URI formatındaki dosyalar için
                mediaPlayer.setDataSource(context, Uri.parse(filePath));
            } else {
                // Normal dosya yolu için
                File audioFile = new File(filePath);
                if (!audioFile.exists()) {
                    Toast.makeText(context, "Ses dosyası bulunamadı!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mediaPlayer.setDataSource(filePath);
            }
            
            mediaPlayer.setLooping(isLooping);
            currentPlayingTitle = title;
            
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                if (playbackListener != null) {
                    playbackListener.onPlaybackStarted(title, mp.getDuration());
                    startProgressUpdates();
                }
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                if (!isLooping) {
                    releaseMediaPlayer();
                    if (playbackListener != null) {
                        playbackListener.onPlaybackStopped();
                    }
                }
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(context, "Ses çalınırken hata oluştu.", Toast.LENGTH_SHORT).show();
                releaseMediaPlayer();
                if (playbackListener != null) {
                    playbackListener.onPlaybackStopped();
                }
                return true;
            });
        } catch (IOException e) {
            Toast.makeText(context, "Ses dosyası yüklenemedi.", Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        } catch (IllegalArgumentException e) {
            Toast.makeText(context, "Ses dosyası yolu geçersiz.", Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        }
    }

    public void releaseMediaPlayer() {
        stopProgressUpdates();
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (Exception e) {
                // Sadece kritik hataları logla
                Log.e(TAG, "MediaPlayer release hatası", e);
            } finally {
                mediaPlayer = null;
                if (playbackListener != null) {
                    playbackListener.onPlaybackStopped();
                }
            }
        }
    }

    public AudioFileAdapter(Context context, List<AudioFileEntity> audioFiles, List<CategoryEntity> allCategories, OnAudioFileDeleteListener deleteListener, Integer initialFilteredCategoryId) {
        this.context = context;
        this.audioFiles = new ArrayList<>();
        if (audioFiles != null) {
            this.audioFiles.addAll(audioFiles);
        }
        this.deleteListener = deleteListener;
        this.allCategoriesList = new ArrayList<>();
        if (allCategories != null) {
            this.allCategoriesList.addAll(allCategories);
        }
        this.currentFilteredCategoryId = initialFilteredCategoryId;
        Log.d(TAG, "AudioFileAdapter created with " + (audioFiles != null ? audioFiles.size() : 0) + " audio files");
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ses, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        AudioFileEntity currentAudio = audioFiles.get(position);
        holder.textViewSesAdi.setText(currentAudio.title);

        if (currentFilteredCategoryId == null) {
            holder.textViewKategoriAdi.setVisibility(View.VISIBLE);
            String kategoriAdi = "Kategorisiz";
            if (currentAudio.categoryIdFK != null) {
                for (CategoryEntity category : allCategoriesList) {
                    if (category.categoryId == currentAudio.categoryIdFK) {
                        kategoriAdi = category.name;
                        break;
                    }
                }
            }
            holder.textViewKategoriAdi.setText(kategoriAdi);
        } else {
            holder.textViewKategoriAdi.setVisibility(View.GONE);
        }

        holder.setClickListeners(currentAudio, this);
    }

    @Override
    public int getItemCount() {
        return audioFiles == null ? 0 : audioFiles.size();
    }

    public void updateAudioFiles(List<AudioFileEntity> newAudioFiles, List<CategoryEntity> newAllCategories, Integer newFilteredCategoryId) {
        if (this.audioFiles == null) {
            this.audioFiles = new ArrayList<>();
        }
        this.audioFiles.clear();
        
        if (newAudioFiles != null) {
            if (newFilteredCategoryId == null) {
                this.audioFiles.addAll(newAudioFiles);
            } else {
                for (AudioFileEntity audio : newAudioFiles) {
                    if (audio.categoryIdFK != null && audio.categoryIdFK.equals(newFilteredCategoryId)) {
                        this.audioFiles.add(audio);
                    }
                }
            }
        }

        if (this.allCategoriesList == null) {
            this.allCategoriesList = new ArrayList<>();
        }
        this.allCategoriesList.clear();
        if (newAllCategories != null) {
            this.allCategoriesList.addAll(newAllCategories);
        }
        this.currentFilteredCategoryId = newFilteredCategoryId;
        
        notifyDataSetChanged();
    }

    static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSesAdi;
        TextView textViewKategoriAdi;
        ImageButton buttonDeleteSes;

        AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSesAdi = itemView.findViewById(R.id.txtSesAdi);
            textViewKategoriAdi = itemView.findViewById(R.id.txtKategoriAdi);
            buttonDeleteSes = itemView.findViewById(R.id.buttonDeleteSes);
        }

        void setClickListeners(AudioFileEntity audio, AudioFileAdapter adapter) {
            itemView.setOnClickListener(v -> adapter.playSound(audio.filePath, audio.title));
            
            if (buttonDeleteSes != null && adapter.deleteListener != null) {
                buttonDeleteSes.setOnClickListener(v -> 
                    adapter.deleteListener.onDeleteAudioFile(audio.audioId, audio.title));
            }
        }
    }
}