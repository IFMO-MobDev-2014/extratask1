package year2013.ifmo.imageloader;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Юлия on 18.01.2015.
 */

public class GetPhotosFromYandex extends AsyncTask<Void, Integer, List<CustomImage>> {

    private ProgressBar progressBar;
    public AsyncResponce delegate = null;

    @Override
    protected List<CustomImage> doInBackground(Void... params) {

        List<CustomImage> images = new ArrayList<CustomImage>();
        XmlPullParserFactory xmlFactoryObject;
        XmlPullParser myParser = null;

        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            myParser = xmlFactoryObject.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet("http://api-fotki.yandex.ru/api/podhistory/poddate;2015-01-17T12:00:00Z/");
        //request.setURI(new URI());
        try {
            response = client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String str1 = response.toString();
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                InputStream inputStream = entity.getContent();
                int event = 0;
                try {
                    event = myParser.getEventType();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                myParser.setInput(inputStream, null);

                int progress = 0;
                String small, big;
                event = XmlPullParser.START_DOCUMENT;
                while (event != XmlPullParser.END_DOCUMENT) {
                    String name = myParser.getName();
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            if (name.equals("content")) {
                                big = myParser.getAttributeValue(null, "src");
                                small = big.replace("XXXL", "XXS").replace("orig", "XXS");
                                images.add(new CustomImage(small, big, ImageService.GetImageByUrl(small)));
                                publishProgress(++progress);
                            }
                            break;
                        case XmlPullParser.TEXT:
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                    }
                    event = myParser.next();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }

        return images;
    }

    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if (this.progressBar != null) {
            progressBar.setProgress(progress[0]);
        }
    }

    protected void onPostExecute(List<CustomImage> result) {
        delegate.ProcessFinished(result);
    }

    public void SetProgressBar(ProgressBar bar){
        this.progressBar = bar;
    }

}
