package air.nikolaychernov.samis.ChernovPryb;

import java.util.Comparator;

public class routeComparator implements Comparator<String> {

    @Override
    public int compare(String arg0, String arg1) {
        // TODO Auto-generated method stub
        try {
            Integer n0 = Integer.parseInt(arg0);
            Integer n1 = Integer.parseInt(arg1);
            return n0.compareTo(n1);
        } catch (NumberFormatException e) {
            return arg0.compareTo(arg1);
        }
    }
}
