package com.example.chineseplate.Model;

import java.util.List;
import java.util.Map;

public class RecipeDetail {
    private int id;
    private String title;
    private String description;
    private String image;
    private String ingredientsFromDB;
    private String methodFromDB;
    private List<String> ingredients;
    List<Map<String, String>> method;
    
    public RecipeDetail(int id, String title, String description, String image, String ingredientsFromDB, String methodFromDB) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.ingredientsFromDB = ingredientsFromDB;
        this.methodFromDB = methodFromDB;

    }


    public String getIngredientsFromDB() {
        return ingredientsFromDB;
    }

    public void setIngredientsFromDB(String ingredientsFromDB) {
        this.ingredientsFromDB = ingredientsFromDB;
    }

    public String getMethodFromDB() {
        return methodFromDB;
    }

    public void setMethodFromDB(String methodFromDB) {
        this.methodFromDB = methodFromDB;
    }

    public List<Map<String, String>> getMethod() {
        return method;
    }

    public void setMethod(List<Map<String, String>> method) {
        this.method = method;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
