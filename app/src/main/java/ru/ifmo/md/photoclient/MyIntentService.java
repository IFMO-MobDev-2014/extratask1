package ru.ifmo.md.photoclient;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
/**
 * Created by Шолохов on 16.01.2015.
 */
public class MyIntentService extends IntentService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MyIntentService() {
        super("MyLoaderIntentService");
    }

    private String getXmlFromUrl(String urlString) {
        StringBuffer output = new StringBuffer("");
        try {
            InputStream stream;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();

                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(stream));
                String s = "";
                while ((s = buffer.readLine()) != null)
                    output.append(s);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return output.toString();
    }

    ResultReceiver receiver;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onHandleIntent(Intent intent)  {

        int mode = intent.getIntExtra("mode", 1);
        receiver = intent.getParcelableExtra("receiver");
        String link = intent.getStringExtra("link");

        String xml;

        if (mode == 0) {
            receiver.send(MyResultReceiver.PROGRESS, Bundle.EMPTY);
            try {
                Bitmap image = BitmapFactory.decodeStream(new URL(link).openConnection().getInputStream());
                Bundle args = new Bundle();
                args.putParcelable("image", image);
                receiver.send(MyResultReceiver.DONE, args);
                return;
            }   catch (Exception e) {
                receiver.send(MyResultReceiver.FAIL, Bundle.EMPTY);
            }

        }

        getContentResolver().delete(MyContentProvider.TABLE_PHOTOS_URI, null, null);
        int limit = intent.getIntExtra("limit", 0);

        for (int i = 1; i<=limit; i++){
            deleteFile((200 + i) + "");
        }

        try {
            xml = getXmlFromUrl(link);
            if (xml.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Service is unreachanble", Toast.LENGTH_LONG);
                Log.d("Internet", "service is unreachable");
                return;
            }
            InputStream stream = new ByteArrayInputStream(xml.getBytes());
            (new MySAXParser(mode)).parse(stream);
            receiver.send(MyResultReceiver.DONE, Bundle.EMPTY);
        } catch (SAXException e) {
            e.printStackTrace();
            receiver.send(MyResultReceiver.FAIL, Bundle.EMPTY);
        } catch (Exception e) {
            Log.i("BAGUETTE", e.toString());
            receiver.send(MyResultReceiver.FAIL, Bundle.EMPTY);
        }
    }

    public class MySAXParser {
        private int mode ;

        MySAXParser(int mode) {
            this.mode = mode;
        }

        public void parse(InputStream is) throws IOException, SAXException, ParserConfigurationException {
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();

            //efaultHandler saxHandler = new DefaultHandler();

           // if (mode > 0)
            DefaultHandler saxHandler = new SAXLmgListParserHandler();
           // }

            xmlReader.setContentHandler(saxHandler);
            xmlReader.parse(new InputSource(is));
        }
    }


    public class SAXLmgListParserHandler extends DefaultHandler {

        private ContentValues node;
        private String buffer= "";
        private int counter = 0;
        private boolean flag = false;

        @Override
        public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException {
            if (qName.equals("entry")) {
                node = new ContentValues();
                counter++;
                Bundle b = new Bundle();
                b.putInt("count", counter);
                receiver.send(MyResultReceiver.PROGRESS, b);
            }
            if (node != null) {
                if (qName.equals("f:img")) {
                //    Log.d("DEBUG", "f:img found");
                    if (attributes.getValue("size").equals("S")) {
                        Bitmap image;
                        try {
                            image = BitmapFactory.decodeStream(new URL(attributes.getValue("href")).openConnection().getInputStream());
                        } catch (Exception e) {
                            image = BitmapFactory.decodeResource(getResources(), R.drawable.notfound);
                        }
                        node.put(MyContentProvider.COLUMN_PHOTO_NAME , ""+counter);
                        saveImage(counter+"", image);
                    }
                    else if (attributes.getValue("size").equals("L")) {
                        node.put(MyContentProvider.COLUMN_PHOTO_LINK_LARGE, attributes.getValue("href"));
                    }
                }
                if ((qName.equals("link") && attributes.getValue("rel").equals("alternate"))) {
                    node.put(MyContentProvider.COLUMN_PHOTO_LINK_ONSITE, attributes.getValue("href"));
                }
                if (qName.equals("title")) {
                    flag = true;
                    buffer = "";
                }

            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (flag) buffer += new String(ch, start, length);
        }

        @Override
        public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName) throws org.xml.sax.SAXException {
            flag = false;
            if (node != null) {
                if (qName.equals("entry")) {
                    getContentResolver().insert(MyContentProvider.TABLE_PHOTOS_URI, node);
                    node = null;
                }
                else {
                    if (qName.equals("title")) {
                        node.put(MyContentProvider.COLUMN_PHOTO_TAB, buffer);
                    }
                }

            }
        }

    }

    void saveImage(String name, Bitmap image) {
        try {
            deleteFile((name)+"");
            FileOutputStream fos = this.openFileOutput(name, getApplicationContext().MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
