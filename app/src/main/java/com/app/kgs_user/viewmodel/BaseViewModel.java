package com.app.kgs_user.viewmodel;

import androidx.lifecycle.ViewModel;

import com.app.kgs_user.helper.NetworkHelperOther;
import com.app.kgs_user.rest.ApiInterface;

public class BaseViewModel extends ViewModel {

    private static ApiInterface apiInterface;

    /*ApiInterface getAPIInterface() {
        if (apiInterface == null) {
            apiInterface = NetworkHelper.getClient().create(ApiInterface.class);
        }
        return apiInterface;
    }*/

    public ApiInterface getAPIInterface() {
        if (apiInterface == null) {
            apiInterface = NetworkHelperOther.INSTANCE.getClient().create(ApiInterface.class);
        }
        return apiInterface;
    }
}
