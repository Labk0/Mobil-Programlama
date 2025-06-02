package com.example.sesuygulama;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sesuygulama.data.db.CategoryEntity;
import com.example.sesuygulama.data.preferences.ProfilePreferences;

import java.util.ArrayList;
import java.util.List;

public class ManageCategoriesActivity extends AppCompatActivity {
    private Toolbar toolbarManageCategories;
    private RecyclerView recyclerViewCategories;
    private EditText editTextCategoryName;
    private Button buttonAddCategory;

    private CategoryViewModel categoryViewModel;
    private CategoryAdapter adapter;
    private String currentProfileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        toolbarManageCategories = findViewById(R.id.toolbarManageCategories);
        setSupportActionBar(toolbarManageCategories);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        buttonAddCategory = findViewById(R.id.buttonAddCategory);

        currentProfileId = ProfilePreferences.getActiveProfileId(this);
        if (currentProfileId == null) {
            Toast.makeText(this, "Aktif profil bulunamadı!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        setupRecyclerView();
        observeCategories();

        buttonAddCategory.setOnClickListener(v -> addCategory());
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(new ArrayList<>(), categoryId -> {
            confirmAndDeleteCategory(categoryId);
        });
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategories.setAdapter(adapter);
    }

    private void observeCategories() {
         categoryViewModel.getCategoriesForProfile(currentProfileId).observe(this, categories -> {
             if (categories != null) {
                 adapter.setCategories(categories);
             }
         });
        Toast.makeText(this, "Kategori gözlemleme ayarlanacak.", Toast.LENGTH_SHORT).show();
    }

    private void addCategory() {
        String categoryName = editTextCategoryName.getText().toString().trim();
        if (TextUtils.isEmpty(categoryName)) {
            editTextCategoryName.setError("Kategori adı boş olamaz!");
            return;
        }

        CategoryEntity newCategory = new CategoryEntity(categoryName, currentProfileId, true);
        categoryViewModel.insertCategory(newCategory);

        androidx.media3.common.util.Log.d("ManageCategories", "Yeni kategori eklendi: " + newCategory.name + " Profile ID: " + newCategory.profileOwnerId);

        editTextCategoryName.setText("");
        Toast.makeText(this, "'" + categoryName + "' eklendi.", Toast.LENGTH_SHORT).show();
    }

    private void confirmAndDeleteCategory(int categoryId) {
        LiveData<CategoryEntity> categoryLiveData = categoryViewModel.getCategoryById(categoryId, currentProfileId);
        categoryLiveData.observe(this, new androidx.lifecycle.Observer<CategoryEntity>() {
            @Override
            public void onChanged(CategoryEntity categoryEntity) {
                categoryLiveData.removeObserver(this);

                if (categoryEntity != null) {
                    if (!categoryEntity.isDeletable) {
                        Toast.makeText(ManageCategoriesActivity.this, "'" + categoryEntity.name + "' silinemez bir kategoridir.", Toast.LENGTH_SHORT).show();
                    } else {
                        showDeleteDialog(categoryEntity.categoryId, categoryEntity.name);
                    }
                } else {
                    Toast.makeText(ManageCategoriesActivity.this, "Onay için kategori bulunamadı.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showDeleteDialog(int categoryId, String categoryName) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Kategoriyi Sil")
                .setMessage("'" + categoryName + "' kategorisini silmek istediğinizden emin misiniz? Bu kategoriye ait sesler kategorisiz kalacaktır.")
                .setPositiveButton("Sil", (dialog, which) -> {
                    categoryViewModel.deleteCategory(categoryId, currentProfileId);
                    Toast.makeText(this, "Kategori silindi.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("İptal", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}