package mariashka.editors;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mariashka on 1/17/15.
 */
public class GridAdapter extends ArrayAdapter<PhotoItem> {

    private Context context;
    private int layoutResourceId;
    private List<PhotoItem> data = new ArrayList<>();

    public GridAdapter(Context context, int layoutResourceId, List<PhotoItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        PhotoItem item = data.get(position);
        byte[] array = item.smallImg;
        Log.d("arrayLength", "" + array.length);
        holder.image.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
        return row;
    }

    static class ViewHolder {
        ImageView image;
    }
}
