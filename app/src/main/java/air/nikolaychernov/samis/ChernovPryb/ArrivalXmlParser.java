package air.nikolaychernov.samis.ChernovPryb;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;

public class ArrivalXmlParser {

    // We don't use namespaces
    private static final String ns = null;
    private Set<Integer> transTypesToShow;

    public ArrayList<ArrivalInfo> parse(String in) throws XmlPullParserException, IOException {
        return parse(new StringReader(in));
    }

    public ArrivalXmlParser(Set<Integer> transTypesToShow) {
        this.transTypesToShow = transTypesToShow;
    }

    public ArrayList<ArrivalInfo> parse(Reader in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in);
            parser.nextTag();
            return readArrival(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<ArrivalInfo> readArrival(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<ArrivalInfo> result = new ArrayList<ArrivalInfo>();
        parser.require(XmlPullParser.START_TAG, ns, "arrival");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            ArrivalInfo tmp;
            // Starts by looking for the entry tag
            if (name.equals("transport")) {
                tmp = readTransport(parser);
                if (transTypesToShow.contains(tmp.typeID)) {
                    result.add(tmp);
                }
            } else {
                skip(parser);
            }
        }
        return result;
    }

    private ArrivalInfo readTransport(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrivalInfo result = new ArrivalInfo();
        result.routeDesc = "";
        parser.require(XmlPullParser.START_TAG, ns, "transport");

        String tmp = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            String data = readStringTag(parser, name);

            if (name.equals("number")) {
                result.routeDesc = data + result.routeDesc;
            } else if (name.equals("time")) {
                result.time = Integer.parseInt(data);
            } else if (name.equals("stateNumber")) {
                result.vehicleID = data;
            } else if (name.equals("modelTitle")) {
                result.model = data;
            } else if (name.equals("remainingLength")) {
                result.remainingLength = (int) Double.parseDouble(data);
            } else if (name.equals("nextStopName")) {
                result.nextStopName = data;
            } else if (name.equals("nextStopId")) {
                result.KS_ID = Integer.parseInt(data);
            }
            // else if (name.equals("type")) {
            // if (data.equals("Автобус")) {
            // result.typeID = 1;
            // } else if (data.equals("Троллейбус")) {
            // result.typeID = 4;
            // }else if (data.equals("Трамвай")) {
            // result.typeID = 3;
            // }
            // }
            else if (name.equals("KR_ID")) {
                result.setKR_ID(Integer.parseInt(data));
                Log.v("TAG5", " msg + " + DataController.getInstance().getRoute(result.getKR_ID()));
                if (DataController.getInstance().getRoute(result.getKR_ID())!=null) {
                    result.routeDesc += ": → " + DataController.getInstance().getRoute(result.getKR_ID()).direction;
                } else {
                    result.routeDesc += ": → ";
                }
            }
        }

        result.position = result.remainingLength + " м до остановки " + result.nextStopName;
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
