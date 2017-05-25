package com.orangelabs.task.utility.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.orangelabs.task.R;
import com.orangelabs.task.model.object.Image;
import com.orangelabs.task.model.object.SearchQuery;
import com.orangelabs.task.model.storage.sharedPrefrences.PrefrencesHelper;
import com.orangelabs.task.model.storage.sqlite.FlickrCache;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private String keyword;
    private List<Image> mImageList;
    private int appWidgetId;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        initializeImages();
    }

    private void initializeImages() {
        FlickrCache flickrCache = new FlickrCache(mContext);
        SearchQuery lastQuery = flickrCache.getLastQuery();
        if (lastQuery != null) {
            keyword = PrefrencesHelper.getLastQueryKeyword(mContext);
            mImageList = new ArrayList<>();
            for (String imagesId : lastQuery.getImagesIds()) {
                Image image = new Image();
                image.setThumbnail(flickrCache.getCachedBitmapImage(imagesId, Image.TYPE_THUMBNAIL));
                mImageList.add(image);
            }
        }
        else {
            keyword = "No items";
            mImageList = new ArrayList<>();
        }
    }

    @Override
    public void onCreate() {
        initializeImages();
    }

    @Override
    public void onDataSetChanged() {
        initializeImages();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mImageList == null ? 0 : mImageList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.image_thumbnail);
        view.setTextViewText(R.id.activity_gallery, keyword);
        view.setImageViewBitmap(R.id.thumbnail, mImageList.get(position).getThumbnail());
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
