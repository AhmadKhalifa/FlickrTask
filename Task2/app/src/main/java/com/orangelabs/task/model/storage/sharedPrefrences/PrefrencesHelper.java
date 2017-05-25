package com.orangelabs.task.model.storage.sharedPrefrences;

import android.content.Context;

public class PrefrencesHelper {

    private static final String LAST_QUERY_KEYWORD = "last_query_keyword";

    public synchronized static String getLastQueryKeyword(Context context) {
        return PreferencesHandler.getString(context, LAST_QUERY_KEYWORD, "");
    }

    public synchronized static void setLastQueryKeyword(Context context, String keyword) {
        PreferencesHandler.setString(context, LAST_QUERY_KEYWORD, keyword);
    }

}
