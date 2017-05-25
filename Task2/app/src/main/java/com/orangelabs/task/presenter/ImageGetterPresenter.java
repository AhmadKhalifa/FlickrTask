package com.orangelabs.task.presenter;

import android.content.Context;
import android.graphics.Bitmap;

import com.orangelabs.task.model.object.Image;
import com.orangelabs.task.network.FlickrAPITask;
import com.orangelabs.task.utility.ConnectionChecker;
import com.orangelabs.task.utility.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

public class ImageGetterPresenter {

    private static final String ERROR_LOADING_IMAGES = "Error loading images";
    private static final String ERROR_GETTING_THUMBNAIL_SIZE = "Error getting thumbnail size";
    private static final String ERROR_GETTING_FULL_SIZE = "Error getting full size";
    private static final String ERROR_INTERNET_CONNECTION = "Please check your internet connection";
    private static final String NO_RESULTS = "No results";

    public void searchForImages(
            final SearchForImagesCallback callback,
            String APIKey,
            Context context,
            String keyword,
            int pageNumber) {
        if (callback != null) {
            if (ConnectionChecker.isNetworkAvailable(context)) {
                new FlickrAPITask(
                        APIKey,
                        keyword,
                        context,
                        FlickrAPITask.LOAD_IMAGES, pageNumber) {
                    @Override
                    protected void onPostExecute(List<Image> newImages) {
                        if (newImages != null) {
                            if (newImages.isEmpty()) {
                                callback.onSearchForImagesFailure(NO_RESULTS);
                            } else {
                                callback.onSearchForImagesSuccess(newImages);
                            }
                        } else {
                            callback.onSearchForImagesFailure(ERROR_LOADING_IMAGES);
                        }
                    }
                }.execute();
            }
            else {
                callback.onSearchForImagesFailure(ERROR_INTERNET_CONNECTION);
            }
        }
    }

    public void getThumbnailSizeImage(final GetThumbnailSizeImageCallback callback,
                                      Context context, final Image image){
        if (callback != null && context != null && image != null) {
            if (ConnectionChecker.isNetworkAvailable(context)) {
                new ImageDownloader(context, image, Image.TYPE_THUMBNAIL) {
                    @Override
                    public void onPostExecute(Bitmap bitmap) {
                        if (bitmap != null) {
                            image.setThumbnail(bitmap);
                            callback.onGetThumbnailSizeImageSuccess(image);
                        } else {
                            callback.onGetThumbnailSizeImageFailure(ERROR_GETTING_THUMBNAIL_SIZE);
                        }
                    }
                }.execute();
            }
            else {
                callback.onGetThumbnailSizeImageFailure(ERROR_INTERNET_CONNECTION);
            }
        }
    }

    public void getFullSizeImage(final GetFullSizeImageCallback callback,
                                 Context context, final Image image){
        if (callback != null && context != null && image != null) {
            new ImageDownloader(context, image, Image.TYPE_FULL_SIZED) {
                @Override
                public void onPostExecute(Bitmap bitmap) {
                    if (bitmap != null) {
                        image.setFullSized(bitmap);
                        callback.onGetFullSizeImageSuccess(image);
                    }
                    else {
                        callback.onGetFullSizeImageFailure(ERROR_GETTING_FULL_SIZE);
                    }
                }
            }.execute();
        }
    }

    public interface SearchForImagesCallback {
        void onSearchForImagesSuccess(List<Image> images);
        void onSearchForImagesFailure(String message);
    }

    public interface GetFullSizeImageCallback {
        void onGetFullSizeImageSuccess(Image thumbnailSizeImage);
        void onGetFullSizeImageFailure(String message);
    }

    public interface GetThumbnailSizeImageCallback {
        void onGetThumbnailSizeImageSuccess(Image fullSizeImage);
        void onGetThumbnailSizeImageFailure(String message);
    }
}
