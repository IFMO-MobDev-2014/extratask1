package year2013.ifmo.photogallery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Image> images;
    private ArrayList<Image> onPage;

    public ImageAdapter(Context context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
        setPage(0);
    }

    public boolean canSet(int p) {
        return images.size() > p * 6;
    }

    public void setPage(int p) {
        onPage = new ArrayList<>();
        int n = p * 6;
        int to = Math.min(images.size(), n + 6);
        for (int i = n; i < to; i++) {
            onPage.add(images.get(i));
        }
        notifyDataSetChanged();
    }

    public void addImage(Image image) {
        images.add(image);
        if (onPage.size() < 6) {
            onPage.add(image);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        images.clear();
        onPage.clear();
        setPage(0);
    }

    @Override
    public int getCount() {
        return onPage.size();
    }

    @Override
    public Image getItem(int position) {
        return onPage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 10, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(onPage.get(position).bitmap);
        return imageView;
    }
}