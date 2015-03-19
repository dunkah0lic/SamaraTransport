package air.nikolaychernov.samis.ChernovPryb;

public class CoordUtils {

    public static double EARTH_RADIUS = 40075.696;

    private static double LATITUDE_TO_KM = 111.11;
    private static double LONGITUDE_SECOND_TO_M = 30.922604938271604938271604938272;

    public static double kilometersInLatitudeDegree() {
        return LATITUDE_TO_KM;
    }

    public static double kilometersInLongitudeDegree(double latitude) {
        return LONGITUDE_SECOND_TO_M * Math.cos(getRadians(latitude)) * 3.6;
    }

    public static double metersInLatitudeDegree() {
        return LATITUDE_TO_KM * 1000;
    }

    public static double metersInLongitudeDegree(double latitude) {
        // return LATITUDE_SECOND_TO_M * Math.cos(getRadians(latitude)) * 3600;
        return kilometersInLongitudeDegree(latitude) * 1000;
    }

    private static double getRadians(double degrees) {
        return degrees * Math.PI / 180;
    }
}

//public class CoordUtils {
//	public static double EARTH_RADIUS = 40075.696;
//
//	private static double LONGITUDE_TO_KM = 111.11;
//	private static double LATITUDE_SECOND_TO_M = 30.922604938271604938271604938272;
//
//	public static double kilometersInLongitudeDegree() {
//		return LONGITUDE_TO_KM;
//	}
//
//	public static double kilometersInLatitudeDegree(double latitude) {
//		return LATITUDE_SECOND_TO_M * Math.cos(getRadians(latitude)) * 3.6;
//	}
//
//	public static double metersInLongitudeDegree() {
//		return LONGITUDE_TO_KM * 1000;
//	}
//
//	public static double metersInLatitudeDegree(double latitude) {
//		// return LATITUDE_SECOND_TO_M * Math.cos(getRadians(latitude)) * 3600;
//		return kilometersInLatitudeDegree(latitude) * 1000;
//	}
//
//	private static double getRadians(double degrees) {
//		return degrees * Math.PI / 180;
//	}
//}