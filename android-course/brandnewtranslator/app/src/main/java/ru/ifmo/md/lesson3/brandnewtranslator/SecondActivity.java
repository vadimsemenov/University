package ru.ifmo.md.lesson3.brandnewtranslator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SecondActivity extends Activity {

    private TextView text;
    private ProgressDialog simpleWaitDialog;
    public static int N = 10;
    public String word;
    String[] urls = new String[N];
    List<Bitmap> images = new ArrayList<Bitmap>();
    public ListView lvMain;
    int cur = 0;
    private ImageView d;
    private ImageView d1;
    private ImageView d2;
    private ImageView d3;
    private ImageView d4;
    private ImageView d5;
    private ImageView d6;
    private ImageView d7;
    private ImageView d8;
    private ImageView d9;
    private ImageView d10;
    Button btn;
    Intent sAct;
    //    ImageView d2 = (ImageView) findViewById(R.id.imageView1);
    ImageView selectedImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.images);
        word = getIntent().getStringExtra("word");
//        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        new RequestTask().execute("http://yandex.ru/images/search?text=" + word + "&isize=eq&iw=50&ih=50");
//        ArrayAdapter<Bitmap> adapter = new ArrayAdapter<Bitmap>(this, android.R.layout.simple_list_item_1, images);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, as);
//        lvMain.setAdapter(adapter);
        btn = (Button) findViewById(R.id.button);
        sAct = new Intent(SecondActivity.this, MainActivity.class);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(sAct);
            }
        });
    }

    class RequestTask extends AsyncTask<String, Integer, List<Bitmap>> {

        @Override
        protected List<Bitmap> doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            List<Bitmap> bm = new ArrayList<Bitmap>();
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                    int prev = 0;
                    int i = 0;
                    while (i < N) {
                        int pos2 = responseString.indexOf(".jpg", prev);
                        int pos = responseString.indexOf("http://", pos2 - 100);
                        if (pos < pos2) {
                            urls[i] = responseString.substring(pos, pos2 + 4);
                            bm.add(downloadBitmap(urls[i]));
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
            } catch (IOException e) {
            }
            return bm;
        }

        @Override
        protected void onPreExecute() {
            Log.i("Async-Example", "onPreExecute Called");
            simpleWaitDialog = ProgressDialog.show(SecondActivity.this,
                    "Wait", "Downloading Image");
        }
        @Override
        protected void onPostExecute(List<Bitmap> result) {
            super.onPostExecute(result);
            Log.i("Async-Example", "onPostExecute Called");
            for (int i = 0; i < N; i++) {
                images.add(result.get(i));
            }
            Log.e("ImageDownloader", "bitmap");
            d = (ImageView) findViewById(R.id.imageView);
            d2 = (ImageView) findViewById(R.id.imageView2);
            d3 = (ImageView) findViewById(R.id.imageView3);
            d4 = (ImageView) findViewById(R.id.imageView4);
            d5 = (ImageView) findViewById(R.id.imageView5);
            d6 = (ImageView) findViewById(R.id.imageView6);
            d7 = (ImageView) findViewById(R.id.imageView7);
            d8 = (ImageView) findViewById(R.id.imageView8);
            d9 = (ImageView) findViewById(R.id.imageView9);
            d10 = (ImageView) findViewById(R.id.imageView10);

            d.setImageBitmap(images.get(0));
            d2.setImageBitmap(images.get(1));
            d3.setImageBitmap(images.get(2));
            d4.setImageBitmap(images.get(3));
            d5.setImageBitmap(images.get(4));
            d6.setImageBitmap(images.get(5));
            d7.setImageBitmap(images.get(6));
            d8.setImageBitmap(images.get(7));
            d9.setImageBitmap(images.get(8));
            d10.setImageBitmap(images.get(9));
//            ArrayAdapter<Bitmap> adapter = new ArrayAdapter<Bitmap>(SecondActivity.this, android.R.layout.simple_list_item_1, images);
//            lvMain.setAdapter(adapter);
            simpleWaitDialog.dismiss();
        }

        private Bitmap downloadBitmap(String url) {
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
                        " retrieving bitmap from " + url + e.toString());
            }
            return null;
        }
    }
}