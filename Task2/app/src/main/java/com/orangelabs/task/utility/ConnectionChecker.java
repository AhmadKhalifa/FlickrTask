package com.orangelabs.task.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This class is important for checking the internet connectivity before invoking any network-
 * related AsyncTasks.
 */
public class ConnectionChecker {

    /**
     * Private default constructor. to prevent from making instances of this class.
     */
    private ConnectionChecker(){}

    /**
     * This static method checks the internet connection and returns true if the device is connected
     * to the internet, false otherwise.
     * @param context the context in which this method is called.
     * @return true if device is connected to the internet, false otherwise.
     */
    public static boolean isNetworkAvailable(Context context){
        NetworkInfo info =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                        .getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }
}