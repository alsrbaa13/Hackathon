package com.magmatart.staffapp;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by magma on 2017-08-31.
 */

public class CustomStart extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "MavenPro-Medium.otf"));
    }
}
