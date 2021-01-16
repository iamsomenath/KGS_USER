package com.app.kgs_user.login_mvp

interface LoginView {

    fun showProgress()
    fun hideProgress()
    fun setLoginError(errResponse: String)
    fun navigateToHome(response: String)
}