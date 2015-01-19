package com.adelnator.extratask1;

import android.graphics.BitmapFactory;
import android.widget.ProgressBar;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adel on 18.01.15.
 */
public class Analyser extends DefaultHandler {
    private List<Picture> list;
    private int count;

    @Override
    public void startDocument() throws SAXException {
        list = new ArrayList<Picture>();
        count = 0;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (count < 30)
            if (qName.equals("f:img") && attributes.getValue(attributes.getIndex("size")).equals("M")) {
                try {
                    int width = Integer.parseInt(attributes.getValue(attributes.getIndex("width")));
                    int height = Integer.parseInt(attributes.getValue(attributes.getIndex("height")));
                    Picture photo = new Picture();
                    photo.width = width;
                    photo.height = height;
                    photo.bitmap = BitmapFactory.decodeStream(new URL(attributes.getValue(attributes.getIndex("href"))).openStream());
                    count++;
                    list.add(photo);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
    }

    public List<Picture> getResult() {
        return list;
    }

}
