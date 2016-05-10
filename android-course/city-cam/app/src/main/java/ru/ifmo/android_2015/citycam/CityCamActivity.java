package ru.ifmo.android_2015.citycam;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import ru.ifmo.android_2015.citycam.model.City;
import ru.ifmo.android_2015.citycam.utils.DownloadUtils;
import ru.ifmo.android_2015.citycam.webcams.Webcams;

/**
 * Экран, показывающий веб-камеру одного выбранного города.
 * Выбранный город передается в extra параметрах.
 */
public class CityCamActivity extends AppCompatActivity {

    /**
     * Обязательный extra параметр - объект City, камеру которого надо показать.
     */
    public static final String EXTRA_CITY = "city";

    private ImageView camImageView;
    private ProgressBar progressBarView;

    private DownloadBitmapTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        City city = getIntent().getParcelableExtra(EXTRA_CITY);
        if (city == null) {
            Log.w(TAG, "City object not provided in extra parameter: " + EXTRA_CITY);
            finish();
        }

        setContentView(R.layout.activity_city_cam);
        camImageView = (ImageView) findViewById(R.id.cam_image);
        progressBarView = (ProgressBar) findViewById(R.id.progress);

        getSupportActionBar().setTitle(city.name);

        task = null;
        if (savedInstanceState != null) {
            task = (DownloadBitmapTask) getLastCustomNonConfigurationInstance();
        }
        if (task != null) {
            task.attachActivity(this);
        } else {
            task = new DownloadBitmapTask(this);
            task.execute(city);
        }
        // Здесь должен быть код, инициирующий асинхронную загрузку изображения с веб-камеры
        // в выбранном городе.
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return task;
    }

    private enum DownloadState {
        DOWNLOADING,
        DONE,
        ERROR
    }

    private static class DownloadBitmapTask extends AsyncTask<City, Void, Bitmap> {
        // Текущий объект Activity, храним для обновления отображения
        private CityCamActivity activity;

        // Текущее состояние загрузки
        private DownloadState state = DownloadState.DOWNLOADING;

        // Скачиваемая картинка
        private Bitmap bitmap = null;

        DownloadBitmapTask(CityCamActivity activity) {
            this.activity = activity;
        }

        /**
         * Этот метод вызывается, когда новый объект Activity подключается к
         * данному таску после смены конфигурации.
         *
         * @param activity новый объект Activity
         */
        void attachActivity(CityCamActivity activity) {
            Log.d(TAG, "attachActivity: state = " + state + ", [bitmap != null] = " + (bitmap != null));
            this.activity = activity;
            activity.updateView(state, bitmap);
        }

        /**
         * Вызывается в UI потоке из execute() до начала выполнения таска.
         */
        @Override
        protected void onPreExecute() {
            state = DownloadState.DOWNLOADING;
            activity.updateView(state, bitmap);
        }

        @Override
        protected Bitmap doInBackground(City... cities) {
            if (!haveInternetConnection()) {
//                Toast.makeText(activity.getApplicationContext(), "Check internet connection, please", Toast.LENGTH_SHORT).show();
                return null;
            }
            state = DownloadState.DOWNLOADING;
            try {
                bitmap = downloadBitmap(cities[0]);
            } catch (Exception e) {
                Log.e(TAG, "Error downloading file: " + e, e);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            this.state = bitmap != null ? DownloadState.DONE : DownloadState.ERROR;
            activity.updateView(state, bitmap);
        }

        private boolean haveInternetConnection() {
            ConnectivityManager manager = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
    }

    private void updateView(DownloadState state, Bitmap bitmap) {
        if (state == DownloadState.DONE) {
            assert bitmap != null;
            progressBarView.setVisibility(View.GONE);
            camImageView.setImageBitmap(bitmap);
        } else if (state == DownloadState.DOWNLOADING) {
            progressBarView.setVisibility(View.VISIBLE);
        } else if (state == DownloadState.ERROR) {
            // TODO: create fail.img
            Toast.makeText(getApplicationContext(), "Error while downloading image, check network connection and try again", Toast.LENGTH_SHORT).show();
        }
    }

    private static Bitmap downloadBitmap(City city) throws IOException {
        return DownloadUtils.downloadBitmap(Webcams.createNearbyUrl(city.latitude, city.longitude));
    }

    private static final String TAG = "CityCam";
}
