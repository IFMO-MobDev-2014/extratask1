/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kirill.topyandexphoto.LoadDataService;
import com.example.kirill.topyandexphoto.db.PhotoContentProvider;
import com.example.kirill.topyandexphoto.db.model.ImageDataTable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.example.kirill.topyandexphoto.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Kirill Timofeev, based on
 * Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * Source code
 */
public class ImageGridFragment extends AbsListViewBaseFragment {

	String[] imageUrls = {};

    int loadedCount = 0;

    int curPosition = 0;

    private ImageAdapter adapter;

	DisplayImageOptions options;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultType = bundle.getInt(LoadDataService.RESULT_TYPE);
                if (resultType == LoadDataService.ERROR_RESULT) {
                    Toast.makeText(context, bundle.getString(LoadDataService.ERROR_MSG), Toast.LENGTH_SHORT).show();
                } else if (resultType == LoadDataService.FINISHED_RESULT) {
                    ArrayList<String> arr = bundle.getStringArrayList(LoadDataService.PREVIEW_URLS);
                    imageUrls = new String[arr.size()];
                    for(int i = 0; i < imageUrls.length; i++)
                        imageUrls[i] = arr.get(i);
                    Arrays.sort(imageUrls);
                    adapter.notifyDataSetInvalidated();
                }
            }
        }
    };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        adapter = new ImageAdapter();

        imageUrls = readFromDb();
        if (imageUrls.length == 0) {
            update();
        }

		options = new DisplayImageOptions.Builder()
				//.showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
	}

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(LoadDataService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_refresh:
                update();
                return true;
            case R.id.item_clear_cache:
                int deleted = getActivity().getContentResolver()
                        .delete(PhotoContentProvider.CONTENT_URI_IMAGES, ImageDataTable._ID + " != ?", new String[]{"-1"});
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                imageUrls = new String[0];
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fr_image_grid, container, false);
		listView = (GridView) rootView.findViewById(R.id.grid);
		((GridView) listView).setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(position);
			}
		});
		return rootView;
	}

	public class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		ImageAdapter() {
			inflater = LayoutInflater.from(getActivity());
		}

		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
            curPosition = position;
            View view = convertView;
			if (view == null) {
				view = inflater.inflate(R.layout.item_grid_image, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			ImageLoader.getInstance()
					.displayImage(imageUrls[position], holder.imageView, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.progressBar.setProgress(0);
							holder.progressBar.setVisibility(View.VISIBLE);
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

                            holder.progressBar.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							holder.progressBar.setVisibility(View.GONE);
						}
					}, new ImageLoadingProgressListener() {
						@Override
						public void onProgressUpdate(String imageUri, View view, int current, int total) {
							holder.progressBar.setProgress(Math.round(100.0f * current / total));
						}
					});

			return view;
		}
	}

	static class ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
	}

    private void update() {
        loadedCount = 0;
        Intent intent = new Intent(getActivity(), LoadDataService.class);
        getActivity().startService(intent);
    }

    private String[] readFromDb() {
        Cursor cursor = getActivity().getContentResolver()
                .query(PhotoContentProvider.CONTENT_URI_IMAGES, null, null, null, null);
        cursor.moveToFirst();
        String[] rv = new String[cursor.getCount()];
        int i = 0;
        while (!cursor.isAfterLast()) {
            rv[i] = cursor.getString(cursor.getColumnIndex(ImageDataTable.PREVIEW_URL_COLUMN));
            cursor.moveToNext();
            i++;
        }
        Arrays.sort(rv);
        return rv;
    }
}