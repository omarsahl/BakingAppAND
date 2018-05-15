package com.os.bakingapp;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.os.bakingapp.models.Recipe;
import com.os.bakingapp.models.RecipePreview;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Omar on 14-May-18 11:17 AM.
 */
public class RecipesRepo {
    private static RecipesRepo instance;
    private Gson gson;
    private Context context;

    private RecipesRepo(Context context) {
        this.gson = new GsonBuilder().create();
        this.context = context;
    }

    public static synchronized RecipesRepo getInstance(Context context) {
        if (instance == null) {
            instance = new RecipesRepo(context);
        }

        return instance;
    }

    public Observable<List<RecipePreview>> getAllRecipesPreviews() {
        return Observable.create(emitter -> {
            try {
                Type type = new TypeToken<ArrayList<RecipePreview>>() {}.getType();

                InputStream inputStream = context.getAssets().open("recipes_data.json");
                JsonReader reader = new JsonReader(new InputStreamReader(inputStream));

                List<RecipePreview> list = gson.fromJson(reader, type);

                emitter.onNext(list);
                emitter.onComplete();
            } catch (IOException e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }

    public Observable<List<Recipe>> getAllRecipes() {
        return Observable.create(emitter -> {
            try {
                Type type = new TypeToken<ArrayList<Recipe>>() {}.getType();

                InputStream inputStream = context.getAssets().open("recipes_data.json");
                JsonReader reader = new JsonReader(new InputStreamReader(inputStream));

                List<Recipe> list = gson.fromJson(reader, type);

                emitter.onNext(list);
                emitter.onComplete();
            } catch (IOException e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }
}
