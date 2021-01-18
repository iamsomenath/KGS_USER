package com.app.kgs_user.signup_mvp

import android.app.Activity
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.app.kgs_user.utils.Constants
import com.app.kgs_user.utils.RequestQueueSingleton
import com.app.kgs_user.utils.SessionManager
import java.util.*


internal class SignupInterceptor {

    internal interface OnSignUpFinishedListener {
        fun onSuccess(response: String)
        fun onSignupFailure(errResponse: String)
    }

    fun signup(
        email: String,
        name: String,
        password: String,
        contact: String,
        signupPresenter: SignupPresenter,
        activity: Activity
    ) {

        val requestQueue: RequestQueue = RequestQueueSingleton.getInstance(activity).requestQueue
        val REQ_TAG = "SignUp"

        val url = Constants.ROOT_URL + "signup"
        val jsonObjectRequest = object : StringRequest(Method.POST, url, Response.Listener { response ->

            signupPresenter.onSuccess(response.toString())

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
            Log.d("TEST", error.toString())
            signupPresenter.onSignupFailure(errorCode.toString() + "")
        }) {
            /*@Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }*/

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["name"] = name
                params["contact"] = contact
                params["email"] = email
                params["password"] = password
                params["dev_key"] = SessionManager(activity).getDevKey!!
                Log.d("TSEST", params.toString())
                return params
            }
        }

        jsonObjectRequest.tag = REQ_TAG
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            30000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)
    }
}
