package ru.ifmo.instafeed.ui.slidingTabs;

/**
 * Created by creed on 19.01.15.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import ru.ifmo.instafeed.R;
import ru.ifmo.instafeed.model.MyFeed;
import ru.ifmo.instafeed.rest.RestClient;
import ru.ifmo.instafeed.ui.feed.EndlessScrollListener;
import ru.ifmo.instafeed.ui.feed.ImageActivity;
import ru.ifmo.instafeed.ui.feed.ImageAdapter;

public class SlidingTabsBasicFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";
    private Context context;


    public SlidingTabsBasicFragment() {

    }

    /*public SlidingTabsBasicFragment(Context context) {
        this.context = context;
    }*/

    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity().getApplicationContext();
        // Notify the system to allow an options menu for this fragment.
        setHasOptionsMenu(true);
    }

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sample, container, false);

        // Retrieve the SwipeRefreshLayout and GridView instances
        //mSwipeRefreshLayout = (MultiSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        return view;
    }

    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        //MultiSlidingRefreshLayout
        /*mSwipeRefreshLayout.setOnRefreshListener(new MultiSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //myFeed.refresh();
                //Toast.makeText(this, R.string.refresh_started, Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(true);
                // ждем 3 секунды и прячем прогресс
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        //Toast.makeText(MainActivity.this, R.string.refresh_finished, Toast.LENGTH_SHORT).show();
                    }
                }, 3000);
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.bright_foreground_material_dark, R.color.bright_foreground_material_light
        );
        mSwipeRefreshLayout.setProgressBackgroundColor(Color.DKGRAY);*/
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabLayout}.
     */
    class SamplePagerAdapter extends PagerAdapter {

        private final Integer COUNT = 2;
        private SwipeRefreshLayout mSwipeRefreshLayout;

        @Override
        public int getCount() {
            return COUNT;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Popular";
            } else {
                return "Feed";
            }
            //return "Item " + (position + 1);
        }

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getActivity().getLayoutInflater().inflate(R.layout.pager_item, container, false);
            // Add the newly created View to the ViewPager
            container.addView(view);

            /*mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //myFeed.refresh();
                    //Toast.makeText(MainActivity.this, R.string.refresh_started, Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(true);
                    // ждем 3 секунды и прячем прогресс
                    mSwipeRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                            //Toast.makeText(MainActivity.this, R.string.refresh_finished, Toast.LENGTH_SHORT).show();
                        }
                    }, 3000);
                }
            });
            mSwipeRefreshLayout.setColorSchemeColors(
                    R.color.bright_foreground_material_dark, R.color.bright_foreground_material_light
            );
            mSwipeRefreshLayout.setProgressBackgroundColor(Color.DKGRAY);*/

            // Retrieve a TextView from the inflated View, and update it's text
            //TextView title = (TextView) view.findViewById(R.id.item_title);
            //title.setText(String.valueOf(position + 1));

            //RestClient
            RestClient restClient = new RestClient();

            //ImageAdapter
            ImageAdapter imageAdapter = new ImageAdapter(context);

            //MyFeed
            final MyFeed myFeed = new MyFeed(restClient, imageAdapter);
            final String source = (position == 0 ? "popular" : "feed");
            myFeed.loadItems(source);

            //GridView
            GridView gridView = (GridView)view.findViewById(R.id.gridView);
            gridView.setVerticalScrollBarEnabled(false);
            gridView.setHorizontalScrollBarEnabled(false);
            gridView.setAdapter(imageAdapter);
            if (source.equals("feed")) {
                gridView.setOnScrollListener(new EndlessScrollListener() {
                    @Override
                    public void onLoadMore(int page, int total) {
                        myFeed.loadItems(source);
                    }
                });
            }
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(context, ImageActivity.class);
                    String url = myFeed.getItems().get(position).getImages().getStandardResolution().getUrl();
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });

            Log.i(LOG_TAG, "instantiateItem() [position: " + position + "]");

            // Return the View
            return view;
        }

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

    }
}   