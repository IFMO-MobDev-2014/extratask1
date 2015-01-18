/*******************************************************************************
* Copyright 2011-2014 Sergey Tarasevich
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
package com.example.kirill.topyandexphoto.fragment;

import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kirill.topyandexphoto.db.PhotoContentProvider;
import com.example.kirill.topyandexphoto.db.model.ImageDataTable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.example.kirill.topyandexphoto.Constants;
import com.example.kirill.topyandexphoto.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

/**
* @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
*/
public class ImagePagerFragment extends Fragment {

	public static final int INDEX = 2;

    ViewPager pager;
    Img[] imageUrls;
	DisplayImageOptions options;
    View currentView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        imageUrls = readFromDb();

		options = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.ic_error)
				.resetViewBeforeLoading(true)
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300))
				.build();

        setHasOptionsMenu(true);
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.big_image_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_open_in_browser:
                openInBrowser();
                return true;
            case R.id.item_save:
                save();
                return true;
            case R.id.item_set_as_wallpaper:
                setAsWallPaper();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_image_pager, container, false);
		pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(new ImageAdapter());
		pager.setCurrentItem(getArguments().getInt(Constants.Extra.IMAGE_POSITION, 0));
		return rootView;
	}

	private class ImageAdapter extends PagerAdapter {

		private LayoutInflater inflater;

		ImageAdapter() {
			inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			ImageLoader.getInstance().displayImage(imageUrls[position].url, imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
						case IO_ERROR:
							message = getResources().getString(R.string.error_io);
							break;
						case DECODING_ERROR:
							message = getResources().getString(R.string.error_decoding);
							break;
						case NETWORK_DENIED:
							message = getResources().getString(R.string.error_network_denied);
							break;
						case OUT_OF_MEMORY:
							message = getResources().getString(R.string.error_out_of_memory);
							break;
						case UNKNOWN:
							message = getResources().getString(R.string.error_unknown);
							break;
					}
					Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            currentView = (View)object;
            if (imageUrls != null && position < imageUrls.length) {
                getActivity().setTitle(imageUrls[position].title);
            }
        }

        @Override
		public Parcelable saveState() {
			return null;
		}
	}

    private void setAsWallPaper() {
        WallpaperManager manager = WallpaperManager.getInstance(getActivity());
        try {
            manager.setBitmap(getCurrentBitmap());
            Toast.makeText(getActivity(), "Wallpaper has been changed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    private void save() {
        OutputStream fOut = null;
        String strDirectory = Environment.getExternalStorageDirectory().toString();
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        File f = new File(strDirectory, ""+seconds);
        try {
            fOut = new FileOutputStream(f);
            Bitmap bitmap = getCurrentBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush();
            fOut.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, f.getName());
            values.put(MediaStore.Images.Media.DESCRIPTION, f.getName());
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
            getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Toast.makeText(getActivity(), "Image has been saved in gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
        }
    }

    private void openInBrowser() {
        int pos = pager.getCurrentItem();
        String link = imageUrls[pos].alternative;
        Uri uri = Uri.parse(link);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
        getActivity().startActivity(browserIntent);
    }

    private Bitmap getCurrentBitmap() {
        int pos = pager.getCurrentItem();
        Bitmap rv = ImageLoader.getInstance().loadImageSync(imageUrls[pos].url);
        return rv;
    }

    private Img[] readFromDb() {
        Cursor cursor = getActivity().getContentResolver()
                .query(PhotoContentProvider.CONTENT_URI_IMAGES, null, null, null, null);
        cursor.moveToFirst();
        Img[] rv = new Img[cursor.getCount()];
        int i = 0;
        while (!cursor.isAfterLast()) {
            rv[i] = new Img();
            rv[i].url = cursor.getString(cursor.getColumnIndex(ImageDataTable.BIG_URL_COLUMN));
            rv[i].title = cursor.getString(cursor.getColumnIndex(ImageDataTable.TITLE_COLUMN));
            rv[i].alternative = cursor.getString(cursor.getColumnIndex(ImageDataTable.ENTRY_URL_COLUMN));
            cursor.moveToNext();
            i++;
        }
        Arrays.sort(rv, new comp());
        return rv;
    }

    private class Img {
        public String title;
        public String url;
        public String alternative;
        public Img() {}
    }
    private class comp implements Comparator<Img>{
        @Override
        public int compare(Img a, Img b){
            return a.url.compareTo(b.url);
        }
    }
}