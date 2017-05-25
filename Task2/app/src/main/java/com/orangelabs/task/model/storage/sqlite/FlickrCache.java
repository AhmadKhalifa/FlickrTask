package com.orangelabs.task.model.storage.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import com.orangelabs.task.model.object.Image;
import com.orangelabs.task.model.object.SearchQuery;
import com.orangelabs.task.model.storage.sharedPrefrences.PrefrencesHelper;
import com.orangelabs.task.utility.ImageDownloader;
import com.orangelabs.task.utility.ImageEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * This class plays an important rule in this software, as it caches each downloaded image in a
 * two-column table which makes data retrieval is much easier and faster.
 */
public class FlickrCache extends SQLiteOpenHelper implements Serializable {

    // Database credentials.
    private final static String DATABASE_NAME = "Flickr Cache";
    private final static int DATABASE_VERSION = 1;

    // Database tables and columns.
    private final static String TABLE_THUMBNAIL = "image_thumbnail";
    private final static String TABLE_FULL_SIZED = "full_sized";
    private final static String TABLE_LAST_QUERY = "last_query";
    private final static String KEY_ID = "ID";
    private final static String KEY_IMAGE = "image";

    private Context mContext;

    /**
     * Parametrized constructor
     * @param context the context in which this class is instantiated.
     */
    public FlickrCache(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * This method is invoked to create the database, by building it's tables.
     * @param sqLiteDatabase the database to be created.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_THUMBNAIL_TABLE =
                String.format("CREATE TABLE IF NOT EXISTS %s (", TABLE_THUMBNAIL)
                        + String.format("%s TEXT PRIMARY KEY, ", KEY_ID)
                        + String.format("%s BLOB", KEY_IMAGE)
                        + ")";
        String CREATE_FULL_SIZED_TABLE =
                String.format("CREATE TABLE IF NOT EXISTS %s (", TABLE_FULL_SIZED)
                        + String.format("%s TEXT PRIMARY KEY, ", KEY_ID)
                        + String.format("%s BLOB", KEY_IMAGE)
                        + ")";
        String CREATE_LAST_QUERY_TABLE =
                String.format("CREATE TABLE IF NOT EXISTS %s (", TABLE_LAST_QUERY)
                        + String.format("%s TEXT PRIMARY KEY, ", KEY_ID)
                        + ")";
        sqLiteDatabase.execSQL(CREATE_THUMBNAIL_TABLE);
        sqLiteDatabase.execSQL(CREATE_FULL_SIZED_TABLE);
        sqLiteDatabase.execSQL(CREATE_LAST_QUERY_TABLE);
    }

    /**
     * This method is used to update the database.
     * @param sqLiteDatabase the database to be updated.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_THUMBNAIL);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FULL_SIZED);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LAST_QUERY);
        onCreate(sqLiteDatabase);
    }

    /**
     * This method is used to cache an image given its desired size, and image ID.
     * If image is already cached, it sets the cached data to the Image object.
     * Otherwise it downloads it (if the first download failed) and caches it.
     * @param image the image to be cached.
     * @param imageType the physical (bitmap) size of the image.
     */
    public void cache(final Image image, final int imageType){
        Bitmap cachedData = getCachedData(image, imageType);
        if (cachedData != null){
            if (imageType == Image.TYPE_THUMBNAIL){
                image.setThumbnail(cachedData);
            }
            else if (imageType == Image.TYPE_FULL_SIZED){
                image.setFullSized(cachedData);
            }
            return;
        }
        if (imageType == Image.TYPE_THUMBNAIL && image.getThumbnail() == null
                || imageType == Image.TYPE_FULL_SIZED && image.getFullSized() == null){
            new ImageDownloader(mContext, image, imageType) {
                @Override
                public void onPostExecute(Bitmap bitmap) {
                    writeImage(image, imageType);
                }
            }.execute();
        }
        else {
            writeImage(image, imageType);
        }
    }

    /**
     * This method searches for the cached data for the parametrized image, and returns the cached
     * data if exists, null otherwise.
     * @param image the image that needs its cached data.
     * @param imageType the physical (bitmap) size of the image.
     * @return the bitmap image of the Image object.
     */
    public Bitmap getCachedData(Image image, int imageType){
        Bitmap cachedData = null;
        String table = imageType == Image.TYPE_THUMBNAIL ? TABLE_THUMBNAIL : TABLE_FULL_SIZED;
        String query = String.format("SELECT * FROM %s where \"%s\" = \"%s\"",
                table, KEY_ID, image.getId());
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()){
            cachedData = ImageEncoder.decode(cursor.getBlob(1));
        }
        cursor.close();
        return cachedData;
    }

    public Bitmap getCachedBitmapImage (String imageID, int imageType) {
        Image image = new Image();
        image.setId(imageID);
        return getCachedData(new Image(), imageType);
    }

    public void setLastQuery(SearchQuery lastQuery) {
        PrefrencesHelper.setLastQueryKeyword(mContext, lastQuery.getKeyword());
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_LAST_QUERY);
        ContentValues contentValues = new ContentValues();
        for (String imageID : lastQuery.getImagesIds()) {
            contentValues.put(KEY_ID, imageID);
            sqLiteDatabase.insert(TABLE_LAST_QUERY, null, contentValues);
        }
    }

    public SearchQuery getLastQuery() {
        SearchQuery lastQuery = new SearchQuery(PrefrencesHelper.getLastQueryKeyword(mContext));
        String query = String.format("SELECT * FROM %s ;", TABLE_LAST_QUERY);
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                lastQuery.addImageId(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lastQuery;
    }

    /**
     * Private method which is called by cache() method.
     * Does the low-level database storing process.
     * Implemented to prevent code duplication.
     * @param image the image that needs its cached data.
     * @param imageType the physical (bitmap) size of the image.
     */
    private void writeImage (Image image, int imageType){
        byte[] encodedData = ImageEncoder.encode(
                imageType == Image.TYPE_THUMBNAIL ?
                        image.getThumbnail() :
                        image.getFullSized()
        );
        SQLiteDatabase sqLiteDatabase = FlickrCache.this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ID, image.getId());
        contentValues.put(KEY_IMAGE, encodedData);
        String table = imageType == Image.TYPE_THUMBNAIL ?
                TABLE_THUMBNAIL :
                TABLE_FULL_SIZED;
        sqLiteDatabase.insert(table, null, contentValues);
    }

    /**
     * This method is used to delete all records in the database.
     */
    public void deleteAllData(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_THUMBNAIL);
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_FULL_SIZED);
    }
}
