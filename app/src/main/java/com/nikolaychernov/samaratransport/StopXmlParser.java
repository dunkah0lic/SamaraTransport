package com.nikolaychernov.samaratransport;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Nikolay on 22.07.2015.
 */
public class StopXmlParser {
    private static final String ns = null;

    public ArrayList<Stop> parse(String in) throws XmlPullParserException, IOException {
        return parse(new StringReader(in));
    }

    public StopXmlParser() {
    }

    public ArrayList<Stop> parse(Reader in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in);
            parser.nextTag();
            return readStops(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<Stop> readStops(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Stop> result = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "stops");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Stop tmp;
            // Starts by looking for the entry tag
            if (name.equals("stop")) {
                tmp = readStop(parser);
                result.add(tmp);
            } else {
                skip(parser);
            }
        }
        return result;
    }

    private Stop readStop(XmlPullParser parser) throws XmlPullParserException, IOException {
        Stop result = new Stop();
        result.direction = "";
        parser.require(XmlPullParser.START_TAG, ns, "stop");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String data = readStringTag(parser, name);

            if (name.equals("title")) {
                result.title = data;
                result.titleLowcase = data.toLowerCase();
            } else if (name.equals("adjacentStreet")) {
                result.adjacentStreet = data;
            } else if (name.equals("direction")) {
                result.direction = data;
            } else if (name.equals("busesMunicipal")) {
                result.busesMunicipal = data;
            } else if (name.equals("busesCommercial")) {
                result.busesCommercial = data;
            } else if (name.equals("busesPrigorod")) {
                result.busesPrigorod = data;
            } else if (name.equals("busesSeason")) {
                result.busesSeason = data;
            } else if (name.equals("busesSpecial")) {
                result.busesSpecial = data;
            } else if (name.equals("trams")) {
                result.trams = data;
            } else if (name.equals("trolleybuses")) {
                result.trolleybuses = data;
            } else if (name.equals("latitude")) {
                result.latitude = Double.parseDouble(data);
            } else if (name.equals("longitude")) {
                result.longitude = Double.parseDouble(data);
            }
            //TODO metros

            else if (name.equals("KS_ID")) {
                result.KS_ID = (Integer.parseInt(data));
                //Log.v("TAG5", " msg + " + DataController.getInstance().getStop(result.KS_ID));
            }
        }
        return result;
    }

    private String readStringTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return result;
    }

	/*
     * private Integer readIntTag(XmlPullParser parser, String tagName) throws
	 * IOException, XmlPullParserException {
	 * parser.require(XmlPullParser.START_TAG, ns, tagName); Integer result =
	 * Integer.parseInt(readText(parser)); parser.require(XmlPullParser.END_TAG,
	 * ns, tagName); return result; }
	 */

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}