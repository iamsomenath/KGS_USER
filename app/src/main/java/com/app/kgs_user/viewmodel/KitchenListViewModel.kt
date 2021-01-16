package com.app.kgs_user.viewmodel

import android.annotation.SuppressLint
import com.app.kgs_user.callBacks.CallbackAllKitchenList
import com.app.kgs_user.rest.ApiInterface
import com.app.kgs_user.utils.Constants.NETWORK_ERROR_MESSAGE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class KitchenListViewModel : BaseViewModel() {

    internal var apiInterface: ApiInterface = getAPIInterface()
    private lateinit var callback: CallbackAllKitchenList

    fun setCallback(callback: CallbackAllKitchenList) {
        this.callback = callback
    }

    fun getRestrurentList(lat: String, lng: String) {
        apiCallKitchenList(lat, lng)
    }

    @SuppressLint("CheckResult")
    private fun apiCallKitchenList(lat: String, lng: String) {

        val userResponseObservable = apiInterface.getKitchen(lat, lng)
        userResponseObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ restResponse ->
                if (restResponse.status == 200) {
                    callback.onSuccess(restResponse.data)
                } else {
                    callback.onError(restResponse.message)
                }
            }, { e -> callback.onNetworkError(NETWORK_ERROR_MESSAGE) })
    }
}
//22.6404544 88.3647198 4, MNK Rd, Ashokgarh, Kolkata, West Bengal 700036, India