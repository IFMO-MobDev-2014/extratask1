package ru.ifmo.ctddev.soloveva.photoviewer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.IOException;

import ru.ifmo.ctddev.soloveva.photoviewer.px500.PhotoList;
import ru.ifmo.ctddev.soloveva.photoviewer.px500.Px500Api;

/**
 * Created by maria on 17.01.15.
 */
public class CategoryFragment extends Fragment {
    public static final String CATEGORY_KEY = "category";
    private ViewPager viewPager;
    private String category;
    private ProgressBar progressBar;
    private PreloadTask preloadTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        category = getArguments().getString(CATEGORY_KEY);

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        preloadTask = new PreloadTask();
        preloadTask.execute();

        return view;
    }

    @Override
    public void onDestroyView() {
        preloadTask.cancel(true);
        super.onDestroyView();
    }

    private class PreloadTask extends AsyncTask<Void, Void, PhotoList> {
        @Override
        protected void onPreExecute() {
            viewPager.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected PhotoList doInBackground(Void... params) {
            Px500Api api = new Px500Api();
            try {
                return api.getPhotoList(category, 1);
            } catch (IOException e) {
                Log.w("pre", e);
                return null;
            } finally {
                try {
                    api.close();
                } catch (IOException ignored) {}
            }
        }

        @Override
        protected void onPostExecute(PhotoList photoList) {
            progressBar.setVisibility(View.GONE);
            viewPager.setAdapter(new PagesAdapter(
                    getActivity().getSupportFragmentManager(),
                    category,
                    getResources().getString(R.string.page),
                    photoList.getTotalPages()
            ));
            viewPager.setVisibility(View.VISIBLE);
        }
    }
}
