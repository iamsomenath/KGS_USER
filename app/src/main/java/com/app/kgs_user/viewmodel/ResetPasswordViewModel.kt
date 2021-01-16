package com.app.kgs_user.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import com.app.kgs_user.callBacks.CallbackResetPassword
import com.app.kgs_user.rest.ApiInterface
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ResetPasswordViewModel : BaseViewModel() {

    var apiInterface: ApiInterface = getAPIInterface()
    private lateinit var callback: CallbackResetPassword

    fun setCallback(callback: CallbackResetPassword) {
        this.callback = callback
    }

    fun resetPassword(user_id: String, password: String, oldpassword: String) {
        apiResetPassword(user_id, password, oldpassword)
    }

    @SuppressLint("CheckResult")
    private fun apiResetPassword(user_id: String, password: String, oldpassword: String) {

        val userResponseObservable = apiInterface.change_password(user_id, password, oldpassword)
        userResponseObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ restResponse ->
                Log.d("RESPONSE", restResponse.toString())
                if (restResponse.status == 200) {
                    callback.onResetPasswordSuccess(restResponse.data!!)
                } else {
                    callback.onResetPasswordError(restResponse.message!!)
                }
            }, {
                    e -> callback.onNetworkError(Constants.NETWORK_ERROR_MESSAGE)
            })
    }
}