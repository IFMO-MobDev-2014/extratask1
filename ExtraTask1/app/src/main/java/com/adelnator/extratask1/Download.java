package com.adelnator.extratask1;

import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adel on 18.01.15.
 */
public class Download extends AsyncTask<Void, Void, List<Picture>> {

    @Override
    protected List<Picture> doInBackground(Void... voids) {
        List<Picture> list = new ArrayList<Picture>();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet("http://api-fotki.yandex.ru/api/top/"));
            HttpEntity httpEntity = httpResponse.getEntity();

            String xml = EntityUtils.toString(httpEntity);
            InputSource is = new InputSource(new StringReader(xml));
            Analyser handler = new Analyser();
            parser.parse(is, handler);

            list = handler.getResult();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}