package mariashka.editors.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mariashka.editors.R;

/**
 * Created by mariashka on 1/16/15.
 */
public class PhotoLoader extends AsyncTaskLoader<List<PhotoItem>>{
    private Handler handler;
    private boolean canceled = false;
    private List<PhotoItem> list = new ArrayList<>();

    protected PhotoLoader(Context context) {
        super(context);
    }

    HttpURLConnection connection;
    InputStream in;
    URL url;

    public List<PhotoItem> getList() {
        return list;
    }


    @Override
    public List<PhotoItem> loadInBackground() {
        try {
            url = new URL(getContext().getString(R.string.editorsUrl));
            connection = (HttpURLConnection) url.openConnection();
            in = connection.getInputStream();
            connection.disconnect();

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            JSONObject j = new JSONObject(responseStrBuilder.toString());
            JSONArray jArray = j.getJSONArray("photos");
            if (jArray == null)
                ;// TODO something
            else {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject curr = jArray.getJSONObject(i);
                    list.add(loadItem(curr));
                    publishProgress(i + 1);

                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    public PhotoItem loadItem(JSONObject curr) throws JSONException, IOException {
        String name = curr.getString("name");
        String descript = curr.getString("description");
        String small_url = curr.getString("image_url");
        String big_url = small_url.replace("2.", "4.");
        JSONObject user = curr.getJSONObject("user");
        String fullname = user.getString("fullname");
        String userpic = user.getString("userpic_url");

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(small_url);
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        int imageLength = (int) (entity.getContentLength());
        InputStream is = entity.getContent();
        byte[] small = new byte[imageLength];
        int bytesRead = 0;
        while (bytesRead < imageLength) {
            int n = is.read(small, bytesRead, imageLength - bytesRead);
            if (n <= 0)
                ; // TODO some error handling
            bytesRead += n;
        }

        request = new HttpGet(big_url);
        response = client.execute(request);
        entity = response.getEntity();
        imageLength = (int) (entity.getContentLength());
        is = entity.getContent();
        byte[] big = new byte[imageLength];
        bytesRead = 0;
        while (bytesRead < imageLength) {
            int n = is.read(big, bytesRead, imageLength - bytesRead);
            if (n <= 0)
                ; // TODO some error handling
            bytesRead += n;
        }

        request = new HttpGet(userpic);
        response = client.execute(request);
        entity = response.getEntity();
        imageLength = (int) (entity.getContentLength());
        is = entity.getContent();
        byte[] face = new byte[imageLength];
        bytesRead = 0;
        while (bytesRead < imageLength) {
            int n = is.read(face, bytesRead, imageLength - bytesRead);
            if (n <= 0)
                ; // TODO some error handling
            bytesRead += n;
        }

        PhotoItem photoItem = new PhotoItem(name, small, big);
        photoItem.descr = descript;
        photoItem.author = fullname;
        photoItem.face = face;

        return photoItem;
    }

    public Bundle getArguments() {
        return null;
    }

    public static Integer getProgress(Message msg) {
        Bundle data = msg.getData();
        if(data.containsKey("message")){
            return data.getInt("message");
        } else {
            return null;
        }
    }

    protected void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setCanceled(boolean cancelled){
        this.canceled = cancelled;
    }

    public boolean isCancelled() {
        return canceled;
    }

    protected void publishProgress(int value){
        if (handler != null) {
            Bundle data = new Bundle();
            data.putInt("message", value);
            Message msg = new Message();
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }
}
