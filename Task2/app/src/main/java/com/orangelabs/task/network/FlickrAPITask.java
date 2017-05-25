package com.orangelabs.task.network;

import android.content.Context;
import android.os.AsyncTask;

import com.orangelabs.task.model.object.Image;
import com.orangelabs.task.model.object.SearchQuery;
import com.orangelabs.task.model.storage.sqlite.FlickrCache;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * This class handles all Flickr's API calls in background, without making the main UI thread
 * busy doing these calls.
 */
public abstract class FlickrAPITask extends AsyncTask<Void, Void, List<Image>> {

    // values for being used as parameters in the functions below.
    private final static int IMAGES_PER_PAGE = 25;
    public final static int LOAD_IMAGES = 0;
    private final static int AUTOMATICALLY_RELOAD_IMAGES = 1;
    private final static int MANUALLY_RELOAD_IMAGES = 2;
    private final static int LOAD_MORE_IMAGES = 3;
    private final static String GET = "GET";

    // fields.
    private String APIKey;
    private int taskType;
    private FlickrCache flickrCache;
    private int currentPage;
    private String searchKeyword;
    private Context mContext;

    /**
     * Main constructor.
     * @param APIKey is used for accessing the Flickr's API.
     * @param taskType is the query type for calling the API.
     * @param currentPage is the current page that is being loaded by the gallery, or the next
     *                    page for the loadMoreImages() method
     */
    protected FlickrAPITask(String APIKey, String searchKeyword,
                            Context context, int taskType, int currentPage){
        this.APIKey = APIKey;
        this.searchKeyword = searchKeyword;
        this.taskType = taskType;
        this.flickrCache = new FlickrCache(context);
        this.currentPage = currentPage;
    }

    /**
     * This method is used for retrieving the first page of images from the API.
     * @return an ArrayList of Images that resulted from the API call.
     */
    private List<Image> loadImages(){
        return loadPage(1);
    }

    /**
     * This method is used for retrieving the next page of images from the API.
     * @return an ArrayList of Images that resulted from the API call.
     */
    private List<Image> loadMoreImages(){
        /*
        This returns the next page but as many images are being uploaded per second,
        I would prefer a random number for the next page.
        return loadPage(new Random.nextInt(100));
         */
        return loadPage(currentPage);
    }

    /**
     * This method is used for retrieving the new images in the API.
     * As many images are being uploaded to Flickr server, calling for first 25 images would be
     * enough.
     * @return an ArrayList of Images that resulted from the API call.
     */
    private List<Image> automaticallyReloadImages(){
        return loadImages();
    }

    /**
     * This method is used for removing all the cached images in the cache, and retrieves the first
     * page of the API to be cached and viewed.
     * @return an ArrayList of Images that resulted from the API call.
     */
    private List<Image> manuallyReloadImages(){
        flickrCache.deleteAllData();
        return loadImages();
    }

    /**
     * This method is the actual API call which is used for calling the API given the API key,
     * page number, and number of images per page.
     * After that it parses the JSON images into Image objects and collects them in an ArrayList.
     * @param page the page number for data retrieving.
     * @return an ArrayList of Images that resulted from the API call.
     */
    private List<Image> loadPage(int page){
        try {
            HttpURLConnection urlConnection;
            BufferedReader bufferedReader;
            String urlString =
                    "https://api.flickr.com/services/rest/?" +
                            "method=flickr.photos.search&" +
                            "api_key=" + APIKey + "&" + "text=" + searchKeyword + "&" +
                            "per_page=" + IMAGES_PER_PAGE + "&" +
                            "page=" + page + "&" +
                            "format=json&" +
                            "nojsoncallback=1";
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(GET);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            if (inputStream == null){
                throw new Exception();
            }
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line).append('\n');
            }
            if (stringBuilder.length() == 0){
                throw new Exception();
            }
            String results = stringBuilder.toString();
            List<JSONObject> jsonImages = Image.splitImages(results);
            SearchQuery lastQuery = new SearchQuery(searchKeyword);
            List<Image> images = new ArrayList<>();
            for (JSONObject jsonImage : jsonImages) {
                Image image = Image.parseImage(jsonImage);
                if (image != null) {
                    lastQuery.addImageId(image.getId());
                    images.add(image);
                }
            }
            flickrCache.setLastQuery(lastQuery);
            return images;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    /**
     * The core function in this class which is called in the background. It checks for the query
     * type and calls the right function to achieve the query goal. After that it sends the resulted
     * ArrayList of images to be used then.
     * @return ArrayList of Images to be sent after that to the onPostExecute() method.
     */
    @Override
    protected List<Image> doInBackground(Void... voids) {
        List<Image> newImages = new ArrayList<>();
        switch (taskType){
            case LOAD_IMAGES:
                newImages = loadImages();
                break;
            case AUTOMATICALLY_RELOAD_IMAGES:
                newImages = automaticallyReloadImages();
                break;
            case MANUALLY_RELOAD_IMAGES:
                newImages = manuallyReloadImages();
                break;
            case LOAD_MORE_IMAGES:
                newImages = loadMoreImages();
                break;
        }
        return newImages;
    }

    /**
     * An abstract method so as to be implemented in the context in which it's called from, which
     * makes it easier to get the results once they are ready.
     * @param newImages the resulted ArrayList of Images from the background thread processing.
     */
    @Override
    protected abstract void onPostExecute(List<Image> newImages);
}