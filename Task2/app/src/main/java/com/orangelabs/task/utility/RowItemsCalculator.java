package com.orangelabs.task.utility;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * This class is used to get the number of available items in one row given the item width.
 */
public class RowItemsCalculator {

    /**
     * Private default constructor. to prevent from making instances of this class.
     */
    private RowItemsCalculator(){}

    /**
     * This method calculates the screen size given the context in which this method is invoked,
     * and then calculates how many possible items of a given width could be in one row.
     * @param context the context in which this method is invoked.
     * @param itemWidth the item width.
     * @return number of possible items that could be in one row.
     */
    public static int getNumberOfColumns(Context context, int itemWidth){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = Math.round(displayMetrics.widthPixels / displayMetrics.density);
        return screenWidth / itemWidth;
    }
}
