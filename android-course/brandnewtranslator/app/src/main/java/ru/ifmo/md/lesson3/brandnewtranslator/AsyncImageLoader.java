package ru.ifmo.md.lesson3.brandnewtranslator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by vadim on 28/09/14.
 */
public class AsyncImageLoader extends AsyncTask<String, Bitmap, Bitmap> {
    protected static final String TAG = "AsyncImageLoader";

    @Override
    protected Bitmap doInBackground(String... strings) {
        Log.d(TAG, "doInBackground() worked with word '" + strings[0] + "'");

        Bitmap result = BitmapFactory.decodeResource(AnotherActivity.RESOURCES, R.drawable.source);

        for (int i = 0; i < 10; ++i)
            publishProgress(result);
        return result;
    }
}
