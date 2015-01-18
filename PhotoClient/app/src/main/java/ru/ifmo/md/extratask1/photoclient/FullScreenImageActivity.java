package ru.ifmo.md.extratask1.photoclient;

import android.app.ProgressDialog;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ru.ifmo.md.extratask1.photoclient.database.ImagesProvider;
import ru.ifmo.md.extratask1.photoclient.database.ImagesTable;

/**
 * Created by sergey on 16.01.15.
 */
public class FullScreenImageActivity extends ActionBarActivity {

    public static final String EXTRA_ROW_ID = "extra_row_id";

    private ImageView imageView;
    private TextView tvTitle;

    private Bitmap imageBitmap;
    private String imageURL;
    private String imageURLonWeb;
    private String title;
    private String authorName;
    private int rowId;
    private GestureDetector gestureDetector;
    private ProgressDialog progressDialog;

    private BroadcastReceiver mDownloadStateReceiver = new BroadcastStateReceiver();

    private class BroadcastStateReceiver extends BroadcastReceiver {

        public BroadcastStateReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            final int stateCode = intent.getIntExtra(BroadcastStateSender.EXTRA_STATE_CODE, BroadcastStateSender.STATE_COMPLETE);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            switch (stateCode) {
                case BroadcastStateSender.STATE_COMPLETE:
                    showBigImage();
                    break;
                case BroadcastStateSender.STATE_NO_CONNECTION:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                    break;
                case BroadcastStateSender.STATE_ERROR:
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_update), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        imageView = (ImageView) findViewById(R.id.full_image_view);
        tvTitle = (TextView) findViewById(R.id.tv_image_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rowId = getIntent().getIntExtra(EXTRA_ROW_ID, 1);
        gestureDetector = initGestureDetector();
        View view = findViewById(R.id.layout_full_image);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View ignore) {
            }
        });
        showPictureByRowId(rowId);
    }

    private GestureDetector initGestureDetector() {
        return new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            private MySwipeDetector detector = new MySwipeDetector();

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    if (detector.isSwipeDown(e1, e2, velocityY)) {
                        return false;
                    } else if (detector.isSwipeUp(e1, e2, velocityY)) {
                        //Close activity and show gallery
                        finish();
                    } else if (detector.isSwipeLeft(e1, e2, velocityX)) {
                        int numRows = getNumberOfRows();
                        rowId = rowId + 1;
                        if (rowId >= numRows) {
                            rowId = numRows - 1;
                            return false;
                        }
                        showPictureByRowId(rowId);
                    } else if (detector.isSwipeRight(e1, e2, velocityX)) {
                        rowId = rowId - 1;
                        if (rowId <= 0) {
                            rowId = 1;
                            return false;
                        }
                        showPictureByRowId(rowId);
                    }
                } catch (Exception e) {
                }
                return false;
            }
        });
    }

    private int getNumberOfRows() {
        Cursor cursor = getContentResolver().query(
                ImagesProvider.CONTENT_URI,
                new String[]{ImagesTable.COLUMN_ID},
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
                new String[]{String.valueOf(pictureRowId)},
                null);
        cursor.moveToFirst();
        imageURLonWeb = cursor.getString(cursor.getColumnIndex(ImagesTable.COLUMN_LINK));
        imageURL = cursor.getString(cursor.getColumnIndex(ImagesTable.COLUMN_BIG_CONTENT_URI));
        title = cursor.getString(cursor.getColumnIndex(ImagesTable.COLUMN_TITLE));
        authorName = cursor.getString(cursor.getColumnIndex(ImagesTable.COLUMN_AUTHOR_NAME));
        cursor.close();
        if (!ImageFilesHandler.imageFileExists(getApplicationContext(), imageURL)) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Downloading image...");
            progressDialog.setProgressStyle(android.R.style.Widget_Holo_ProgressBar_Horizontal);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            ImagesLoader.startActionLoadBigPhoto(getApplicationContext(), imageURL);
        } else {
            showBigImage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDownloadStateReceiver = new BroadcastStateReceiver();
        IntentFilter intentFilter = new IntentFilter(BroadcastStateSender.BROADCAST_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mDownloadStateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDownloadStateReceiver != null) {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mDownloadStateReceiver);
            mDownloadStateReceiver = null;
        }
    }

    // dismiss dialog if activity is destroyed
    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        super.onDestroy();
    }

    private void showBigImage() {
        imageBitmap = ImageFilesHandler.loadImageFromStorage(getApplicationContext(), imageURL);
        imageView.setImageBitmap(imageBitmap);
        tvTitle.setText(title);
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
                actionSetAsWallpaper();
                break;
            case R.id.action_open_in_browser:
                actionOpenInBrowser();
                break;
            case R.id.action_save_to_gallery:
                actionSaveToGallery();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actionSetAsWallpaper() {
        try {
            WallpaperManager manager = WallpaperManager.getInstance(this);
            manager.setBitmap(imageBitmap);
            Toast.makeText(this, "Wallpaper has been changed.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Couldn't set wallpaper.", Toast.LENGTH_LONG).show();
        }
    }

    private void actionOpenInBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageURLonWeb));
        startActivity(browserIntent);
    }

    private void actionSaveToGallery() {
        String resultCode = MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, title, "Author: " + authorName);
        if (resultCode != null)
            Toast.makeText(this, "Image has been saved in gallery.", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Couldn't save image.", Toast.LENGTH_LONG).show();
    }

}
