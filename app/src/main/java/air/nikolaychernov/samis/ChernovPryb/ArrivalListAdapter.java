package air.nikolaychernov.samis.ChernovPryb;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ArrivalListAdapter extends BaseAdapter {

    Context cont;
    LayoutInflater lInflater;
    ArrayList<ArrivalInfo> objects;
    private DataController dataMan;

    // Map<Integer, Stop> stops;

    public ArrivalListAdapter(Context context, ArrayList<ArrivalInfo> arrivalInfo) {
        //Log.appendLog("ArrivalListAdapter constructor");
        cont = context;
        // stops=stopsDB;
        objects = arrivalInfo;
        lInflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dataMan = DataController.getInstance();
    }

    public int getCount() {
        // return stops.size();
        return objects.size();
    }

    public ArrivalInfo getItem(int position) {
        // return stops.get(position);
        return objects.get(position);
    }

    public long getItemId(int position) {
        return objects.get(position).getKR_ID();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        //Log.appendLog("ArrivalListAdapter getView 0");
        View view = convertView;
        if (view == null) {
            // получаем LayoutInflater для работы с layout-ресурсами
            view = lInflater.inflate(R.layout.arrival_list, parent, false);
        }

        ArrivalInfo p = getItem(position);

        //Log.appendLog("ArrivalListAdapter getView 1");
        // заполняем View в пункте списка данными
        TextView tv;
        tv = (TextView) view.findViewById(R.id.txtArrivalListPosition);
        tv.setText(p.position);
        //dataMan.setTypeface(tv, Typeface.NORMAL);

        //Log.appendLog("ArrivalListAdapter getView 2");
        tv = (TextView) view.findViewById(R.id.txtArrivalListRoute);
        tv.setText(p.routeDesc);
        dataMan.setTypeface(tv, Typeface.BOLD);

        //Log.appendLog("ArrivalListAdapter getView 3");
        tv = (TextView) view.findViewById(R.id.txtArrivalListTime);
        tv.setText(String.valueOf(p.time));
        //dataMan.setTypeface(tv, Typeface.NORMAL);

        //Log.appendLog("ArrivalListAdapter getView 4");
        tv = (TextView) view.findViewById(R.id.txtArrivalListMinutesLabel);
        //dataMan.setTypeface(tv, HelveticaFont.Light);

        //Log.appendLog("ArrivalListAdapter getView 5");
        tv = (TextView) view.findViewById(R.id.txtArrivalListVehicle);
        tv.setText(p.model + " | " + p.vehicleID);
        dataMan.setTypeface(tv, Typeface.ITALIC);

        //Log.appendLog("ArrivalListAdapter getView 6");
        int transType = p.typeID; //DataController.getInstance().getTransType(p.getKR_ID());

        if (transType == 0) {
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListBusIcon)).setVisibility(View.INVISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListTramIcon)).setVisibility(View.INVISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListTrollIcon)).setVisibility(View.INVISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListCommBusIcon)).setVisibility(View.VISIBLE);
        } else if (transType == 1) {
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListBusIcon)).setVisibility(View.VISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListTramIcon)).setVisibility(View.INVISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListTrollIcon)).setVisibility(View.INVISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListCommBusIcon)).setVisibility(View.INVISIBLE);
        } else if (transType == 3) {
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListBusIcon)).setVisibility(View.INVISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListTramIcon)).setVisibility(View.VISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListTrollIcon)).setVisibility(View.INVISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListCommBusIcon)).setVisibility(View.INVISIBLE);
        } else if (transType == 4) {
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListBusIcon)).setVisibility(View.INVISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListTramIcon)).setVisibility(View.INVISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListTrollIcon)).setVisibility(View.VISIBLE);
            ((RelativeLayout) view.findViewById(R.id.relLayoutArrivalListCommBusIcon)).setVisibility(View.INVISIBLE);
        }

        //Log.appendLog("ArrivalListAdapter getView END");
        return view;
    }

}
