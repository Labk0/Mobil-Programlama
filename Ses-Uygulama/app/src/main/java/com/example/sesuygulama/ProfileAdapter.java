package com.example.sesuygulama;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sesuygulama.data.db.ProfileEntity;

import java.util.Objects;

public class ProfileAdapter extends ListAdapter<ProfileEntity, ProfileAdapter.ProfileViewHolder> {

    private final OnProfileClickListener clickListener;
    private final OnProfileDeleteListener deleteListener;

    public interface OnProfileClickListener {
        void onProfileClick(ProfileEntity profile);
    }
    public interface OnProfileDeleteListener {
        void onDeleteClick(ProfileEntity profile);
    }

    public ProfileAdapter(OnProfileClickListener clickListener, OnProfileDeleteListener deleteListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    // DiffUtil, RecyclerView'ın listeyi verimli bir şekilde güncellemesine yardımcı olur
    private static final DiffUtil.ItemCallback<ProfileEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<ProfileEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProfileEntity oldItem, @NonNull ProfileEntity newItem) {
            return Objects.equals(oldItem.profileId, newItem.profileId); // benzersiz ID'leri karşılaştır
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProfileEntity oldItem, @NonNull ProfileEntity newItem) {
            // isim veya diğer içerikler değişmişse güncelle
            return Objects.equals(oldItem.name, newItem.name) &&
                    oldItem.isDefault == newItem.isDefault;
        }
    };

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_profile, parent, false);
        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        ProfileEntity currentProfile = getItem(position);
        holder.textViewProfileName.setText(currentProfile.name);

        if (clickListener != null) {
            holder.itemView.setOnClickListener(v -> clickListener.onProfileClick(currentProfile));
        }

        if (deleteListener != null) {
            holder.buttonDeleteProfile.setOnClickListener(v -> deleteListener.onDeleteClick(currentProfile));
        }
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewProfileName;
        private final ImageButton buttonDeleteProfile;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProfileName = itemView.findViewById(R.id.textViewProfileNameItem);
            buttonDeleteProfile = itemView.findViewById(R.id.buttonDeleteProfile);
        }
    }
}