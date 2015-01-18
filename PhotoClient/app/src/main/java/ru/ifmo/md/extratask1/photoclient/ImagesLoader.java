package ru.ifmo.md.extratask1.photoclient;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ru.ifmo.md.extratask1.photoclient.database.ImagesProvider;
import ru.ifmo.md.extratask1.photoclient.database.ImagesTable;

/**
 * Created by sergey on 16.01.15.
 */
public class ImagesLoader extends IntentService {

    public static final String ACTION_LOAD_FEED = "ru.ifmo.md.extratask1.LOAD_FEED";
    public static final String ACTION_LOAD_BIG_PHOTO = "ru.ifmo.md.extratask1.LOAD_BIG_PHOTO";

    public static final String EXTRA_PHOTO_URL = "photo_url";

    public static final String URL_RECENT_PHOTOS = "http://api-fotki.yandex.ru/api/recent/";
//    public static final String URL_TOP_PHOTOS = "http://api-fotki.yandex.ru/api/top/";
//    public static final String URL_DAY_PHOTOS = "http://api-fotki.yandex.ru/api/podhistory/";
    public static final int LIMIT_PHOTOS = 30;

    private BroadcastStateSender broadcastStateSender = new BroadcastStateSender(this);

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ImagesLoader(String name) {
        super(name);
    }

    public ImagesLoader() {
        super("ImagesLoaderIntentService");
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailable()) {
            broadcastStateSender.sendBroadcastState(BroadcastStateSender.STATE_NO_CONNECTION);
            return;
        }
        if (intent != null) {
            final String actionType = intent.getAction();
            switch (actionType) {
                case ACTION_LOAD_FEED:
                    actionLoadFeed();
                    break;
                case ACTION_LOAD_BIG_PHOTO:
                    String imageURL = intent.getStringExtra(EXTRA_PHOTO_URL);
                    actionLoadBigPhoto(imageURL);
                    break;
            }
        }
    }

    public static void startActionLoadFeed(Context context) {
        Intent intent = new Intent(context, ImagesLoader.class);
        intent.setAction(ACTION_LOAD_FEED);
        context.startService(intent);
    }

    public static void startActionLoadBigPhoto(Context context, String imageURL) {
        Intent intent = new Intent(context, ImagesLoader.class);
        intent.setAction(ACTION_LOAD_BIG_PHOTO);
        intent.putExtra(EXTRA_PHOTO_URL, imageURL);
        context.startService(intent);
    }

    private void actionLoadFeed() {
        String photosType = URL_RECENT_PHOTOS;
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(buildPhotosURL(photosType));
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                ImageFeed resultFeed = parseFeedXML(is);
                addPhotosToDatabase(resultFeed);
                broadcastStateSender.sendBroadcastState(BroadcastStateSender.STATE_COMPLETE);
            } else {
                broadcastStateSender.sendBroadcastState(BroadcastStateSender.STATE_ERROR);
            }
        } catch (IOException e) {
            broadcastStateSender.sendBroadcastState(BroadcastStateSender.STATE_ERROR);
            e.printStackTrace();
        }
    }

    private void actionLoadBigPhoto(String imageURL) {
        try {
            ImageFilesHandler.downloadAndSaveImage(getApplicationContext(), imageURL);
        } catch (IOException e) {
            e.printStackTrace();
            broadcastStateSender.sendBroadcastState(BroadcastStateSender.STATE_ERROR);
            return;
        }
        broadcastStateSender.sendBroadcastState(BroadcastStateSender.STATE_COMPLETE);
    }

    private void addPhotosToDatabase(ImageFeed imageFeed) {
        ArrayList<ImageEntry> imageEntries = imageFeed.getImageEntries();
        ArrayList<ContentValues> contentValueses = new ArrayList<>();
        for (ImageEntry imageEntry : imageEntries) {
            //TODO: update progress by known number of new photos;
            //Find URLs of small and big versions
            String smallVersionURL = getMostCompatibleSmallImageURL(imageEntry.getVariants());
            String bigVersionURL = getMostCompatibleBigImageURL(imageEntry.getVariants());

            try {
                ImageFilesHandler.downloadAndSaveImage(getApplicationContext(), smallVersionURL);
            } catch (IOException e) {
                broadcastStateSender.sendBroadcastState(BroadcastStateSender.STATE_ERROR);
                return;
            }

            ContentValues values = new ContentValues();
            values.put(ImagesTable.COLUMN_TITLE, imageEntry.getTitle());
            values.put(ImagesTable.COLUMN_LINK, imageEntry.getUrlOnWeb());
            values.put(ImagesTable.COLUMN_AUTHOR_NAME, imageEntry.getAuthor().getName());
            values.put(ImagesTable.COLUMN_SMALL_CONTENT_URI, smallVersionURL);
            values.put(ImagesTable.COLUMN_BIG_CONTENT_URI, bigVersionURL);
            getContentResolver().insert(ImagesProvider.CONTENT_URI, values);
//            contentValueses.add(values);
        }
//        ContentValues arrayValues[] = new ContentValues[contentValueses.size()];
//        contentValueses.toArray(arrayValues);
//        getContentResolver().bulkInsert(ImagesProvider.CONTENT_URI, arrayValues);
//        getContentResolver().notifyChange();
        broadcastStateSender.sendBroadcastState(BroadcastStateSender.STATE_COMPLETE);
    }

    final static String[] smallPriorities = new String[] {"M", "S", "L", "XS", "XXS"};
    private String getMostCompatibleSmallImageURL(ArrayList<ImageEntry.ImageVariant> variants) {
        String result = null;
        int smallCurrentIndex = smallPriorities.length;
        for (ImageEntry.ImageVariant variant : variants) {
            String href = variant.getHref();
            String size = variant.getSize();
            for (int index = 0; index < smallPriorities.length; index++)
                if (smallPriorities[index].equals(size))
                    if (smallCurrentIndex > index) {
                        smallCurrentIndex = index;
                        result = href;
                    }
        }
        return result;
    }
    final static String[] bigPriorities = new String[] {"XXXXL", "XXXL", "XXL", "XL", "L"};

    private String getMostCompatibleBigImageURL(ArrayList<ImageEntry.ImageVariant> variants) {
        String result = null;
        int bigCurrentIndex = bigPriorities.length;
        for (ImageEntry.ImageVariant variant : variants) {
            String href = variant.getHref();
            String size = variant.getSize();
            for (int index = 0; index < bigPriorities.length; index++)
                if (bigPriorities[index].equals(size))
                    if (bigCurrentIndex > index) {
                        bigCurrentIndex = index;
                        result = href;
                    }
        }
        return result;
    }

    private boolean isAlreadyInDatabase(ImageEntry imageEntry) {
        Cursor finder = getContentResolver().query(
                ImagesProvider.CONTENT_URI,
                new String[]{ImagesTable.COLUMN_LINK},
                ImagesTable.COLUMN_LINK + " = ?",
                new String[]{imageEntry.getUrlOnWeb()},
                null);
        boolean result = finder.getCount() > 0;
        finder.close();
        return result;
    }

    private ImageFeed parseFeedXML(InputStream is) {
        ImageFeed resultFeed = new ImageFeed();
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new InputStreamReader(is));
            int event = parser.getEventType();

            ImageEntry imageEntry = null;
            String tagName = null;
            String currentTag = null;

            while (event != XmlPullParser.END_DOCUMENT) {
                tagName = parser.getName();
                if (event == XmlPullParser.START_TAG) {
                    switch (tagName) {
                        case "title":
                            if (imageEntry == null) {
                                currentTag = "feed_title";
                            } else {
                                currentTag = "image_title";
                            }
                            break;
                        case "entry":
                            imageEntry = new ImageEntry();
                            break;
                        case "id":
                            currentTag = "entry_id";
                            break;
                        case "name":
                            currentTag = "author_name";
                            break;
                        case "f:uid":
                            currentTag = "author_id";
                            break;
                        case "link":
                            String href = parser.getAttributeValue(null, "href");
                            String rel = parser.getAttributeValue(null, "rel");
                            if (imageEntry != null) {
                                if (rel.equals("alternate"))
                                    imageEntry.setUrlOnWeb(href);
                            } else if (rel.equals("next"))
                                resultFeed.setNextFeedLink(href);
                            break;
                        case "f:img":
                            int height = Integer.parseInt(parser.getAttributeValue(null, "height"));
                            int width = Integer.parseInt(parser.getAttributeValue(null, "width"));
                            href = parser.getAttributeValue(null, "href");
                            String size = parser.getAttributeValue(null, "size");
                            if (imageEntry != null)
                                imageEntry.addImageVariant(height, width, href, size);
                            break;
                        case "content":
                            if (imageEntry != null)
                                imageEntry.setContentUrl(parser.getAttributeValue(null, "src"));
                            break;
                        default:
                            currentTag = tagName;
                            break;
                    }
                } else if (event == XmlPullParser.TEXT) {
                    if (currentTag != null) {
                        switch (currentTag) {
                            case "entry_id":
                                if (imageEntry != null)
                                    imageEntry.setImageWebId(parser.getText());
                                currentTag = "another";
                                break;
                            case "feed_title":
                                resultFeed.setTitle(parser.getText());
                                currentTag = "another";
                                break;
                            case "image_title":
                                imageEntry.setTitle(parser.getText());
                                currentTag = "another";
                                break;
                            case "author_name":
                                imageEntry.getAuthor().setName(parser.getText());
                                currentTag = "another";
                                break;
                            case "author_id":
                                imageEntry.getAuthor().setUid(Long.parseLong(parser.getText()));
                                currentTag = "another";
                                break;
                        }
                    }
                } else if (event == XmlPullParser.END_TAG) {
                    if (tagName.equals("entry")) {
                        if (!isAlreadyInDatabase(imageEntry))
                            resultFeed.getImageEntries().add(imageEntry);
                    }
                }
                event = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return resultFeed;
    }

    private String buildPhotosURL(String photosType) {
        return photosType + "/?limit=" + LIMIT_PHOTOS;
    }

}
