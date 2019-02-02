package com.smartconsultingchallenge.exercise1.network;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

public interface PostalService {
    @GET("data/codigos_postais.csv")
    Single<ResponseBody> getPostalCodes();
}
