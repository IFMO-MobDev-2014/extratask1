package mariashka.editors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by mariashka on 1/17/15.
 */
public class FullScreenFragment extends Fragment {
    private byte[] bytes;
    private Bitmap bitmap;

    public static FullScreenFragment newInstance(String title, byte[] bytes) {
        FullScreenFragment fragment = new FullScreenFragment();
        Bundle args = new Bundle();
        args.putByteArray("img", bytes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bytes = getArguments().getByteArray("img");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fullscreen, container, false);
        ImageView img = (ImageView) view.findViewById(R.id.imageView);
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        img.setImageBitmap(bitmap);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bitmap != null)
            bitmap.recycle();
    }

}
