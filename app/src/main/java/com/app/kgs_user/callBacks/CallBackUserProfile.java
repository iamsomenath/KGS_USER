package com.app.kgs_user.callBacks;

import com.app.kgs_user.model.UserData;

public interface CallBackUserProfile {
    void onStarted();
    void onSuccess(UserData userData);
    void onError(String message);
    void onNetworkError(String message);
}
