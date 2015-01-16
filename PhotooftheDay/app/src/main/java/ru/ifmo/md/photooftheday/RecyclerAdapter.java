package ru.ifmo.md.photooftheday;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by vadim on 16/01/15.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {
    private static final String TAG = "photooftheday.RecyclerAdapter";
    private List<Bitmap> dataset;

    public RecyclerAdapter(List<Bitmap> dataset) {
        this.dataset = dataset;
    }

    public class Holder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.small_image);
        }
    }

    public void add(int position, Bitmap bitmap) {
        dataset.add(position, bitmap);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        dataset.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(Bitmap bitmap) {
        int position = dataset.indexOf(bitmap);
        if (position == -1) {
            Log.d(TAG, "There is no such element in dataset");
            throw new IllegalArgumentException("There is no such element in dataset");
        }
        remove(position);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Bitmap bitmap = dataset.get(position);
        holder.imageView.setImageBitmap(bitmap);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("OnClickListener", "click");
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
