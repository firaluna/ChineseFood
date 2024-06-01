package com.example.chineseplate;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.chineseplate.Fragment.RecipeFragment;
import com.example.chineseplate.Fragment.SaveFragment;
import com.example.chineseplate.Fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView IV_Recipe = findViewById(R.id.IV_Recipe);
        ImageView IV_Search = findViewById(R.id.IV_Search);
        ImageView IV_Save = findViewById(R.id.IV_Save);

        FragmentManager fragmentManager = getSupportFragmentManager();

        RecipeFragment recipeFragment = new RecipeFragment();

        Fragment fragment = fragmentManager.findFragmentByTag(RecipeFragment.class.getSimpleName());

        if (!(fragment instanceof RecipeFragment)){
            fragmentManager.beginTransaction().add(R.id.frame_container, recipeFragment).commit();
        }


        IV_Recipe.setOnClickListener(v -> {
            RecipeFragment recipesFragment = new RecipeFragment();
            fragmentManager.beginTransaction().replace(R.id.frame_container, recipesFragment)
                    .addToBackStack(null).commit();
        });

        IV_Search.setOnClickListener(v -> {
            SearchFragment searchFragment = new SearchFragment();
            fragmentManager.beginTransaction().replace(R.id.frame_container, searchFragment)
                    .addToBackStack(null).commit();
        });

        IV_Save.setOnClickListener(v -> {
            SaveFragment saveFragment = new SaveFragment();
            fragmentManager.beginTransaction().replace(R.id.frame_container, saveFragment)
                    .addToBackStack(null).commit();
        });


    }
}