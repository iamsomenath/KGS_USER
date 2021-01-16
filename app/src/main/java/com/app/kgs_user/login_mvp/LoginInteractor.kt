package com.app.kgs_user.login_mvp

import android.app.Activity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.utils.RequestQueueSingleton
import java.util.*


internal class LoginInteractor {

    internal interface OnLoginFinishedListener {
        fun onSuccess(response: String)
        fun onLoginFailure(errResponse: String)
    }

    fun login(
        contact: String,
        password: String,
        loginPresenter: LoginPresenter,
        activity: Activity
    ) {

        val requestQueue: RequestQueue
        val REQ_TAG = "Login"

        val url = Constants.ROOT_URL + "login"
        val jsonObjectRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->

            loginPresenter.onSuccess(response.toString())

        }, Response.ErrorListener { error ->
            var errorCode = 0
            when (error) {
                is TimeoutError -> errorCode = -7
                is NoConnectionError -> errorCode = -1
                is AuthFailureError -> errorCode = -6
                is ServerError -> errorCode = -5
                is NetworkError -> errorCode = -2
                is ParseError -> errorCode = -8
            }
            loginPresenter.onLoginFailure(errorCode.toString() + "")
        }) {
            /*@Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                //headers.put("Authorization", Elfinpay.getAppSharedPreference().getToken());
                return headers
            }*/
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["contact"] = contact
                params["password"] = password
                return params
            }
        }

        requestQueue = RequestQueueSingleton.getInstance(activity).requestQueue
        jsonObjectRequest.tag = REQ_TAG
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            30000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)
    }
}
