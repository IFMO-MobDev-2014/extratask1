package lapics.sergeybudkov.ru.lapics;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class AllPictureShow extends Activity {

    public static int W,H;
    private int width;
    public static ProgressDialog dialog;
    private GridView pictures;
    private MyAdapter adapter;
    private List<Bitmap> bitmapList, smallPictures = new ArrayList<Bitmap>();
    private PicsDatabase database;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_pictures);
        DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        width = dimension.widthPixels;
        pictures = (GridView) findViewById(R.id.grid_view);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            pictures.setNumColumns(2);
            pictures.setColumnWidth((int) (width / 2.1));
            W = (int) (width / 2.1);
            H = (int) (width / 2.1);
        } else {
            pictures.setNumColumns(4);
            pictures.setColumnWidth((int) (width / 4.1));
            W = (int) (width / 4.1);
            H = (int) (width / 4.1);
        }
        pictures.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        database = new PicsDatabase(this);
        database.open();
        bitmapList = database.getAllPicturesData();
        getPictures();
        if (bitmapList.isEmpty()) {
            loadImages();
        }
        adapter = new MyAdapter(this, android.R.layout.simple_list_item_1, smallPictures);
        pictures.setAdapter(adapter);
        pictures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), SinglePictureShow.class);
                intent.putExtra("index", i);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        width = dimension.widthPixels;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            pictures.setNumColumns(2);
            pictures.setColumnWidth((int) (width / 2.1));
            W = (int) (width / 2.1);
            H = (int) (width / 2.1);
        } else {
            pictures.setNumColumns(4);
            pictures.setColumnWidth((int) (width / 4.1));
            W = (int) (width / 4.1);
            H = (int) (width / 4.1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_pictures_menu, menu);
        return true;
    }


    private void loadImages() {
        dialog = new ProgressDialog(AllPictureShow.this);
        dialog.setCancelable(false);
        dialog.setTitle("Загружаем картники");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(40);
        dialog.setIndeterminate(true);
        dialog.show();
        database.deleteAllChannels();
        new PictureDownload() {
            @Override
            protected void onPostExecute(List<SinglePicture> pictures) {
                if (pictures.size() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "NO Network",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                bitmapList.clear();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Saving pictures, please wait...",
                        Toast.LENGTH_LONG);
                toast.show();
                for (int i = 0; i < pictures.size(); i++) {
                    bitmapList.add(pictures.get(i).getBigImage());
                    database.addChannel(pictures.get(i));
                }
                getPictures();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                toast = Toast.makeText(getApplicationContext(),
                        "Load Finished",
                        Toast.LENGTH_LONG);
                toast.show();

            }
        }.execute();
    }

    private Bitmap getSmallPictures(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, W, H, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if(item.getItemId() == R.id.item2){
            loadImages();
        }
        return false;
    }

    public void getPictures() {
        smallPictures.clear();
        for (int i = 0; i < bitmapList.size(); i++)
            smallPictures.add(getSmallPictures(bitmapList.get(i)));
    }
}
