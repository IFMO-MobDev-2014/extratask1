package ru.ifmo.md.photooftheday;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * @author Vadim Semenov <semenov@rain.ifmo.ru>
 */
public abstract class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
    public static final String TAG = RecyclerAdapter.class.getSimpleName();

    protected List<Photo> dataset;

    public RecyclerAdapter(List<Photo> dataset) {
        this.dataset = dataset;
    }

    public class Holder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.small_image);
        }
    }

    public void add(int position, Photo photo) {
        dataset.add(position, photo);
        notifyItemInserted(position);
    }

    public void add(Photo photo) {
        add(dataset.size(), photo);
    }

    public void remove(int position) {
        dataset.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(Photo photo) {
        int position = dataset.indexOf(photo);
        if (position == -1) {
            Log.e(TAG, "There is no such element in dataset");
            throw new IllegalArgumentException("There is no such element in dataset");
        }
        remove(position);
    }

    public void clear() {
        dataset.clear();
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout, parent, false);
        return new Holder(view);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
