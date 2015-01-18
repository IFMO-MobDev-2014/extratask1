package daria.extratask;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by daria on 18.01.15.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    private List<Bitmap> photoList;

    public PhotoAdapter(List<Bitmap> photoList) {
        this.photoList = photoList;
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder photoViewHolder, int i) {
        Bitmap temp = photoList.get(i);
        photoViewHolder.image.setImageBitmap(temp);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item, viewGroup, false);
        return new PhotoViewHolder(item);
    }
}
