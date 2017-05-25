package com.orangelabs.task.model.object;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Image implements Serializable {

    // values (names) of the keys that are saved in the JSON format of an image object
    private final static String JSON_ID = "id";
    private final static String JSON_SECRET = "secret";
    private final static String JSON_SERVER = "server";
    private final static String JSON_FARM = "farm";
    private final static String JSON_TITLE = "title";

    // type of the physical (Bitmap) image of the Image object
    public final static int TYPE_THUMBNAIL = 0;
    public final static int TYPE_NORMAL = 1;
    public final static int TYPE_FULL_SIZED = 2;

    private final static String PHOTOS_OBJECT = "photos";
    private final static String PHOTO_ARRAY = "photo";

    // fields
    private String id;
    private String secret;
    private String server;
    private int farm;
    private String title;
    private Bitmap thumbnail;
    private Bitmap fullSized;

    // getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getFullSized() {
        return fullSized;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the full path for the image in the full sized mode.
     */
    public String getImageURL(){
        String url = buildURL();
        return url == null ? null : url + "_b.jpg";
    }

    /**
     * @return the full path for an image in the image_thumbnail mode.
     */
    public String getThumbnailImageURL(){
        String url = buildURL();
        return url == null ? null : url + "_q.jpg";
    }

    /**
     * @return the full path for an image in the normal mode.
     */
    public String getNormalImageURL(){
        String url = buildURL();
        return url == null ? null : url + ".jpg";
    }

    /**
     * This method is mainly used by getImageURL() and getThumbnailURL()
     * @return mostly the full path of an image using it's data (i.e. ID, and secret).
     */
    private String buildURL(){
        return id == null ?
                null :
                String.format(Locale.getDefault(),
                        "https://farm%d.staticflickr.com/%s/%s_%s",
                        farm,
                        server,
                        id,
                        secret
                );
    }

    /**
     * This method sets the physical (bitmap) full-sized image to the Image object.
     * @param fullSized the bitmap full-sized image after being downloaded
     */
    public void setFullSized(Bitmap fullSized) {
        this.fullSized = fullSized;
    }

    /**
     * This method sets the physical (bitmap) image_thumbnail image to the Image object.
     * @param thumbnail the bitmap image_thumbnail image after being downloaded
     */
    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * This static function is used to parses JSON Object for an image to an Image object
     * @param jsonImage the JSON object to be parsed
     * @return the parsed image from the JSON object
     */
    public static Image parseImage(JSONObject jsonImage){
        try {
            return new ImageBuilder()
                    .id(jsonImage.getString(JSON_ID))
                    .secret(jsonImage.getString(JSON_SECRET))
                    .server(jsonImage.getString(JSON_SERVER))
                    .farm(jsonImage.getInt(JSON_FARM))
                    .title(jsonImage.getString(JSON_TITLE))
                    .build();
        }
        catch (Exception e){
            return null;
        }
    }

    /**
     * This method takes the JSON string resulted from the API response and parses it into an
     * ArrayList of Images to be used.
     * @param jsonImagesPage the JSON string resulted from the API response.
     * @return the parsed ArrayList of Images.
     */
    public static List<JSONObject> splitImages(String jsonImagesPage){
        try {
            JSONObject jResult = new JSONObject(jsonImagesPage);
            JSONObject jPage = jResult.getJSONObject(PHOTOS_OBJECT);
            JSONArray jPhotosArray = jPage.getJSONArray(PHOTO_ARRAY);
            List<JSONObject> jsonImages = new ArrayList<>();
            for (int i = 0; i < jPhotosArray.length(); i++) {
                jsonImages.add(jPhotosArray.getJSONObject(i));
            }
            return jsonImages;
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    /**
     * This inner class is used for building the Image object without worrying about constructors
     * and setters.
     */
    private static class ImageBuilder {

        // fields
        private String id;
        private String secret;
        private String server;
        private int farm;
        private String title;

        // setters
        public ImageBuilder id(String id){
            this.id = id;
            return this;
        }

        ImageBuilder secret(String secret){
            this.secret = secret;
            return this;
        }

        ImageBuilder server(String server){
            this.server = server;
            return this;
        }

        ImageBuilder farm(int farm){
            this.farm = farm;
            return this;
        }

        ImageBuilder title(String title){
            this.title = title;
            return this;
        }

        /**
         * This method builds the gathered components by the ImageBuilder and makes an Image
         * object with these fields.
         * @return the built Image.
         */
        Image build(){
            Image image = new Image();
            image.id = this.id;
            image.secret = this.secret;
            image.server = this.server;
            image.farm = this.farm;
            image.title = this.title;
            return image;
        }
    }
}
