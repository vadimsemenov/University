package ru.ifmo.md.lesson3.brandnewtranslator;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by vadim on 28/09/14.
 */
public class AnotherActivity extends Activity /*extends ListActivity*/ {
    private static final String TAG = "AnotherActivity";

    public static Resources RESOURCES;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);
        RESOURCES = getResources();

        Log.d(TAG, "onCreate() started");

        Intent intent = getIntent();
        final String word = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        final AnotherActivity context = this; // TODO: is it best solution you can find?
        final ListView listView = (ListView) findViewById(R.id.imageListView);


        final ArrayAdapter<Bitmap> adapter = new ArrayAdapter<Bitmap>(context, android.R.layout.simple_list_item_1, R.id.listViewItem) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Log.d("Adapter", "works!");
                ImageView imageView;
                if (convertView == null) {
                    imageView = new ImageView(context);
                    //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //imageView.setLayoutParams(params);
                } else {
                    imageView = (ImageView) convertView;
                }
                Bitmap bitmap = getItem(position);
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loading);
                }
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setMinimumHeight(bitmap.getHeight());
                imageView.setMinimumWidth(bitmap.getWidth());
                imageView.setImageBitmap(bitmap);
                return imageView;
            }
        };
        listView.setAdapter(adapter);
        final TextView textView = (TextView) findViewById(R.id.translatedTextView);

        new AsyncTranslator() {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d(super.TAG, "onPostExecute() works: " + s);
                textView.setText(s.toCharArray(), 0, s.length());
                textView.invalidate();
            }
        }.execute(word);

        new AsyncImageLoader() {
            @Override
            protected void onProgressUpdate(Bitmap... values) {
                super.onProgressUpdate(values[0]);
                Log.d(super.TAG, "onProgressUpdate() works");
                adapter.add(values[0]);
                adapter.notifyDataSetChanged();
            }
        }.execute(word);
    }
}
