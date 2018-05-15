package com.os.bakingapp.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Omar on 15-May-18 4:44 PM.
 */
public class RecipeIngredientsListRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeIngredientsListRemoteViewsFactory(this.getApplicationContext());
    }
}
