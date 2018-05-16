package com.os.bakingapp.repo;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.os.bakingapp.models.Recipe;
import com.os.bakingapp.models.RecipeImage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Omar on 14-May-18 11:17 AM.
 */
public class RecipesRepo {
    private static RecipesRepo instance;
    private Gson gson;
    private Context context;
    private RecipesApiService apiService;

    private RecipesRepo(Context context) {
        this.gson = new GsonBuilder().create();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://go.udacity.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();

        this.apiService = retrofit.create(RecipesApiService.class);

        this.context = context;
    }

    public static synchronized RecipesRepo getInstance(Context context) {
        if (instance == null) {
            instance = new RecipesRepo(context);
        }

        return instance;
    }

    public Observable<List<Recipe>> getAllRecipes() {
        Observable<List<RecipeImage>> recipesImages = getAllRecipesImages();

        return apiService.getRecipes()
                .zipWith(recipesImages, (recipes, recipeImages) -> {
                    for (int i = 0; i < recipes.size(); i++) {
                        recipes.get(i).imageUrl = recipeImages.get(i).imageUrl;
                    }

                    return recipes;
                });
    }

    public Observable<List<RecipeImage>> getAllRecipesImages() {
        return Observable.create(emitter -> {
            try {
                Type type = new TypeToken<ArrayList<RecipeImage>>() {}.getType();

                InputStream inputStream = context.getAssets().open("recipes_images.json");
                JsonReader reader = new JsonReader(new InputStreamReader(inputStream));

                List<RecipeImage> list = gson.fromJson(reader, type);

                emitter.onNext(list);
                emitter.onComplete();
            } catch (IOException e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }
}
