package me.loskutov.popularphotosviewer;

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
 * A simple {@link Fragment} subclass.
 * Use the {@link PagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PagerFragment extends Fragment {

    private String url;
    private String id;
    private Bitmap bmp = null;

    public static PagerFragment newInstance(int number, String url, String id) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putInt("number", number);
        args.putString("url", url);
        args.putString("id", id);
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
            url = getArguments().getString("url");
            id = getArguments().getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_fullres, null);
        final ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
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
        if(!file.exists()) {
            (new ImageDownloadTask(url, file, setImg)).execute();
        } else {
            setImg.run();
        }
        return view;
    }
}
