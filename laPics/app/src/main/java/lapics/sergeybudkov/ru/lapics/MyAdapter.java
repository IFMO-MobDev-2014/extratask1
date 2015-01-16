package lapics.sergeybudkov.ru.lapics;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import java.util.List;


public class MyAdapter extends ArrayAdapter<Bitmap> {
    private Context context;

    public MyAdapter(Context context, int textViewResourceId, List<Bitmap> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view = new ImageView(getContext());
        view.setImageBitmap(getItem(position));
        return view;
    }
}