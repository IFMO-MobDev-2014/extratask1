package ru.ifmo.md.extratask1.photoclient;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import ru.ifmo.md.extratask1.photoclient.database.ImagesProvider;
import ru.ifmo.md.extratask1.photoclient.database.ImagesTable;

/**
 * Created by sergey on 16.01.15.
 */
public class FullImageActivity extends ActionBarActivity {

    public static final String EXTRA_ROW_ID = "extra_row_id";

    private ImageView imageView;
    private Bitmap imageBitmap;
    private String imageURL;
    private String imageURLonWeb;
    private String title;
    private String authorName;
    private int rowId;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", "Loading of big photo complete");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(ImagesLoader.EXTRA_RESULT_CODE);
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("TAG", "Everything is OK");
                    showBigImage();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        imageView = (ImageView) findViewById(R.id.full_image_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rowId = getIntent().getIntExtra(EXTRA_ROW_ID, 1);
        showPictureByRowId(rowId);
    }

    private int getNextRowId() {
        int numRows = getNumberOfRows();
        if (rowId + 1 >= numRows)
            return 1;
        else
            return rowId + 1;
    }

    private int getNumberOfRows() {
        Cursor cursor = getContentResolver().query(
                ImagesProvider.CONTENT_URI,
                new String[] { ImagesTable.COLUMN_ID },
                null, null, null);
        int result = cursor.getCount();
        cursor.close();
        return result;
    }

    private void showPictureByRowId(int pictureRowId) {
        Cursor cursor = getContentResolver().query(
                ImagesProvider.CONTENT_URI,
                null,
                ImagesTable.COLUMN_ID + " = ?",
                new String[] {String.valueOf(pictureRowId)},
                null);
        cursor.moveToFirst();
        imageURLonWeb = cursor.getString(cursor.getColumnIndex(ImagesTable.COLUMN_LINK));
        imageURL = cursor.getString(cursor.getColumnIndex(ImagesTable.COLUMN_BIG_CONTENT_URI));
        title = cursor.getString(cursor.getColumnIndex(ImagesTable.COLUMN_TITLE));
        authorName = cursor.getString(cursor.getColumnIndex(ImagesTable.COLUMN_AUTHOR_NAME));
        cursor.close();
        if (!ImageFilesHandler.imageFileExists(getApplicationContext(), imageURL)) {
            ImagesLoader.startActionLoadBigPhoto(getApplicationContext(), imageURL);
        } else {
            showBigImage();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_set_wallpaper:
                setAsWallpaper();
                break;
            case R.id.action_open_in_browser:
                openInBrowser();
                break;
            case R.id.action_save_to_gallery:
                saveToGallery();
                break;
            case R.id.action_next_big_picture:
                rowId = getNextRowId();
                showPictureByRowId(rowId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAsWallpaper() {
        WallpaperManager manager = WallpaperManager.getInstance(this);
        try {
            manager.setBitmap(imageBitmap);
            Toast.makeText(this, "Wallpaper has been changed.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Couldn't set wallpaper.", Toast.LENGTH_LONG).show();
        }
    }

    private void openInBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageURLonWeb));
        startActivity(browserIntent);
    }

    private void saveToGallery() {
        String resultCode = MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, title, "Author: " + authorName);
        if (resultCode != null)
            Toast.makeText(this, "Image has been saved in gallery.", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Couldn't save image.", Toast.LENGTH_LONG).show();
    }

    private void showBigImage() {
        imageBitmap = ImageFilesHandler.loadImageFromStorage(getApplicationContext(), imageURL);
        imageView.setImageBitmap(imageBitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(ImagesLoader.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


}
