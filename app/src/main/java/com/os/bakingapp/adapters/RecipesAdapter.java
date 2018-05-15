package com.os.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.looptech.bakingapp.R;
import com.os.bakingapp.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Omar on 14-May-18 9:03 AM.
 */
public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    private Context context;
    private OnRecipeSelectedListener listener;
    private List<Recipe> recipes;

    public RecipesAdapter(Context context, OnRecipeSelectedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void updateData(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        if (recipes == null) return 0;
        return recipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatTextView recipeName;
        private ImageView recipeImage;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.recipeImage = itemView.findViewById(R.id.recipeImage);
            this.recipeName = itemView.findViewById(R.id.recipeName);
        }

        public void bind(Recipe recipe) {
            this.recipeName.setText(recipe.recipeName);
            Picasso.get()
                    .load(recipe.imageUrl)
                    .placeholder(R.drawable.place_holder)
                    .into(this.recipeImage);
        }

        @Override
        public void onClick(View v) {
            listener.onRecipeSelected(recipes.get(getAdapterPosition()));
        }
    }

    public interface OnRecipeSelectedListener {
        void onRecipeSelected(Recipe recipe);
    }
}
