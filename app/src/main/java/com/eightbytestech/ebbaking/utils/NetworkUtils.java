package com.eightbytestech.ebbaking.utils;

import com.eightbytestech.ebbaking.BuildConfig;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vishalsaxena on 01/06/17.
 */

public class NetworkUtils {
    /**
     * Method to create a new Retrofit instance
     * Add the API key {@link BakingApiInterceptor#intercept(Interceptor.Chain)}
     * Show debug information only in DEBUG build
     *
     * @return Retrofit - used for building API calls
     */
    public static Retrofit buildRetrofit() {
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(new BakingApiInterceptor());

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(loggingInterceptor);
        }

        return new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net/")
                .client(httpClient.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
