package com.arktech.waqasansari.mindpalace;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by WaqasAhmed on 5/9/2016.
 */
public class Utility {

    public static boolean isMemoryEdited = false;

    public static Bitmap scaledBitmap(String imgFilePath, Bitmap d){
        int nh = (int) ( d.getHeight() * (1024 / d.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(d, 768, nh, true);
        return setImage(imgFilePath, scaled);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    public static Bitmap setImage(String filePath, Bitmap bitmap){
        File curFile = new File(filePath);
        Bitmap rotatedBitmap = null;

        try {
            ExifInterface exif = new ExifInterface(curFile.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0.0f) {matrix.preRotate(rotationInDegrees);}
            rotatedBitmap = Bitmap.createBitmap(bitmap,0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


        }catch(IOException ex){
            ex.printStackTrace();
        }
        return rotatedBitmap;
    }

    public static String getFormattedDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmma", Locale.getDefault());
        Date date1 = null;
        Calendar temp = Calendar.getInstance();
        try {
            date1 = dateFormat.parse(date);
            temp.setTime(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        return newFormat.format(date1);
    }

    public static boolean checkIfGreater(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmma", Locale.getDefault());
        Date currentDate = new Date();
        Date checkDate = dateFormat.parse(date);
        return currentDate.getTime() < checkDate.getTime();
    }

    public static Bitmap decodeSampledBitmap(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap scaled = BitmapFactory.decodeFile(filePath, options);
        return setImage(filePath, scaled);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
