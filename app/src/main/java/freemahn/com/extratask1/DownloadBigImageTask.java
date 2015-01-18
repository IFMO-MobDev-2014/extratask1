package freemahn.com.extratask1;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by Freemahn on 18.01.2015.
 */
public class DownloadBigImageTask extends AsyncTask<String, Integer, Bitmap> {
    ProgressDialog pd;
    Context context;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.show();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (pd.isShowing()) {
            pd.dismiss();
        }
        ((ImageActivity) context).setImageBitmap(bitmap);

    }

    public DownloadBigImageTask(Context c) {
        context = c;
        pd = new ProgressDialog(context);
        pd.setTitle("Loading big size image");
        pd.setMessage("Please wait...");

    }

    @Override
    protected Bitmap doInBackground(String... imgUrl) {
        try {

            //Thread.sleep(4000);

            return BitmapFactory.decodeStream(new URL(imgUrl[0]).openConnection().getInputStream());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
