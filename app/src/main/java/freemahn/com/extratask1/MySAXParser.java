package freemahn.com.extratask1;

/**
 * Created by Freemahn on 16.01.2015.
 */


import android.util.Log;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Freemahn on 05.01.2015.
 */
public class MySAXParser {

    //parse entry, title and link
    public static ArrayList<Entry> parse(InputStream is) {
        final ArrayList<Entry> list = new ArrayList<Entry>();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            DefaultHandler handler = new DefaultHandler() {

                boolean entry = false;
                boolean title = false;
                boolean link = false;
                Entry current;

                public void startElement(String uri, String localName, String qName,
                                         Attributes attributes) throws SAXException {
                    if (qName.equalsIgnoreCase("entry") || qName.equalsIgnoreCase("item")) {
                        current = new Entry();
                        entry = true;
                    }

                    if (qName.equalsIgnoreCase("title") & entry) {
                        title = true;
                    }

                    if (qName.equalsIgnoreCase("f:img") & entry) {
                        int size = Integer.parseInt(attributes.getValue("height"));
                        if (size == 75)
                            current.linkSmall = attributes.getValue("href");
                        if (size >= 500 && size <= 1600)
                            current.linkBig = attributes.getValue("href");
                    }


                }

                public void endElement(String uri, String localName,
                                       String qName) throws SAXException {
                    if (qName.equalsIgnoreCase("entry") || qName.equalsIgnoreCase("item")) {
                        list.add(current);
                        entry = false;
                    }

                }

                public void characters(char ch[], int start, int length) throws SAXException {
                    if (title) {
                        // Log.d("title : ", new String(ch, start, length));
                        current.title = new String(ch, start, length);
                        title = false;
                    }


                }

            };
            saxParser.parse(is, handler);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


}
