package com.os.bakingapp;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by Omar on 14-May-18 11:14 AM.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
