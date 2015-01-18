package ru.ifmo.md.flickrclient;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.REST;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by sultan on 16.01.15.
 */
public class FlickrHelper {
    private static final String API_KEY = "e22326b3c2fe2090c87382d84d50f7c2";
    private static final String API_SECRET = "c81af42d459dbc6f";
    private static FlickrHelper instance = null;

    private FlickrHelper() {};

    public static FlickrHelper getInstance() {
        if (instance == null) {
           instance = new FlickrHelper();
        }

        return instance;
    }

    public Flickr getFlickr() {
        Flickr f = null;
        try {
            f = new Flickr(API_KEY, API_SECRET, new REST());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return f;
    }

}
