package com.example.chineseplate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBConfig extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Recipes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_RECIPES = "recipes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_INGREDIENTS = "ingredients";
    private static final String COLUMN_METHOD = "method";
    private static final String COLUMN_IMAGE_URL = "image_url";

    public DBConfig(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY , " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, "+
                COLUMN_INGREDIENTS + " TEXT, " +
                COLUMN_METHOD + " TEXT, " +
                COLUMN_IMAGE_URL + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    public boolean deleteRecipe(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("recipes", "name = ?", new String[]{name});
        return result > 0;
    }


    public boolean recipeExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECIPES, null, COLUMN_NAME + "=?", new String[]{name}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean insertRecipe(int id, String name, String description, String ingredients, String method, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, id);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_INGREDIENTS, ingredients);
        contentValues.put(COLUMN_METHOD, method);
        contentValues.put(COLUMN_IMAGE_URL, imageUrl);

        long result = db.insert(TABLE_RECIPES, null, contentValues);
        return result != -1;
    }

    public Cursor getAllRecipes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECIPES, null);
    }

    public Cursor getRecipeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RECIPES, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }
}
