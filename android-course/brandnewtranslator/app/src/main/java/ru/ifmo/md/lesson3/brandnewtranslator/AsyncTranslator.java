package ru.ifmo.md.lesson3.brandnewtranslator;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by vadim on 28/09/14.
 */
public class AsyncTranslator extends AsyncTask<String, Void, String> {
    protected static final String TAG = "AsyncTranslator";

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground() started");

        // translate strings[0] here
        return "yo"; // translated word
    }
}
