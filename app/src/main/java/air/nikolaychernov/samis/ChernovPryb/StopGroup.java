package air.nikolaychernov.samis.ChernovPryb;

import java.io.Serializable;

public class StopGroup implements Serializable, Comparable<StopGroup> {

    public int[] KS_IDs;
    public Stop[] stops;
    public String title = "";
    public String titleLowcase = "";
    public String adjacentStreet = "";
    public String titleEn = "";
    public String titleEnLowcase = "";
    public String adjacentStreetEn = "";
    public String busesMunicipal = "";
    public String busesCommercial = "";
    public String busesPrigorod = "";
    public String busesSeason = "";
    public String busesSpecial = "";
    public String trams = "";
    public String trolleybuses = "";
    public String metros = "";
    public double latitude;
    public double longitude;
    public double dist;

    @Override
    public int compareTo(StopGroup another) {
        // TODO Auto-generated method stub
        // DataController dataMan = DataController.getInstance();
        // double dist1 = dataMan.getDistTo(latitude, longitude, false);
        // double dist2 = dataMan.getDistTo(another.latitude, another.longitude,
        // false);
        // return Double.compare(dist1, dist2);
        return Double.compare(dist, another.dist);
    }
}
