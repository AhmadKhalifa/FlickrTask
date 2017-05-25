package com.orangelabs.task.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.orangelabs.task.model.object.Image;
import com.orangelabs.task.model.storage.sqlite.FlickrCache;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is used for both downloading and caching images in the background.
 */
public abstract class ImageDownloader extends AsyncTask<Void, Void, Bitmap> {

    // fields
    private FlickrCache flickrCache;
    private Image image;
    private int imageType;
    private boolean normal;

    /**
     * Parametrized constructor.
     * @param image the image which the user wants to download and/or cache.
     * @param imageType the physical (bitmap) size of the image.
     */
    public ImageDownloader(Context context, Image image, int imageType){
        normal = false;
        this.flickrCache = new FlickrCache(context);
        this.image = image;
        this.imageType = imageType;
        if (imageType == Image.TYPE_NORMAL){
            this.imageType = Image.TYPE_THUMBNAIL;
            normal = true;
        }
    }

    /**
     * The core method in this class, in which image is retrieved from the cache first.
     * If data exists in the cache, it returns is faster than downloading it.
     * else it downloads it and then caches it.
     * @return the bitmap needed and resulted from downloading and/or caching.
     */
    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            Bitmap data = flickrCache.getCachedData(image, imageType);
            if (data == null){
                String url;
                if (imageType == Image.TYPE_FULL_SIZED){
                    url = image.getImageURL();
                }
                else {
                    url = normal ? image.getNormalImageURL() : image.getThumbnailImageURL();
                }
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap =  BitmapFactory.decodeStream(input);

                if (imageType == Image.TYPE_THUMBNAIL){
                    image.setThumbnail(bitmap);
                }
                else if (imageType == Image.TYPE_FULL_SIZED){
                    image.setFullSized(bitmap);
                }
                flickrCache.cache(image, imageType);
                return bitmap;
            }
            else {
                return data;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * An abstract method so as to be implemented in the context in which it's called from, which
     * makes it easier to get the results once they are ready.
     * @param bitmap the resulted bitmap image from the background thread processing.
     */
    @Override
    protected abstract void onPostExecute(Bitmap bitmap);
}
