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
public class RouteStopBindXmlParser {

    private static final String ns = null;

    public ArrayList<RouteStopBind> parse(String in) throws XmlPullParserException, IOException {
        return parse(new StringReader(in));
    }

    public RouteStopBindXmlParser() {
    }

    public ArrayList<RouteStopBind> parse(Reader in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in);
            parser.nextTag();
            return readRouteStopBinds(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<RouteStopBind> readRouteStopBinds(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<RouteStopBind> result = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, "routes");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            ArrayList<RouteStopBind> tmp;
            // Starts by looking for the entry tag
            if (name.equals("route")) {
                tmp = readRoute(parser);
                result.addAll(tmp);
            } else {
                skip(parser);
            }
        }
        return result;
    }

    private ArrayList<RouteStopBind> readRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<RouteStopBind> result = new ArrayList<>();
        RouteStopBind temp = new RouteStopBind();
        parser.require(XmlPullParser.START_TAG, ns, "route");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("stop")) {
                temp.setKS_ID(readStop(parser));
                result.add(temp);
            } else if (name.equals("KR_ID")) {
                String data = readStringTag(parser, name);
                temp.setKR_ID(Integer.parseInt(data));
            } else {
                skip(parser);
            }
        }
        return result;
    }

    private int readStop(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "stop");

        int result = 0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String data = readStringTag(parser, name);

            if (name.equals("KS_ID")) {
                result = Integer.parseInt(data);
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

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        int a = parser.getEventType();
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