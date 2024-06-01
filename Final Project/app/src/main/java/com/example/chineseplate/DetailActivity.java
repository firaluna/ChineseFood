package com.example.chineseplate;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chineseplate.API.APIConfig;
import com.example.chineseplate.API.ApiService;
import com.example.chineseplate.Model.RecipeDetail;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private ImageView backBtn, saveBtn, savedBtn, recipeImage;
    private TextView titleTv, descriptionTv, ingredientsTv, methodTv;
    private ProgressBar progressBar;
    private Button retry;
    private TextView error;
    private RecipeDetail currentRecipe;
    private final Handler handler = new Handler(Looper.myLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private DBConfig dbConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        backBtn = findViewById(R.id.btn_back);
        saveBtn = findViewById(R.id.btn_save);
        savedBtn = findViewById(R.id.btn_saved);
        recipeImage = findViewById(R.id.dt_img);
        titleTv = findViewById(R.id.dt_title);
        descriptionTv = findViewById(R.id.desc);
        ingredientsTv = findViewById(R.id.ingridient);
        methodTv = findViewById(R.id.method);
        progressBar = findViewById(R.id.progressBar);
        error = findViewById(R.id.dt_error);
        retry = findViewById(R.id.dt_retry);

        dbConfig = new DBConfig(this);

        int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
        boolean fromDatabase = getIntent().getBooleanExtra("FROM_DATABASE", false);

        if (recipeId != -1) {
            if (fromDatabase) {
                getRecipeDetailsFromDb(recipeId);
            } else {
                if (isNetworkAvailable()) {
                    getRecipeDetailsFromApi(recipeId);
                } else {
                    showErrorView();
                }
            }
        }


        backBtn.setOnClickListener(v -> finish());

        saveBtn.setOnClickListener(v -> saveRecipe());

        savedBtn.setOnClickListener(v -> deleteRecipe());
    }

    private void getRecipeDetailsFromApi(int recipeId) {
        progressBar.setVisibility(View.VISIBLE);
        hideRecipeViews();
        ApiService apiService = APIConfig.getClient().create(ApiService.class);
        Call<RecipeDetail> call = apiService.getDetails(recipeId);

        call.enqueue(new Callback<RecipeDetail>() {
            @Override
            public void onResponse(Call<RecipeDetail> call, Response<RecipeDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RecipeDetail recipeDetail = response.body();
                    currentRecipe = recipeDetail;
                    executor.execute(() -> {
                        try {
                            Thread.sleep(1000); // Simulate delay
                            handler.post(() -> {
                                progressBar.setVisibility(View.GONE);
                                showRecipeViews();
                                displayRecipeDetails(recipeDetail);
                                updateSaveButtonVisibility();
                            });
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    showErrorView();
                }
            }

            @Override
            public void onFailure(Call<RecipeDetail> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showErrorView();
            }
        });
    }

    private void getRecipeDetailsFromDb(int recipeId) {
        executor.execute(() -> {
            Cursor cursor = dbConfig.getRecipeById(recipeId);
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String ingredients = cursor.getString(cursor.getColumnIndexOrThrow("ingredients"));
                String method = cursor.getString(cursor.getColumnIndexOrThrow("method"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));

                currentRecipe = new RecipeDetail(id, title, description, imageUrl, ingredients, method);
                cursor.close();
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    showRecipeViews();
                    displayRecipeDetailsForDB(currentRecipe);
                    updateSaveButtonVisibility();
                });
            }
        });
    }

    private void displayRecipeDetailsForDB(RecipeDetail recipeDetail) {
        titleTv.setText(recipeDetail.getTitle());
        descriptionTv.setText("Description:\n" + recipeDetail.getDescription());
        ingredientsTv.setText("Ingredients:\n" + recipeDetail.getIngredientsFromDB());
        methodTv.setText("Method:\n" + recipeDetail.getMethodFromDB());

        Glide.with(this)
                .load(recipeDetail.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(recipeImage);
    }

    private void displayRecipeDetails(RecipeDetail recipeDetail) {
        titleTv.setText(recipeDetail.getTitle());
        descriptionTv.setText("Description:\n" + recipeDetail.getDescription());

        StringBuilder ingredientsBuilder = new StringBuilder();
        for (String ingredient : recipeDetail.getIngredients()) {
            ingredientsBuilder.append("- ").append(ingredient).append("\n");
        }
        ingredientsTv.setText("Ingredients:\n" + ingredientsBuilder.toString());

        StringBuilder methodBuilder = new StringBuilder();
        for (Map<String, String> step : recipeDetail.getMethod()) {
            for (Map.Entry<String, String> entry : step.entrySet()) {
                methodBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n\n");
            }
        }
        methodTv.setText("Method:\n" + methodBuilder.toString());

        Glide.with(this)
                .load(recipeDetail.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(recipeImage);
    }

    private void saveRecipe() {
        if (currentRecipe != null) {
            if (dbConfig.recipeExists(currentRecipe.getTitle())) {
                Toast.makeText(this, "Recipe already exists in the database", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder ingredientsBuilder = new StringBuilder();
            for (String ingredient : currentRecipe.getIngredients()) {
                ingredientsBuilder.append("- ").append(ingredient).append("\n");
            }

            StringBuilder methodBuilder = new StringBuilder();
            for (Map<String, String> step : currentRecipe.getMethod()) {
                for (Map.Entry<String, String> entry : step.entrySet()) {
                    methodBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n\n");
                }
            }

            boolean isInserted = dbConfig.insertRecipe(
                    currentRecipe.getId(),
                    currentRecipe.getTitle(),
                    currentRecipe.getDescription(),
                    ingredientsBuilder.toString(),
                    methodBuilder.toString(),
                    currentRecipe.getImage()
            );

            if (isInserted) {
                Toast.makeText(this, "Recipe saved successfully", Toast.LENGTH_SHORT).show();
                updateSaveButtonVisibility();
            } else {
                Toast.makeText(this, "Failed to save recipe", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteRecipe() {
        if (currentRecipe != null) {
            boolean isDeleted = dbConfig.deleteRecipe(currentRecipe.getTitle());
            if (isDeleted) {
                Toast.makeText(this, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();
                updateSaveButtonVisibility();
            } else {
                Toast.makeText(this, "Failed to delete recipe", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateSaveButtonVisibility() {
        if (dbConfig.recipeExists(currentRecipe.getTitle())) {
            saveBtn.setVisibility(View.GONE);
            savedBtn.setVisibility(View.VISIBLE);
        } else {
            saveBtn.setVisibility(View.VISIBLE);
            savedBtn.setVisibility(View.GONE);
        }
    }

    private void showRecipeViews() {
        recipeImage.setVisibility(View.VISIBLE);
        titleTv.setVisibility(View.VISIBLE);
        descriptionTv.setVisibility(View.VISIBLE);
        ingredientsTv.setVisibility(View.VISIBLE);
        methodTv.setVisibility(View.VISIBLE);
    }

    private void hideRecipeViews() {
        recipeImage.setVisibility(View.GONE);
        titleTv.setVisibility(View.GONE);
        descriptionTv.setVisibility(View.GONE);
        ingredientsTv.setVisibility(View.GONE);
        methodTv.setVisibility(View.GONE);
    }

    private void showErrorView() {
        error.setVisibility(View.VISIBLE);
        retry.setVisibility(View.VISIBLE);
        hideRecipeViews();
        retry.setOnClickListener(v -> recreate());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
