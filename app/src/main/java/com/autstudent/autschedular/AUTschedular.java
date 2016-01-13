package com.autstudent.autschedular;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by wilzo on 24/12/2015.
 */
public class AUTschedular extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
