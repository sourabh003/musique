package com.example.musique.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Functions {
    public static void closeKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void showLoading(boolean show, ProgressBar loading) {
        if (show) {
            loading.setVisibility(View.VISIBLE);
        } else {
            loading.setVisibility(View.INVISIBLE);
        }
    }

    public static String getModifiedDate(String time) {
        Calendar calendar = Calendar.getInstance();
        String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        calendar.setTimeInMillis(Long.parseLong(time));
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        return mDay + "/" + months[mMonth] + "/" + mYear;
    }

    public static String generateID() {
        int len = 10;
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    public static void saveToInternalStorage(String data, String filename, Context context) throws IOException {
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        //default mode is PRIVATE, can be APPEND etc.
        fos.write(data.getBytes());
        fos.close();
    }

    @SuppressLint("DefaultLocale")
    public static String getModifiedDuration(int time) {
        String duration;
        if (time > 3600000) {
            duration = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
                    TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                    TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        } else {
            duration = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                    TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
        }
        return duration;
    }

    public static String encryptName(String id, String album) {
        return album + Constants.SEPERATER + id;
    }

    public static String decryptName(String encryptedName) {
        return encryptedName.split(Constants.SEPERATER)[0];
    }

    public static String decryptID(String encryptedName) {
        return encryptedName.split(Constants.SEPERATER)[1];
    }

    public static int getValueInDP(int sizeInDP, Context context) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP, context.getResources().getDisplayMetrics());
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public static String saveImageToInternalStorage(Context context, Bitmap bitmap) {
        File directory = new File(context.getFilesDir(), Constants.IMAGES_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String name = System.currentTimeMillis() + Constants.IMAGE_JPG;
        new Thread(() -> {
            File path = new File(directory, name);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.close();
                Log.d("Functions", "saveImageToInternalStorage: " + path + " : " + path.exists());
            } catch (Exception e) {
                Log.e("SAVE_IMAGE", e.getMessage(), e);
            }
        }).start();
        return name;
    }

    public static void deleteImageFromInternalStorage(Context context, String image) {
        File directory = new File(context.getFilesDir(), Constants.IMAGES_DIRECTORY);
        new Thread(() -> {
            File path = new File(directory, image);
            if (path.exists()){
                path.delete();
            }
        }).start();
    }

    public static File getPlaylistImage(Context context, String image) {
        File directory = new File(context.getFilesDir(), Constants.IMAGES_DIRECTORY);
        return new File(directory, image);
    }

    public static Bitmap compressBitmap(Bitmap original){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        original.compress(Bitmap.CompressFormat.JPEG, 50, out);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
    }
}
