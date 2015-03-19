package air.nikolaychernov.samis.ChernovPryb;

import java.io.Serializable;

public class Route implements Serializable, Comparable<Route>, Cloneable {

    public int KR_ID;
    public String number;
    public int transportTypeID;
    public int affiliationID;
    public String direction;
    public String directionEn;
    public String affiliation;
    public String transportType;

    @Override
    public int compareTo(Route another) {
        // TODO Auto-generated method stub
        return new routeComparator().compare(number, another.number);
    }

}
