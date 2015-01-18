package daria.extratask;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by daria on 18.01.15.
 */
public class PhotoViewHolder extends RecyclerView.ViewHolder {
    protected ImageView image;

    public PhotoViewHolder(View view) {
        super(view);
        image = (ImageView) view.findViewById(R.id.photo);
    }
}
