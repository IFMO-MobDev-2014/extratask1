package me.loskutov.popularphotosviewer;

/**
* Created by ignat on 12.01.15.
*/
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageViewHolder extends RecyclerView.ViewHolder {
    public final ImageView imageView;
    public final ProgressBar progressBar;
    public ImageViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
    }
}