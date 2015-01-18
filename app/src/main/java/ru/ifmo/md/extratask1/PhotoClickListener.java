package ru.ifmo.md.extratask1;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by pinguinson on 17.01.2015.
 */
public class PhotoClickListener implements View.OnClickListener {
    String url;
    Context ctx;

    public PhotoClickListener(Context ctx, String url) {
        super();
        this.ctx = ctx;
        this.url = url;
    }

    @Override
    public void onClick(View view) {
        Intent viewIntent = new Intent(ctx, PhotoPreview.class);
        viewIntent.putExtra("url", url);
        ctx.startActivity(viewIntent);
    }
}
