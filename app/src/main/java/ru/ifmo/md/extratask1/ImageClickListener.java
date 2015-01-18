package ru.ifmo.md.extratask1;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class ImageClickListener implements View.OnClickListener {
    Image image;
    Context ctx;

    public ImageClickListener(Context ctx, Image image) {
        super();
        this.ctx = ctx;
        this.image = image;
    }

    @Override
    public void onClick(View view) {
        Intent viewIntent = new Intent(ctx, ImagePreview.class);
        viewIntent.putExtra("url", image.url);
        viewIntent.putExtra("title", image.title);
        ctx.startActivity(viewIntent);
    }
}
