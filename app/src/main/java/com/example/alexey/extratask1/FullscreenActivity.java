package com.example.alexey.extratask1;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


public class FullscreenActivity extends Activity {
    int pos;
    ImageView imageView;
    protected void onCreate(final Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        final View contentView = findViewById(R.id.imageView3);
        imageView = (ImageView) findViewById(R.id.imageView3);
        pos = getIntent().getIntExtra("img", 1);
        set();
        Toast toast = Toast.makeText(getApplicationContext(),
                "Click to next, long click to close.", Toast.LENGTH_SHORT);
        toast.show();
        toast.show();
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos++;
                pos %= IServise.IMGCOUNT;
                set();
            }
        });
        contentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FullscreenActivity.this.finish();
                return false;
            }
        });
    }

    private void set() {
        Cursor cursor = getContentResolver().query(provider.CONTENT_URI, null, provider.DAY + " = " + Integer.toString(pos), null, null);
        cursor.moveToFirst();
        Bitmap bitmap = ImageConverter.getImage(cursor.getBlob(cursor.getColumnIndex(provider.DATE)));
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
