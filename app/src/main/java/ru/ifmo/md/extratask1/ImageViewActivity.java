package ru.ifmo.md.extratask1;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageViewActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<ArrayList<MyImage>> {
    private static final int IMAGES_LOADER_ID = 1;
    public static final String APP_PREFERENCES_POSITION = "position";
    SharedPreferences settings;
    ArrayList<MyImage> list;
    ImageView imageView;
    int position;
    TextView textView1;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_image_view);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.contains(APP_PREFERENCES_POSITION)) {
            position = settings.getInt(APP_PREFERENCES_POSITION, 0);
        } else position = 0;

        imageView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                position++;
                if (position >= list.size()) position -= list.size();
                update();
            }
            public void onSwipeRight() {
                position--;
                if (position < 0) position += list.size();
                update();
            }
        });

        getLoaderManager().initLoader(IMAGES_LOADER_ID, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(APP_PREFERENCES_POSITION, position);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (settings.contains(APP_PREFERENCES_POSITION)) {
            position = settings.getInt(APP_PREFERENCES_POSITION, 0);
        } else position = 0;
        update();
    }

    private void update() {
        if (list != null && position >= 0 && position < list.size()) {
            imageView.setImageBitmap(list.get(position).picture);
            textView1.setText(list.get(position).pictureName);
            textView2.setText("by " + list.get(position).username);
        }
    }

    public void onClickPrev(View view) {
        position--;
        if (position < 0) position += list.size();
        update();
    }

    public void onClickNext(View view) {
        position++;
        if (position >= list.size()) position -= list.size();
        update();
    }

    public Loader<ArrayList<MyImage>> onCreateLoader(int i, Bundle bundle) {
        return new MyImagesListLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MyImage>> listLoader, final ArrayList<MyImage> list) {
        this.list = list;
        update();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MyImage>> listLoader) {
        new MyImagesListLoader(this);
    }
}
