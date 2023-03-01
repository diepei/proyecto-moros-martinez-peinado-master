package es.upv.etsit.aatt.paco.trabajoaatt;

import es.upv.etsit.aatt.paco.trabajoaatt.retrofit.CryptoList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

interface APIInterface {

    @Headers("X-CMC_PRO_API_KEY: 72311af0-4b64-4272-986e-53e9399ef53f")
    @GET("/v1/cryptocurrency/listings/latest?")
    Call<CryptoList> doGetUserList(@Query("limit") String page);
}

