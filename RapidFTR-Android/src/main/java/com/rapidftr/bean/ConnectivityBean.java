package com.rapidftr.bean;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

@EBean
public class ConnectivityBean {

    @SystemService
    protected ConnectivityManager connectivityManager;

    public boolean isOnline() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

}
