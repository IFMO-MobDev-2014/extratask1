package ru.ifmo.zakharvoit.extratask1.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ru.ifmo.zakharvoit.extratask1.R;
import ru.ifmo.zakharvoit.extratask1.images.Image;
import ru.ifmo.zakharvoit.extratask1.images.ImagesDownloadService;
import ru.ifmo.zakharvoit.extratask1.images.ImagesResultReceiver;
import ru.ifmo.zakharvoit.extratask1.provider.picture.PictureContentValues;
import ru.ifmo.zakharvoit.extratask1.provider.picture.PictureSelection;

public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = (ViewPager) findViewById(R.id.images_pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager(), 100));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reload) {
            ImagesResultReceiver receiver = new ImagesResultReceiver();
            receiver.setReceiver(createReceiver(this));

            Intent intent = new Intent(this, ImagesDownloadService.class);
            intent.putExtra(ImagesDownloadService.RESULT_RECEIVER_EXTRA_KEY, receiver);
            startService(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private ImagesResultReceiver.Receiver createReceiver(final Context context) {
        return new ImagesResultReceiver.Receiver() {
            public static final String TAG = "ResultReceiver";

            private Image[] images;
            private int currentSize;
            private ProgressDialog dialog;

            @Override
            public void onListDownload(int size) {
                Log.d(TAG, "List downloaded");
                images = new Image[size];
                currentSize = 0;
                dialog = new ProgressDialog(context);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setIndeterminate(false);
                dialog.setCancelable(false);
                dialog.setProgress(0);
                dialog.setMessage("Downloading...");
                dialog.show();
            }

            @Override
            public void onImageDownload(Image image) {
                Log.d(TAG, "New image downloaded");
                images[currentSize++] = image;
                dialog.setProgress(100 * currentSize / images.length);
            }

            @Override
            public void onFinishDownload() {
                Log.d(TAG, "Finished downloading");
                dialog.setProgress(100);
                new PictureSelection().delete(getContentResolver());
                for (int i = 0; i < currentSize; i++) {
                    Image image = images[i];
                    PictureContentValues contentValues = new PictureContentValues();
                    contentValues.putTitle(image.getTitle());
                    contentValues.putContents(image.getContents());
                    contentValues.putLargeLink(image.getLargeLink());
                    contentValues.putMyId(i);
                    contentValues.insert(getContentResolver());
                }
                images = null;
                try {
                    dialog.dismiss();
                } catch (Exception ignore) {
                    
                }
            }

            @Override
            public void onError() {
                Toast.makeText(context, "Error during loading images", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "An error happened");
                images = null;
            }
        };
    }
}
