package com.os.bakingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.looptech.bakingapp.R;
import com.os.bakingapp.RecipesRepo;
import com.os.bakingapp.adapters.RecipesAdapter;
import com.os.bakingapp.models.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.OnRecipeSelectedListener {

    @BindView(R.id.recipesRecyclerView)
    RecyclerView recipesRecyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Disposable disposable;
    private RecipesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new RecipesAdapter(this, this);
        recipesRecyclerView.setHasFixedSize(true);

        boolean useGridLayout = getResources().getBoolean(R.bool.useGridLayout);
        if (useGridLayout) {
            GridLayoutManager manager = new GridLayoutManager(this, 2);
            recipesRecyclerView.setLayoutManager(manager);
        } else {
            LinearLayoutManager manager = new LinearLayoutManager(this);
            recipesRecyclerView.setLayoutManager(manager);
        }

        recipesRecyclerView.setAdapter(adapter);

        updateRecipes();
    }

    private void updateRecipes() {
        progressBar.setVisibility(View.VISIBLE);

        disposable = RecipesRepo.getInstance(this).getAllRecipes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> progressBar.setVisibility(View.INVISIBLE))
                .subscribe(recipes -> adapter.updateData(recipes), Timber::e);
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra(RecipeDetailsActivity.RECIPE_EXTRA, recipe);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) disposable.dispose();
        super.onDestroy();
    }
}
