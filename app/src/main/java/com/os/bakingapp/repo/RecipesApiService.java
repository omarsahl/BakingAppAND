package com.os.bakingapp.repo;

import com.os.bakingapp.models.Recipe;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by Omar on 16-May-18 10:02 AM.
 */
public interface RecipesApiService {
    @GET("android-baking-app-json")
    Observable<List<Recipe>> getRecipes();
}
