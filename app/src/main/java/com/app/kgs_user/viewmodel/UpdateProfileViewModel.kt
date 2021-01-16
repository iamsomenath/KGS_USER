package com.app.kgs_user.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import com.app.kgs_user.callBacks.CallbackUpdateProfile
import com.app.kgs_user.rest.ApiInterface
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UpdateProfileViewModel : BaseViewModel() {

    var apiInterface: ApiInterface = getAPIInterface()
    private lateinit var callback: CallbackUpdateProfile

    fun setCallback(callback: CallbackUpdateProfile) {
        this.callback = callback
    }

    fun updateProfile(user_id: String, name: String, email: String, contact : String) {
        apiUpdateProfile(user_id, name, email, contact)
    }

    @SuppressLint("CheckResult")
    private fun apiUpdateProfile(user_id: String, name: String, email: String, contact : String) {

        val userResponseObservable = apiInterface.update_user_profile(user_id, name, email, contact)
        userResponseObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ restResponse ->
                Log.d("RESPONSE", restResponse.toString())
                if (restResponse.status == 200) {
                    callback.onSuccess(restResponse.data!!)
                } else {
                    callback.onError(restResponse.message!!)
                }
            }, {
                    e -> callback.onNetworkError(Constants.NETWORK_ERROR_MESSAGE)
            })
    }
}