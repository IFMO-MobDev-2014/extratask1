package ru.ifmo.md.extratask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;


/**
 * Created by gshark on 17.03.15
 */
public class PagerFragment extends Fragment {
    public static final String BUNDLE_NUMBER = "number";
    public static final String BUNDLE_URL = "url";
    public static final String BUNDLE_ID = "id";
    private String url;
    private String id;
    private Bitmap bmp = null;

    public static PagerFragment newInstance(int number, String url, String id) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_NUMBER, number);
        args.putString(BUNDLE_URL, url);
        args.putString(BUNDLE_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public PagerFragment() {
        // Required empty public constructor
    }

    public Bitmap getBitmap() {
        return bmp;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(BUNDLE_URL);
            id = getArguments().getString(BUNDLE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_fullres, null);
        final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBarFullres);

        final File file = new File(getActivity().getExternalFilesDir(null), id);
        Runnable setImg = new Runnable() {
            @Override
            public void run() {
                bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(bmp);
                progressBar.setVisibility(View.INVISIBLE);
            }
        };
        if (!file.exists()) {
            new ImageDownloadTask(file, setImg).execute(url);
        } else {
            setImg.run();
        }
        return view;
    }
}
