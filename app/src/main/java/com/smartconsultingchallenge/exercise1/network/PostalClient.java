package com.smartconsultingchallenge.exercise1.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class PostalClient {
    private static final Object LOCK = new Object();
    private static PostalService sInstance;
    private static String BASE_URL = "https://raw.githubusercontent.com/centraldedados/codigos_postais/a91fb37bd485b8da578f3874bd40477504dddf91/";

    public synchronized static PostalService getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new Retrofit
                        .Builder()
                        .baseUrl(BASE_URL)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build()
                        .create(PostalService.class);
            }
        }
        return sInstance;
    }
}
