package ru.ifmo.md.extratask1;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageFullscreenFragment extends Fragment {

    private static final String ARGUMENT_BITMAP = "argument_bitmap";
    private static final String ARGUMENT_TITLE = "argument_title";
    private static final String ARGUMENT_AUTHOR = "arg_author";


    Bitmap bitmap;
    String title;
    String author;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, null);
        ((ImageView)view.findViewById(R.id.fullscreenImageView)).setImageBitmap(bitmap);
        ((TextView)view.findViewById(R.id.pictureName)).setText(title);
        ((TextView)view.findViewById(R.id.username)).setText("author: " + author);
        return view;
    }

    static ImageFullscreenFragment newInstance(Bitmap bmp, String pictureName, String username) {
        ImageFullscreenFragment pageFragment = new ImageFullscreenFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_BITMAP, bmp);
        arguments.putString(ARGUMENT_TITLE, pictureName);
        arguments.putString(ARGUMENT_AUTHOR, username);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bitmap = getArguments().getParcelable(ARGUMENT_BITMAP);
        title = getArguments().getString(ARGUMENT_TITLE);
        author = getArguments().getString(ARGUMENT_AUTHOR);
    }


}
