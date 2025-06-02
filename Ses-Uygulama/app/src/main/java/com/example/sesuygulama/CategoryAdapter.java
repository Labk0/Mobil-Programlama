package com.example.sesuygulama;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sesuygulama.data.db.CategoryEntity;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<CategoryEntity> categories;
    private final OnCategoryDeleteListener deleteListener;

    public interface OnCategoryDeleteListener {
        void onDeleteClicked(int categoryId);
    }

    public CategoryAdapter(List<CategoryEntity> categories, OnCategoryDeleteListener deleteListener) {
        this.categories = categories;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_manage, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryEntity currentCategory = categories.get(position);
        holder.textViewCategoryName.setText(currentCategory.name);

        if (!currentCategory.isDeletable) {
            holder.buttonDeleteCategory.setVisibility(View.GONE);
        } else {
            holder.buttonDeleteCategory.setVisibility(View.VISIBLE);
            holder.buttonDeleteCategory.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClicked(currentCategory.categoryId);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    public void setCategories(List<CategoryEntity> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategoryName;
        ImageButton buttonDeleteCategory;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.textViewCategoryNameItem);
            buttonDeleteCategory = itemView.findViewById(R.id.buttonDeleteCategory);
        }
    }
}