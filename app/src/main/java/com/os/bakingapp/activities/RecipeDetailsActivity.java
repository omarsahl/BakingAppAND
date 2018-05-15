package com.os.bakingapp.activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.looptech.bakingapp.R;
import com.os.bakingapp.adapters.IngredientsAdapter;
import com.os.bakingapp.models.Recipe;
import com.os.bakingapp.widgets.RecipeIngredientsListRemoteViewsFactory;
import com.os.bakingapp.widgets.RecipesWidget;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class RecipeDetailsActivity extends AppCompatActivity {
    public static final String RECIPE_EXTRA = "recipe";

    @BindView(R.id.recipeDetailsImage)
    ImageView recipeImage;

    @BindView(R.id.recipeDetailsName)
    TextView recipeName;

    @BindView(R.id.recipeDetailsServings)
    TextView recipeServings;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.ingredientsRv)
    RecyclerView ingredientsRv;

    private IngredientsAdapter adapter;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recipe = getIntent().getParcelableExtra(RECIPE_EXTRA);
        Timber.d("recipe details=%s", recipe);

        adapter = new IngredientsAdapter(this);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, StepsActivity.class);
            intent.putParcelableArrayListExtra(StepsActivity.STEPS_EXTRA, recipe.steps);
            intent.putExtra(StepsActivity.RECIPE_NAME_EXTRA, recipe.recipeName);
            startActivity(intent);
        });

        bindRecipe(recipe);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionAddToWidget:
                updateWidget();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateWidget() {
        Gson gson = new Gson();

        getSharedPreferences(RecipeIngredientsListRemoteViewsFactory.WIDGET_PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(RecipeIngredientsListRemoteViewsFactory.WIDGET_RECIPE_KEY, gson.toJson(recipe))
                .apply();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipesWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetRecipeIngredients);
        RecipesWidget.updateAllWidgets(this, appWidgetManager, appWidgetIds);

        Snackbar.make(fab, "Recipe added to widget", Snackbar.LENGTH_SHORT).show();
    }

    private void bindRecipe(Recipe recipe) {
        Picasso.get().load(recipe.imageUrl).into(recipeImage);
        recipeName.setText(recipe.recipeName);
        recipeServings.setText(String.valueOf(recipe.servings));

        ingredientsRv.setLayoutManager(new LinearLayoutManager(this));
        ingredientsRv.setAdapter(adapter);
        adapter.updateIngredients(recipe.ingredients);
    }
}
