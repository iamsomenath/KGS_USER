package com.app.kgs_user.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.constraintlayout.widget.Constraints
import com.app.kgs_user.callBacks.CallBackMenuList
import com.app.kgs_user.rest.ApiInterface
import com.app.kgs_user.utils.Constants
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MenuViewModel : BaseViewModel() {

    var apiInterface: ApiInterface = getAPIInterface()
    private lateinit var callback: CallBackMenuList

    fun setCallback(callback: CallBackMenuList) {
        this.callback = callback
    }

    fun getMenuList(user_id: String) {
        apiCallKitchenList(user_id)
    }

    @SuppressLint("CheckResult")
    private fun apiCallKitchenList(user_id: String) {

        /*val userResponseObservable = apiInterface.todaysMenu(user_id)
        userResponseObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ restResponse ->
                Log.d("RESPONSE", restResponse.toString())
                *//*if (restResponse.status == 200) {
                    callback.onSuccess(restResponse.data!!)
                } else {
                    callback.onError(restResponse.message!!)
                }*//*
            }, { e -> callback.onNetworkError(Constants.NETWORK_ERROR_MESSAGE) })*/

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.ROOT_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(ApiInterface::class.java)
        val call: Call<ResponseBody> = service.todaysMenu(user_id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.body() != null) {
                    callback.onSuccess(response.body()!!.string())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(Constraints.TAG, "onFailure: " + t.message)
                callback.onNetworkError(Constants.NETWORK_ERROR_MESSAGE)
            }
        })
    }
}