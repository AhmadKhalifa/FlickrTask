package com.orangelabs.task.utility;

import android.support.design.widget.Snackbar;
import android.view.View;

public class UserCommunication {

    private View mRootView;
    private Snackbar mSnackbar;

    public UserCommunication(View rootView){
        mRootView = rootView;
    }

    public void showMessage(String message){
        showMessage(message, Snackbar.LENGTH_LONG);
    }

    public void showMessage(String message, int duration){
        dismissMessageIfAny();
        mSnackbar = Snackbar.make(mRootView, message, duration);
        mSnackbar.show();
    }

    public void dismissMessageIfAny() {
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
    }

}
