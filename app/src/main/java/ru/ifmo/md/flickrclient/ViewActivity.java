package ru.ifmo.md.flickrclient;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


public class ViewActivity extends ActionBarActivity {

    public static final String IMAGE_ID = "IMAGE_ID";

    private long view_id;
    private ImageView imageView;
    private ProgressDialog progressDialog;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        view_id = getIntent().getLongExtra(IMAGE_ID, -1);
        imageView = (ImageView) findViewById(R.id.full_image);

        if (view_id != -1) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Downloading image");
            progressDialog.show();
            toast = new Toast(this);
            ImageTask imageTask = new ImageTask(getContentResolver(), imageView, progressDialog, toast);
            imageTask.execute(view_id);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
