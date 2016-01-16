package com.autstudent.autschedular;

import android.app.Application;

import com.alamkanak.weekview.WeekView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.Parse;

/**
 * Created by wilzo on 24/12/2015.
 */
public class AUTschedular extends Application {
    private WeekView mWeekView;
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
    public WeekView getmWeekView() {
        return mWeekView;
    }

    public void setmWeekView(WeekView mWeekView) {
        this.mWeekView = mWeekView;
    }
}
