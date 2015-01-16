package lapics.sergeybudkov.ru.lapics;

import android.graphics.BitmapFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Handler extends DefaultHandler {
    private static final int PICTURES_COUNT = 40;
    private static final String PICTURES_SIZE = "L";
    private List<SinglePicture> list;
    private int count;
    @Override
    public void startDocument() throws SAXException {
        list = new ArrayList<SinglePicture>();
        count = 0;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (count < PICTURES_COUNT){
            if (qName.equals("f:img") && attributes.getValue(attributes.getIndex("size")).equals(PICTURES_SIZE)) {
                try {
                    int width = Integer.parseInt(attributes.getValue(attributes.getIndex("width")));
                    int height = Integer.parseInt(attributes.getValue(attributes.getIndex("height")));
                    list.add(new SinglePicture(
                            width,
                            height,
                            BitmapFactory.decodeStream(new URL(attributes.getValue(attributes.getIndex("href"))).openStream())
                    ));
                    AllPictureShow.dialog.setIndeterminate(false);
                    AllPictureShow.dialog.incrementProgressBy(1);
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<SinglePicture> getResult() {
        return list;
    }


}