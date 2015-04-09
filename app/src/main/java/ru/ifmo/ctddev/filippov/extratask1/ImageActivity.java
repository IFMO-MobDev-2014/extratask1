package ru.ifmo.ctddev.filippov.extratask1;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayInputStream;

import ru.ifmo.ctddev.filippov.extratask1.util.SystemUiHider;

/**
 * Created by Dima_2 on 02.03.2015.
 */
public class ImageActivity extends Activity implements View.OnTouchListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private boolean TOGGLE_ON_CLICK = true;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private SystemUiHider systemUiHider;

    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF middle = new PointF();
    private float oldDistance = 1.0f;
    private int mode = 0;

    private ProgressBar progressBar;
    private ImageView imageView;
    private String photoId;
    private String browseUrl;
    private int databaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        photoId = getIntent().getStringExtra("id");
        databaseId = getIntent().getIntExtra("databaseId", 0);
        browseUrl = getIntent().getStringExtra("browse");
        this.setTitle(getIntent().getStringExtra("title"));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);


        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressBar.getVisibility() == View.INVISIBLE) {
                    Intent servIntent = new Intent(getApplicationContext(), MyIntentService.class);
                    servIntent.putExtra("wallpaper", true);
                    servIntent.putExtra("databaseId", databaseId);
                    startService(servIntent);
                }
            }
        };


        View.OnClickListener browseListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(browseUrl));
                startActivity(intent);
            }
        };

        View.OnClickListener saveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressBar.getVisibility() == View.INVISIBLE) {
                    Intent servIntent = new Intent(getApplicationContext(), MyIntentService.class);
                    servIntent.putExtra("wallpaper", true);
                    servIntent.putExtra("save", true);
                    servIntent.putExtra("databaseId", databaseId);
                    startService(servIntent);

                }
            }
        };

        systemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        systemUiHider.setup();
        systemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    int controlsHeight;
                    int shortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            if (controlsHeight == 0) {
                                controlsHeight = controlsView.getHeight();
                            }
                            if (shortAnimTime == 0) {
                                shortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : controlsHeight)
                                    .setDuration(shortAnimTime);
                        } else {
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible) {
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TOGGLE_ON_CLICK) {
                    TOGGLE_ON_CLICK = false;
                    systemUiHider.show();
                } else {
                    TOGGLE_ON_CLICK = true;
                    systemUiHider.hide();
                }
            }
        });

        findViewById(R.id.button_wallpaper).setOnClickListener(clickListener);
        findViewById(R.id.button_browse).setOnClickListener(browseListener);
        findViewById(R.id.button_save).setOnClickListener(saveListener);
        imageView = (ImageView) contentView;
        contentView.setOnTouchListener(this);
        getLoaderManager().initLoader(1, null, this);
        loadPhoto();
    }

    void loadPhoto() {
        if (checkInternetConnection()) {
            Intent serviceIntent = new Intent(this, MyIntentService.class);
            serviceIntent.putExtra("id", photoId);
            serviceIntent.putExtra("databaseId", databaseId);
            startService(serviceIntent);
        }
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, R.string.check_your_connection, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    Handler hideHandler = new Handler();
    Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            systemUiHider.hide();
        }
    };

    private void delayedHide(int delayMillis) {
        hideHandler.removeCallbacks(hideRunnable);
        hideHandler.postDelayed(hideRunnable, delayMillis);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ContentUris.withAppendedId(Provider.PHOTOS_CONTENT_URI, databaseId);
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            String id = cursor.getString(2);
            byte[] image = cursor.getBlob(5);
            if (image != null) {
                ByteArrayInputStream imageStream = new ByteArrayInputStream(image);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                RectF drawableRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
                RectF viewRect = new RectF(0, 0, imageView.getWidth(), imageView.getHeight());
                matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                imageView.setImageMatrix(matrix);
                imageView.setImageBitmap(bitmap);
                imageView.invalidate();
                progressBar.setVisibility(View.INVISIBLE);

                MyPhoto nowPhoto = new MyPhoto(id, cursor.getString(1), cursor.getBlob(4));
                nowPhoto.databaseId = cursor.getInt(0);
                nowPhoto.fullUrl = cursor.getString(3);
                browseUrl = nowPhoto.browseUrl = cursor.getString(8);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDistance = distance(event);
                if (oldDistance > 10.0f) {
                    savedMatrix.set(matrix);
                    setMiddlePoint(middle, event);
                    mode = 1;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == 1) {
                    float newDistance = distance(event);
                    if (newDistance > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDistance / oldDistance;
                        matrix.postScale(scale, scale, middle.x, middle.y);
                    }
                }
                break;
        }
        view.setImageMatrix(matrix);
        return true;
    }


    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void setMiddlePoint(PointF point, MotionEvent event) {
        point.set((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2);
    }
}
