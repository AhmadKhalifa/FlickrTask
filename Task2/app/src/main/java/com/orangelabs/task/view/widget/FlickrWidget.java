package com.orangelabs.task.view.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.orangelabs.task.R;
import com.orangelabs.task.model.object.SearchQuery;
import com.orangelabs.task.model.storage.sqlite.FlickrCache;
import com.orangelabs.task.utility.widget.WidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class FlickrWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        FlickrCache flickrCache = new FlickrCache(context);
        SearchQuery searchQuery = flickrCache.getLastQuery();
        String keyword = searchQuery == null ? "" : searchQuery.getKeyword();
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.flickr_widget);
        views.setTextViewText(R.id.appwidget_search_keyword, keyword);
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(appWidgetId, R.id.appwidget_gridView, intent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @SuppressWarnings("deprecation")
    private RemoteViews initializeViews(Context context, int widgetId) {
        FlickrCache flickrCache = new FlickrCache(context);
        SearchQuery searchQuery = flickrCache.getLastQuery();
        String keyword = searchQuery == null ? "" : searchQuery.getKeyword();
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.flickr_widget);
        views.setTextViewText(
                R.id.appwidget_search_keyword,
                keyword);
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(widgetId, R.id.appwidget_gridView, intent);
        return views;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = initializeViews(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.appwidget_gridView);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

