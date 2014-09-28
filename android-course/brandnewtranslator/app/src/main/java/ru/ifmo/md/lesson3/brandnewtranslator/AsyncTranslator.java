package ru.ifmo.md.lesson3.brandnewtranslator;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.*;

public class AsyncTranslator extends AsyncTask<String, Void, String> {
    protected static final String TAG = "AsyncTranslator";

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground() started");

        String word = strings[0];
        String sURL = "http://www.google.ru";

        try {
            URL url = new URL("http://example.com/pages/");
            HttpsURLConnection request = (HttpsURLConnection) url.openConnection();
            request.connect();

            JSONObject obj = new JSONObject(" .... ");
            String translatedWord = obj.getString("text");
            return translatedWord;
        } catch (Throwable t) {
            throw new RuntimeException();
        }
        /*JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //convert the input stream to a json element
        JsonObject rootobj = root.getAsJsonObject(); //may be an array, may be an object.
        zipcode=rootobj.get("zipcode").getAsString();//just grab the zipcode*/
    }

    //@Override
    protected void postOnExecute(String s) {

    }
}
