package air.nikolaychernov.samis.ChernovPryb;

import java.io.Serializable;

public class ArrivalInfo implements Serializable {

    public int KS_ID;
    private int KR_ID;

    public int getKR_ID() {
        return KR_ID;
    }

    public void setKR_ID(int kR_ID) {
        KR_ID = kR_ID;
        typeID = DataController.getInstance().getTransType(kR_ID);
    }

    public int typeID;

    public int time;
    public String position;
    public String routeDesc;
    public String model;
    public String vehicleID;
    public String nextStopName;
    public int remainingLength;
}
