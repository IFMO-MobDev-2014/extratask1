package ru.ifmo.md.extratask1;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Svet on 15.01.2015.
 */
public class JSONParser {
    public static final int COUNT = 16;

    public static ArrayList<ImageContainer> getImagesData(String s) {
        ArrayList<ImageContainer> result = null;
        try {
            JSONObject base = new JSONObject(s);
            JSONArray images = base.getJSONArray("entries");
            result = new ArrayList<ImageContainer>();


            for(int i = 0; i < images.length(); i++) {
                JSONObject current = images.getJSONObject(i);

                JSONObject img = current.getJSONObject("img");
                JSONObject sizeL = img.getJSONObject("L");
                String hrefL = sizeL.getString("href");

                JSONObject sizeXXXL = img.getJSONObject("XXXL");
                String hrefXXXL = sizeXXXL.getString("href");

                String title = current.getString("title");
                String author = current.getString("author");
                ImageContainer container = new ImageContainer(hrefL, hrefXXXL, title, author, "", "", null, null, null);
                result.add(container);
            }

            Random r = new Random();
            while(result.size() > COUNT) {
                int cur = r.nextInt(COUNT);
                result.remove(cur);
            }
        } catch (JSONException e) {
            Log.i("ERROR", "JSONException in getImagesURLs");
        }
        return result;
    }
}
