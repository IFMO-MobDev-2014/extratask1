package ru.ifmo.md.extratask1;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Svet on 15.01.2015.
 */
public class MyImageLoadService extends IntentService {
    public static final String REQUEST_TYPE = "request_type";
    public static final String REQUEST_ADDRESS = "request_address";
    public static final String IMAGE_NAME = "image_name";
    public static final String RESPONSE_STRING = "json_result";
    public static final String ANSWER_TYPE = "answer_type";
    public static final String IMAGE_INDEX = "image_index";
    public static final String IMAGE_SIZE = "image_size";
    public static final String DIFF = "diff";
    public static final String IMAGE_ADDRESS = "address_small";


    public MyImageLoadService() {
        super("MyImageLoadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(REQUEST_TYPE, -1);
        switch(type) {
            case 1 : {
                downloadSmallImage(intent);
                break;
            }
            case 2 : {
                downloadJSONFile(intent);
                break;
            }
            case 3 : {
                downloadHugeImage(intent);
                break;
            }
        }
    }

    private void downloadJSONFile(Intent intent) {
        String request = intent.getStringExtra(REQUEST_ADDRESS);
        HttpClient httpCliend = new DefaultHttpClient();
        HttpResponse response;
        String answer = null;
        try {
            response = httpCliend.execute(new HttpGet(request));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                answer = out.toString();
            } else {
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch(ClientProtocolException ex) {
            Log.e("ERROR", "ClientProtocolException in LoadInfoMethod");
        } catch(IOException e) {
            Log.e("ERROR", "IOException in LoadInfoMethod");
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MyBroadcastReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(ANSWER_TYPE, 11);
        broadcastIntent.putExtra(RESPONSE_STRING, answer);
        sendBroadcast(broadcastIntent);
    }

    private void downloadHugeImage(Intent intent) {
        String request = intent.getStringExtra(REQUEST_ADDRESS);
        String imageName = intent.getStringExtra(IMAGE_NAME);
        int index = intent.getIntExtra(IMAGE_INDEX, -1);


        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();

            String filePath = Environment.getExternalStorageDirectory().toString().concat("/savedImages");
            File file = new File(filePath);
            file.mkdirs();
            File output = new File(file, imageName + ".jpeg");

            FileOutputStream f = new FileOutputStream(output);
            InputStream input = connection.getInputStream();
            byte [] buffer = new byte[1024];
            int length;
            while((length = input.read(buffer)) > 0) {
                f.write(buffer, 0, length);
            }

            Intent send = new Intent();
            send.setAction(MyBroadcastReceiver.PROCESS_RESPONSE);
            send.addCategory(Intent.CATEGORY_DEFAULT);
            send.putExtra(ANSWER_TYPE, 4);
            send.putExtra(IMAGE_ADDRESS, output.getAbsolutePath());
            send.putExtra(IMAGE_INDEX, index);
            sendBroadcast(send);

            input.close();
            f.close();

        } catch(MalformedURLException e) {
            Log.i("ERROR", "MalformedURLException in downloadAndStoreImage()");
        } catch(IOException e) {
            Log.i("ERROR", "IOException in downloadAndStoreImage()");
        }
    }

    private void downloadSmallImage(Intent intent) {
        String request = intent.getStringExtra(REQUEST_ADDRESS);
        String imageName = intent.getStringExtra(IMAGE_NAME);
        int index = intent.getIntExtra(IMAGE_INDEX, -1);

        Intent send = new Intent();
        send.setAction(MyBroadcastReceiver.PROCESS_RESPONSE);
        send.addCategory(Intent.CATEGORY_DEFAULT);

        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();

            int size = connection.getContentLength();

            send.putExtra(ANSWER_TYPE, 1);
            send.putExtra(IMAGE_INDEX, index);
            send.putExtra(IMAGE_SIZE, size);
            sendBroadcast(send);


            String filePath = Environment.getExternalStorageDirectory().toString().concat("/savedImages");
            File file = new File(filePath);
            file.mkdirs();
            File output = new File(file, imageName + ".jpeg");

            FileOutputStream f = new FileOutputStream(output);
            InputStream input = connection.getInputStream();
            byte [] buffer = new byte[1024];
            int length;
            while((length = input.read(buffer)) > 0) {
                f.write(buffer, 0, length);

                send.putExtra(ANSWER_TYPE, 2);
                send.putExtra(IMAGE_INDEX, index);
                send.putExtra(DIFF, length);
                sendBroadcast(send);
            }

            send.putExtra(ANSWER_TYPE, 3);
            send.putExtra(IMAGE_ADDRESS, output.getAbsolutePath());
            send.putExtra(IMAGE_INDEX, index);
            sendBroadcast(send);

            input.close();
            f.close();

        } catch(MalformedURLException e) {
            Log.i("ERROR", "MalformedURLException in downloadAndStoreImage()");
        } catch(IOException e) {
            Log.i("ERROR", "IOException in downloadAndStoreImage()");
        }
    }
}
