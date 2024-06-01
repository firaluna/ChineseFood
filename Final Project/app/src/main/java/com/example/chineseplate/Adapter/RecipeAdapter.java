package com.example.chineseplate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chineseplate.Model.Recipes;
import com.example.chineseplate.DetailActivity;
import com.example.chineseplate.R;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>{

    public static List<Recipes> recipeList;
    private Context context;

    public RecipeAdapter(List<Recipes> recipeList, Context context) {
        RecipeAdapter.recipeList = recipeList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder holder, int position) {
        Recipes recipe = recipeList.get(position);
        holder.recipeName.setText(recipe.getTitle());
        System.out.println(recipe.getImage());
        Glide.with(holder.itemView.getContext())
                .load(recipe.
                getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.images);

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView images;
        private TextView recipeName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.image);
            recipeName = itemView.findViewById(R.id.name_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Recipes clickedRecipe = recipeList.get(position);
                        Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                        intent.putExtra("RECIPE_ID", clickedRecipe.getId());
                        intent.putExtra("FROM_DATABASE", false);

                        itemView.getContext().startActivity(intent);
                    }
                }
            });

        }

    }
}
