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
public class RouteXmlParser {

    private static final String ns = null;

    public ArrayList<Route> parse(String in) throws XmlPullParserException, IOException {
        return parse(new StringReader(in));
    }

    public RouteXmlParser() {
    }

    public ArrayList<Route> parse(Reader in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in);
            parser.nextTag();
            return readRoutes(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<Route> readRoutes(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Route> result = new ArrayList<Route>();
        parser.require(XmlPullParser.START_TAG, ns, "routes");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Route tmp;
            // Starts by looking for the entry tag
            if (name.equals("route")) {
                tmp = readRoute(parser);
                    result.add(tmp);
            } else {
                skip(parser);
            }
        }
        return result;
    }

    private Route readRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
        Route result = new Route();
        result.direction = "";
        parser.require(XmlPullParser.START_TAG, ns, "route");

        String tmp = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String data = readStringTag(parser, name);

            if (name.equals("number")) {
                result.number = data;
            } else if (name.equals("transportTypeID")) {
                result.transportTypeID = Integer.parseInt(data);
            } else if (name.equals("affiliationID")) {
                result.affiliationID = Integer.parseInt(data);
            } else if (name.equals("direction")) {
                result.direction = data;
            }
            else if (name.equals("KR_ID")) {
                result.KR_ID = (Integer.parseInt(data));
                //Log.v("TAG5", " msg + " + DataController.getInstance().getRoute(result.KR_ID));
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

