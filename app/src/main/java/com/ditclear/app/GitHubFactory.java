package com.ditclear.app;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 页面描述：
 * <p>
 * Created by ditclear on 2017/2/9.
 */

public class GitHubFactory {

    private static Retrofit mInstance;

    public static Retrofit getInstance() {
        if (mInstance == null) {
            mInstance = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return mInstance;
    }

}
