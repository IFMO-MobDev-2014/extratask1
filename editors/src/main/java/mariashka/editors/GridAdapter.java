package mariashka.editors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        ImageViewRecyclable imageView = (convertView == null) ? new ImageViewRecyclable(context) : (ImageViewRecyclable) convertView;
        PhotoItem item = data.get(position);
        byte[] bytes = item.smallImg;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);

        return imageView;
    }

    public class ImageViewRecyclable extends ImageView
    {
        private Bitmap bitmap;

        public ImageViewRecyclable(Context context)
        {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            int width = getMeasuredWidth();
            setMeasuredDimension(width, width);
        }


        @Override
        public void setImageBitmap(Bitmap bm)
        {
            super.setImageBitmap(bm);
            if (bitmap != null)
                bitmap.recycle();
            this.bitmap = bm;
        }
    }

    static class ViewHolder {
        ImageViewRecyclable image;
    }
}
