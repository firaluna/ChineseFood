package com.example.chineseplate.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chineseplate.Model.Recipes;
import com.example.chineseplate.Adapter.SaveAdapter;
import com.example.chineseplate.DBConfig;
import com.example.chineseplate.R;
import java.util.ArrayList;
import java.util.List;

public class SaveFragment extends Fragment {

    private RecyclerView recyclerView;
    private SaveAdapter saveAdapter;
    private List<Recipes> recipeList;
    private DBConfig dbConfig;
    private TextView tvSearch, noResep;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save, container, false);

        tvSearch = view.findViewById(R.id.tv_save);
        noResep = view.findViewById(R.id.noResep);
        recyclerView = view.findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        dbConfig = new DBConfig(getContext());
        recipeList = new ArrayList<>();
        saveAdapter = new SaveAdapter(recipeList,getContext());
        recyclerView.setAdapter(saveAdapter);

        loadSavedRecipes();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSavedRecipes();
    }

    private void loadSavedRecipes() {
        recipeList.clear();
        Cursor cursor = dbConfig.getAllRecipes();
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));

                    Recipes recipe = new Recipes(id, name, imageUrl);
                    recipeList.add(recipe);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
            saveAdapter.notifyDataSetChanged();
        }

        if (recipeList.isEmpty()) {
            noResep.setVisibility(View.VISIBLE);
        } else {
            noResep.setVisibility(View.GONE);
        }
    }
}
