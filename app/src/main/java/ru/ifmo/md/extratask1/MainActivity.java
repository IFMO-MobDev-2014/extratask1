package ru.ifmo.md.extratask1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Svet on 15.01.2015.
 */
public class MainActivity extends Activity{
    MyBroadcastReceiver receiver;
    ArrayList<ImageContainer> containers = null;
    GridView table;
    ImageAdapter adapter;
    ImageView fullscreen;
    int currentImage = -1;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.main_layout);

        Cursor cursor = getContentResolver().query(MyContentProvider.IMAGE_CONTENT_URI, null, null, null, null);
        startManagingCursor(cursor);
        fullscreen = (ImageView) findViewById(R.id.fullscreen);

        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        fullscreen.setOnTouchListener(gestureListener);



        table = (GridView) findViewById(R.id.grid_view);

        IntentFilter filter = new IntentFilter(MyBroadcastReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyBroadcastReceiver();
        receiver.setMainActivityHandler(this);
        registerReceiver(receiver, filter);

        if(cursor.getCount() == 0) {
            Intent intent = new Intent(MainActivity.this, MyImageLoadService.class);
            intent.putExtra(MyImageLoadService.REQUEST_TYPE, 2);
            intent.putExtra(MyImageLoadService.REQUEST_ADDRESS, "http://api-fotki.yandex.ru/api/top/?format=json");
            startService(intent);
        } else {
            containers = new ArrayList<ImageContainer>();
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                String urlL = cursor.getString(cursor.getColumnIndex(MyContentProvider.URLL));
                String urlXXXL = cursor.getString(cursor.getColumnIndex(MyContentProvider.URLXXXL));
                String title = cursor.getString(cursor.getColumnIndex(MyContentProvider.TITLE));
                String author = cursor.getString(cursor.getColumnIndex(MyContentProvider.AUTHOR));
                String addressL = cursor.getString(cursor.getColumnIndex(MyContentProvider.ADDRESSL));
                String addressXXXL = cursor.getString(cursor.getColumnIndex(MyContentProvider.ADDRESSXXXL));

                View v = LayoutInflater.from(this).inflate(R.layout.image_item, null);
                ImageView imageView = (ImageView) v.findViewById(R.id.image_view);
                imageView.setImageDrawable(this.getResources().getDrawable(R.drawable.by_default));
                ProgressBar progress = (ProgressBar) v.findViewById(R.id.progress);
                ImageContainer cur = new ImageContainer(urlL, urlXXXL, title, author, addressL, addressXXXL, v, imageView, progress);
                cur.hideProgressBar();
                containers.add(cur);
                cursor.moveToNext();
            }

            adapter = new ImageAdapter(containers);
            table.setAdapter(adapter);
            setListenerToTable();

            for(ImageContainer container : containers) {
                container.setImage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.update_button : {
                performUpdate();
                break;
            }
        }
        return  true;
    }

    public void performUpdate() {
        clearDatabase();
        for(ImageContainer container : containers) {
            container.showProgressBar();
        }
        Intent intent = new Intent(MainActivity.this, MyImageLoadService.class);
        intent.putExtra(MyImageLoadService.REQUEST_TYPE, 2);
        intent.putExtra(MyImageLoadService.REQUEST_ADDRESS, "http://api-fotki.yandex.ru/api/top/?format=json");
        startService(intent);
    }

    private void clearDatabase() {
        int count = getContentResolver().delete(MyContentProvider.IMAGE_CONTENT_URI, null, null);
    }

    public void onBackPressed() {
        if(fullscreen.getVisibility() == View.VISIBLE) {
            fullscreen.setVisibility(View.GONE);
            table.setEnabled(true);
        } else {
            finish();
        }
    }

    public void setURLs(String answer) {
        containers = JSONParser.getImagesData(answer);

        adapter = new ImageAdapter(containers);
        table.setAdapter(adapter);
        setListenerToTable();

        for(int i = 0; i < containers.size(); i++) {
            putDataInTable(containers.get(i));

            View v = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView imageView = (ImageView) v.findViewById(R.id.image_view);
            imageView.setImageDrawable(this.getResources().getDrawable(R.drawable.by_default));
            ProgressBar progress = (ProgressBar) v.findViewById(R.id.progress);
            containers.get(i).mainView = v;
            containers.get(i).imageView = imageView;
            containers.get(i).progressBar = progress;

            Intent requestIntent = new Intent(MainActivity.this, MyImageLoadService.class);
            requestIntent.putExtra(MyImageLoadService.REQUEST_TYPE, 1);
            requestIntent.putExtra(MyImageLoadService.REQUEST_ADDRESS, containers.get(i).urlL);
            requestIntent.putExtra(MyImageLoadService.IMAGE_INDEX, i);
            requestIntent.putExtra(MyImageLoadService.IMAGE_NAME, "test" + i);
            startService(requestIntent);
        }
    }

    private void setListenerToTable() {
        table.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            changePicture(position);
            fullscreen.setVisibility(View.VISIBLE);
            currentImage = position;
            table.setEnabled(false);
            }
        });
    }

    private void changePicture(int position) {
        if(currentImage < JSONParser.COUNT) {
            ImageContainer cur = ((ImageContainer) adapter.getItem(position));
            if(!cur.addressXXXL.isEmpty()) {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inSampleSize = 4;
                Bitmap b = BitmapFactory.decodeFile(cur.addressXXXL, o);
                fullscreen.setImageBitmap(b);
            } else {
                fullscreen.setImageDrawable(cur.imageView.getDrawable());
                Intent intent = new Intent(MainActivity.this, MyImageLoadService.class);
                intent.putExtra(MyImageLoadService.REQUEST_TYPE, 3);
                intent.putExtra(MyImageLoadService.REQUEST_ADDRESS, cur.urlXXXL);
                intent.putExtra(MyImageLoadService.IMAGE_NAME, "huge" + position);
                intent.putExtra(MyImageLoadService.IMAGE_INDEX, position);
                startService(intent);
            }
        }
    }

    public void uploadHugeImage(int index, String address) {
        containers.get(index).addressXXXL = address;
        if(fullscreen.getVisibility() == View.VISIBLE && index == currentImage) {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inSampleSize = 4;
            Bitmap b = BitmapFactory.decodeFile(address, o);
            fullscreen.setImageBitmap(b);
        }
        ContentValues values = new ContentValues();
        values.put(MyContentProvider.ADDRESSXXXL, address);
        getContentResolver().update(MyContentProvider.IMAGE_CONTENT_URI, values, MyContentProvider.TITLE + " = ? ", new String[]{containers.get(index).title});
    }

    private void putDataInTable(ImageContainer ic) {
        ContentValues values = new ContentValues();

        values.put(MyContentProvider.TITLE, ic.title);
        values.put(MyContentProvider.URLL, ic.urlL);
        values.put(MyContentProvider.URLXXXL, ic.urlXXXL);
        values.put(MyContentProvider.AUTHOR, ic.author);
        values.put(MyContentProvider.ADDRESSL, ic.addressL);
        values.put(MyContentProvider.ADDRESSXXXL, ic.addressXXXL);
        Uri resultUri = getContentResolver().insert(MyContentProvider.IMAGE_CONTENT_URI, values);
    }

    private void updateImageInformation(ContentValues values, String title) {
        getContentResolver().update(MyContentProvider.IMAGE_CONTENT_URI, values, MyContentProvider.TITLE + " = ?", new String[]{title});
    }

    public void hideProgressBar(int index, String adrSmall) {
        ImageContainer container = (ImageContainer)adapter.getItem(index);
        ((ImageContainer)adapter.getItem(index)).hideProgressBar();
        ((ImageContainer)adapter.getItem(index)).addressL = adrSmall;
        ((ImageContainer)adapter.getItem(index)).setImage();
        ContentValues values = new ContentValues();
        values.put(MyContentProvider.ADDRESSL, adrSmall);
        updateImageInformation(values, container.title);
    }

    public void increaseProgressBar(int index, int diff) {
        ((ImageContainer)adapter.getItem(index)).increaseProgressBar(diff);
    }

    public void setImageSize(int index, int size) {
        ((ImageContainer)adapter.getItem(index)).setImageSize(size);
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    currentImage = (currentImage + 1) % JSONParser.COUNT;
                    changePicture(currentImage);
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    currentImage--;
                    if(currentImage < 0) currentImage = JSONParser.COUNT - 1;
                    changePicture(currentImage);
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

}
