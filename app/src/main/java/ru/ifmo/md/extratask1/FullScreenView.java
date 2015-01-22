package ru.ifmo.md.extratask1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class FullScreenView extends Activity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (ImageView) findViewById(R.id.imageView2);

        int index = getIntent().getIntExtra("index", 0);
        imageView.setImageBitmap(GridActivity.images.get(index));
    }
}
