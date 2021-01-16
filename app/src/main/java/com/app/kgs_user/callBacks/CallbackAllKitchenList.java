package com.app.kgs_user.callBacks;

import com.app.kgs_user.model.KitchenData;

import java.util.ArrayList;

public interface CallbackAllKitchenList {
    void onSuccess(ArrayList<KitchenData> getRestaurant);
    void onError(String message);
    void onNetworkError(String message);
}
