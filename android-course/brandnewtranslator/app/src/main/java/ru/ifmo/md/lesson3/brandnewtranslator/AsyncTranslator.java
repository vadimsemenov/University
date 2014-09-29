package ru.ifmo.md.lesson3.brandnewtranslator;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import org.json.JSONObject;

public class AsyncTranslator extends AsyncTask<String, Void, String> {
    protected static final String TAG = "AsyncTranslator";
    protected static final String MAIN_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    protected static final String API_KEY = "trnsl.1.1.20140928T165116Z.593c092fb80c5665.489e094f9a1d86b0043990e21a3c5abc1cdce854";
    protected static final String LANGUAGE = "en-ru";

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground() started");

        String word = strings[0];

        try {
            URL url = new URL(MAIN_URL + "?key=" + API_KEY + "&text=" + word + "&lang=" + LANGUAGE);
            HttpsURLConnection request = (HttpsURLConnection) url.openConnection();
            request.connect();

            BufferedReader streamReader = new BufferedReader(new InputStreamReader((InputStream) request.getContent(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputString;
            while ((inputString = streamReader.readLine()) != null)
                responseStrBuilder.append(inputString);
            JSONObject answer = new JSONObject(responseStrBuilder.toString());
            String translatedWord = answer.get("text").toString();
            return translatedWord;
        } catch (Throwable t) {
            return "fail :(";
        }
    }
}
