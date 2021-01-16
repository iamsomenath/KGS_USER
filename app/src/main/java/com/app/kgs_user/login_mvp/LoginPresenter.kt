package com.app.kgs_user.login_mvp

import android.app.Activity

class LoginPresenter internal constructor(private var loginView: LoginView?, private val loginInteractor: LoginInteractor) :
    LoginInteractor.OnLoginFinishedListener {

    fun validateCredentials(username: String, password: String, activity: Activity) {
        if (loginView != null) {
            loginView!!.showProgress()
        }
        loginInteractor.login(username, password, this, activity)
    }

    override fun onSuccess(response: String) {
        if (loginView != null) {
            loginView!!.hideProgress()
            loginView!!.navigateToHome(response)
        }
    }

    override fun onLoginFailure(errResponse: String) {
        if (loginView != null) {
            loginView!!.hideProgress()
            loginView!!.setLoginError(errResponse)
        }
    }
}
