package com.example.chineseplate.API;

import com.example.chineseplate.Model.RecipeDetail;
import com.example.chineseplate.Model.Recipes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ApiService {

    @Headers({
        "X-RapidAPI-Host: chinese-food-db.p.rapidapi.com",
        "X-RapidAPI-Key: 0df36becbdmsh046bb6f97ddcbdap148189jsndbf60193ceb9"
    })
    @GET("/")
    Call<List<Recipes>> getFoods();

    @Headers({
            "X-RapidAPI-Host: chinese-food-db.p.rapidapi.com",
            "X-RapidAPI-Key: 0df36becbdmsh046bb6f97ddcbdap148189jsndbf60193ceb9 "
    })
    @GET("{id}")
    Call<RecipeDetail> getDetails(@Path("id") int recipeId);

}
