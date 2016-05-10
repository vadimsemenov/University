package ru.ifmo.android_2015.citycam.webcams;

import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Константы для работы с Webcams API
 */
public final class Webcams {
    private static final String DEV_ID = "a025e892dac62548da270502e1d31caf";

    private static final String BASE_URL = "http://api.webcams.travel/rest";

    private static final String PARAM_DEVID = "devid";
    private static final String PARAM_METHOD = "method";
    private static final String PARAM_LAT = "lat";
    private static final String PARAM_LON = "lng";
    private static final String PARAM_FORMAT = "format";

    private static final String METHOD_NEARBY = "wct.webcams.list_nearby";

    private static final String FORMAT_JSON = "json";

    private static final Random RANDOM = new Random(3339);

    /**
     * Возвращает URL для выполнения запроса Webcams API для получения
     * информации о веб-камерах рядом с указанными координатами в формате JSON.
     */
    public static URL createNearbyUrl(double latitude, double longitude)
            throws MalformedURLException {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_METHOD, METHOD_NEARBY)
                .appendQueryParameter(PARAM_LAT, Double.toString(latitude))
                .appendQueryParameter(PARAM_LON, Double.toString(longitude))
                .appendQueryParameter(PARAM_DEVID, DEV_ID)
                .appendQueryParameter(PARAM_FORMAT, FORMAT_JSON)
                .build();
        return new URL(uri.toString());
    }

    public static String parseJson(JsonReader reader) throws IOException {
        List<String> urls = new ArrayList<>();
        reader.beginObject(); // response
        while (reader.hasNext()) {
            if ("webcams".equals(reader.nextName())) {
                Log.d(TAG, "parseJson: webcams");
                reader.beginObject(); // webcamsObject
                while (reader.hasNext()) {
                    if ("webcam".equals(reader.nextName())) {
                        Log.d(TAG, "parseJson: webcam");
                        reader.beginArray(); // webcamsArray
                        while (reader.hasNext()) {
                            reader.beginObject(); // webcamObject
                            while (reader.hasNext()) {
                                if ("preview_url".equals(reader.nextName())) {
                                    Log.d(TAG, "parseJson: preview_url");
                                    urls.add(reader.nextString());
                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject(); // webcamObject
                        }
                        reader.endArray(); // webcamsArray
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject(); // webcamsObject
            } else {
                reader.skipValue();
            }
        }
        reader.endObject(); // response
        return urls.size() == 0 ? null : urls.get(RANDOM.nextInt(urls.size()));
    }

    private Webcams() {}

    private static final String TAG = "Webcams";
}
