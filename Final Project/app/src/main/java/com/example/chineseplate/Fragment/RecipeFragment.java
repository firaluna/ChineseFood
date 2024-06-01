package com.example.chineseplate.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class RecipeFragment extends Fragment {
    private ApiService apiService;
    private RecipeAdapter recipeAdapter;
    private RecyclerView recyclerView;
    private List<Recipes> recipe = new ArrayList<>();
    private ProgressBar progressBar;
    private Button retry;
    private TextView error;
    private final Handler handler = new Handler(Looper.myLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recipeList_rv);
        apiService = APIConfig.getClient().create(ApiService.class);
        progressBar = view.findViewById(R.id.progressBar);
        error = view.findViewById(R.id.error);
        retry = view.findViewById(R.id.retry);

        getRecipeFromApi();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        return view;
    }

    private void getRecipeFromApi() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Recipes>> call = apiService.getFoods();
        call.enqueue(new Callback<List<Recipes>>() {
            @Override
            public void onResponse(Call<List<Recipes>> call, Response<List<Recipes>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recipe = response.body();
                    executor.execute(() -> {
                        try {
                            Thread.sleep(1000);
                            handler.post(() -> {
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                error.setVisibility(View.GONE);
                                retry.setVisibility(View.GONE);

                                recipeAdapter = new RecipeAdapter(recipe, getContext());
                                recyclerView.setAdapter(recipeAdapter);
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
            public void onFailure(Call<List<Recipes>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showErrorView();
            }
        });
    }

    private void showErrorView() {
        recyclerView.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        retry.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        retry.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                progressBar.setVisibility(View.VISIBLE);
                error.setVisibility(View.GONE);
                retry.setVisibility(View.GONE);
                handler.post(this::getRecipeFromApi);
            } else {
                Toast.makeText(getContext(), "Internet connection still unavailable", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
