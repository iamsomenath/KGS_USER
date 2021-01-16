package com.app.kgs_user.rest

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitInstance {

    private var retrofit: Retrofit? = null

    private val sURL = "https://retailerapp.centuryply.com/"
    private val sPHP_PATH = "services/user/"
    private val sMAIN_PHP_PATH = sURL + sPHP_PATH

    var BASE_URL = sMAIN_PHP_PATH

    /**
     * Create an instance of Retrofit object
     */
    val retrofitInstance3: Retrofit
        get() {

            val okkHttpclient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val ongoing = chain.request().newBuilder()
                    chain.proceed(ongoing.build())
                }
                .build()

            if (retrofit == null) {
                retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okkHttpclient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
}