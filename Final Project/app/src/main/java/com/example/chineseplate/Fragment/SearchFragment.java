package com.example.chineseplate.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.chineseplate.API.APIConfig;
import com.example.chineseplate.API.ApiService;
import com.example.chineseplate.Model.Recipes;
import com.example.chineseplate.Adapter.RecipeAdapter;
import com.example.chineseplate.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {
    private SearchView searchView;
    private ApiService apiService;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipes> recipeList = new ArrayList<>();
    private ProgressBar progressBar;
    private Handler handler = new Handler(Looper.myLooper());
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recylerView);
        progressBar = view.findViewById(R.id.progressBar);
        searchView = view.findViewById(R.id.search);
        recipeAdapter = new RecipeAdapter(recipeList, getContext());
        recyclerView.setAdapter(recipeAdapter);
        apiService = APIConfig.getClient().create(ApiService.class);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    showError("Please enter a search query");
                } else {
                    fetchRecipes(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    recipeList.clear();
                    recipeAdapter.notifyDataSetChanged();
                } else {
                    fetchRecipes(newText);
                }
                return false;
            }
        });
    }

    private void fetchRecipes(String query) {
        progressBar.setVisibility(View.VISIBLE);

        Call<List<Recipes>> call = apiService.getFoods();
        call.enqueue(new Callback<List<Recipes>>() {
            @Override
            public void onResponse(Call<List<Recipes>> call, Response<List<Recipes>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        try {
                            Thread.sleep(1000);
                            handler.post(() -> {
                                recipeList.clear();
                                for (Recipes recipe : response.body()) {
                                    if (recipe.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                        recipeList.add(recipe);
                                    }
                                }
                                recipeAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                if (recipeList.isEmpty()) {
                                    showError("No recipes found");
                                }
                            });
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    showError("Failed to retrieve recipes");
                }
            }

            @Override
            public void onFailure(Call<List<Recipes>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Internet connection is unavailable");
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
