package ru.ifmo.md.photooftheday;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class DisplayPhotoActivity extends Activity {
// TODO: update to viewPager
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        imageView = (ImageView) findViewById(R.id.full_image_view);
        Photo photo = getIntent().getParcelableExtra(MainActivity.PHOTO);
        imageView.setImageBitmap(photo.getFullBitmap());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO: save to storage, change wallpaper, open in browser

        return super.onOptionsItemSelected(item);
    }
}
