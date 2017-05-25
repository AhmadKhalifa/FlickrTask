package com.orangelabs.task.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;

/**
 * This class is used for encoding/decoding (compressing/decompressing) images in order to easily
 * store and load them with the cache (local database).
 */
public class ImageEncoder {

    /**
     * Private default constructor. to prevent from making instances of this class.
     */
    private ImageEncoder(){}

    /**
     * This method takes a Bitmap image and converts it to an array of bytes.
     * @param image the Bitmap image to be compressed (encoded).
     * @return byte array of encoded data.
     */
    public static byte[] encode(Bitmap image){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * This method takes a byte array and converts it to a Bitmap image.
     * @param encodedImage the byte array of encoded data to be decompressed (decoded).
     * @return Bitmap image of the decoded data.
     */
    public static Bitmap decode(byte[] encodedImage){
        return BitmapFactory.decodeByteArray(encodedImage, 0, encodedImage.length);
    }
}
