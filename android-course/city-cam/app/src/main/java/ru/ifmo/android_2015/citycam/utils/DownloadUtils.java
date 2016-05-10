package ru.ifmo.android_2015.citycam.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.ifmo.android_2015.citycam.webcams.Webcams;

/**
 * @author Vadim Semenov <semenov@rain.ifmo.ru>
 */
public final class DownloadUtils {
    /**
     * Выполняет сетевой запрос для скачивания файла, и сохраняет ответ в указанный файл.
     *
     * @param jsonUrl      URL - откуда скачивать (http:// или https://)
     *
     * @throws IOException В случае ошибки выполнения сетевого запроса или записи файла.
     */
    public static Bitmap downloadBitmap(URL jsonUrl) throws IOException {
        Log.d(TAG, "Start downloading json: " + jsonUrl);

        // Выполняем запрос по указанному урлу. Поскольку мы используем только http:// или https://
        // урлы для скачивания, мы приводим результат к HttpURLConnection. В случае урла с другой
        // схемой, будет ошибка.
        HttpURLConnection conn = (HttpURLConnection) jsonUrl.openConnection();
        InputStream in = null;
        URL bitmapUrl = null;
        try {
            // Проверяем HTTP код ответа. Ожидаем только ответ 200 (ОК).
            // Остальные коды считаем ошибкой.
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Received HTTP response code: " + responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new FileNotFoundException("Unexpected HTTP response: " + responseCode
                        + ", " + conn.getResponseMessage());
            }

            // Начинаем читать ответ
            in = conn.getInputStream();
            JsonReader reader = new JsonReader(new InputStreamReader(in));
            String bitmapStringUrl = Webcams.parseJson(reader);
            Log.d(TAG, "downloadBitmap: " + bitmapStringUrl);
            if (bitmapStringUrl == null) {
                return null;
            }
            bitmapUrl = new URL(bitmapStringUrl);
        } finally {
            // Закрываем все потоки и соедиениние
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close HTTP input stream: " + e, e);
                }
            }
            conn.disconnect();
        }
        Log.d(TAG, "Start downloading bitmap: " + bitmapUrl);

        conn = (HttpURLConnection) bitmapUrl.openConnection();
        in = null;
        try {
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Received HTTP response code: " + responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new FileNotFoundException("Unexpected HTTP response: " + responseCode
                        + ", " + conn.getResponseMessage());
            }

            // Начинаем читать ответ
            in = conn.getInputStream();
            return BitmapFactory.decodeStream(in);
        } finally {
            // Закрываем все потоки и соедиениние
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close HTTP input stream: " + e, e);
                }
            }
            conn.disconnect();
        }
    }

    private static final String TAG = "Download";


    private DownloadUtils() {
    }
}
