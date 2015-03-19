package air.nikolaychernov.samis.ChernovPryb;

import java.io.Serializable;

public class Stop implements Serializable, Comparable<Stop> {

    public int KS_ID;
    public String title;
    public String titleLowcase;
    public String adjacentStreet;
    public String direction;
    public String titleEn;
    public String titleEnLowcase;
    public String adjacentStreetEn;
    public String directionEn;
    public String busesMunicipal;
    public String busesCommercial;
    public String busesPrigorod;
    public String busesSeason;
    public String busesSpecial;
    public String trams;
    public String trolleybuses;
    public String metros;
    public double latitude;
    public double longitude;
    public String geoportalID;

    @Override
    public int compareTo(Stop another) {
        // TODO Auto-generated method stub
        int main = adjacentStreet.compareToIgnoreCase(another.adjacentStreet);
        int slave = title.compareToIgnoreCase(another.title);
        return main != 0 ? main : slave;
    }
}
