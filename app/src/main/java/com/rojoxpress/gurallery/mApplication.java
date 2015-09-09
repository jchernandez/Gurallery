package com.rojoxpress.gurallery;

import android.app.Application;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.rojoxpress.gurallery.utils.BitmapLruCache;


public class mApplication extends Application {

    ImageLoader imageLoader;
    BitmapLruCache bitmapLruCache;

    @Override
    public void onCreate() {
        super.onCreate();
        bitmapLruCache = new BitmapLruCache();
        imageLoader = new ImageLoader(Volley.newRequestQueue(getApplicationContext()), bitmapLruCache);
    }
}
