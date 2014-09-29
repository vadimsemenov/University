package ru.ifmo.md.lesson3.brandnewtranslator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vadim on 28/09/14.
 */
public class AsyncImageLoader extends AsyncTask<String, Bitmap, List<Bitmap>> {
    protected static final String TAG = "AsyncImageLoader";

//    private ProgressDialog simpleWaitDialog;

    @Override
    protected List<Bitmap> doInBackground(String... words) {
        String uri = "http://yandex.ru/images/search?text=" + words[0] + "&isize=medium&itype=jpg"; //"&isize=eq&iw=50&ih=50&itype=jpg";
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString;
        List<Bitmap> bm = new ArrayList<Bitmap>();
        String[] urls = new String[AnotherActivity.NEED_IMAGES];
        try {
            response = httpclient.execute(new HttpGet(uri));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
                int prev = 0;
                int i = 0;
                while (i < AnotherActivity.NEED_IMAGES) {
                    int pos2 = responseString.indexOf(".jpg", prev);
                    int pos = responseString.indexOf("http://", pos2 - 100);
                    //int pos = responseString.substring(prev, pos2).lastIndexOf("http://"); // why it doesn't work?!
                    if (pos < pos2) {
                        urls[i] = responseString.substring(pos, pos2 + 4);
                        Bitmap bitmap = downloadBitmap(urls[i]);
                        Log.d(TAG, "some image downloaded");
                        bm.add(bitmap);
                        publishProgress(bitmap);
                        i++;
                    }
                    prev = responseString.indexOf("img class", pos2 + 5);
                }
                return bm;
            } else {
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return bm;
    }

    @Override
    protected void onPreExecute() {
        Log.i("Async-Example", "onPreExecute Called");
//        simpleWaitDialog = ProgressDialog.show(AnotherActivity.context, "Wait", "Downloading Image");
    }

    @Override
    protected void onPostExecute(List<Bitmap> result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute Called");

//        for (int i = 0; i < AnotherActivity.N; i++) {
//            images.add(result.get(i));
//        }
        Log.d("ImageDownloader", "bitmap"); // what does it mean?
//            ArrayAdapter<Bitmap> adapter = new ArrayAdapter<Bitmap>(SecondActivity.this, android.R.layout.simple_list_item_1, images);
//            lvMain.setAdapter(adapter);
//        simpleWaitDialog.dismiss();
    }

    private Bitmap downloadBitmap(String url) {
        Log.d(TAG, "start downloading");
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url + " ");
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            getRequest.abort();
            Log.e("ImageDownloader", "Something went wrong while" +
                    " retrieving bitmap from " + url + "\nError: " + e.toString());
        }
        return null;
    }
}