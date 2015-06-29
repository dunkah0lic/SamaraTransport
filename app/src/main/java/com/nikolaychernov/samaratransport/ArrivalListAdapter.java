package com.nikolaychernov.samaratransport;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ArrivalListAdapter extends BaseAdapter {

    Context cont;
    LayoutInflater lInflater;
    ArrayList<ArrivalInfo> objects;
    private DataController dataMan;

    public ArrivalListAdapter(Context context, ArrayList<ArrivalInfo> arrivalInfo) {
        //Log.appendLog("ArrivalListAdapter constructor");
        cont = context;
        objects = arrivalInfo;
        lInflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dataMan = DataController.getInstance();
    }

    public int getCount() {
        // return stops.size();
        return objects.size();
    }

    public ArrivalInfo getItem(int position) {
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
        final int pos = position;
        ArrivalInfo p = getItem(position);
        Button btn = (Button) view.findViewById(R.id.btnShowRoute);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int KR_ID = objects.get(pos).getKR_ID();
                Log.d("ArrivalListAdapter", "btnOnClick " + KR_ID);

                Intent intent = new Intent(cont, MapsActivity.class);
                intent.putExtra("KR_ID", KR_ID);
                cont.startActivity(intent);
            }
        });

        //Log.appendLog("ArrivalListAdapter getView 1");
        // заполняем View в пункте списка данными
        TextView tv;
        tv = (TextView) view.findViewById(R.id.txtArrivalListPosition);
        tv.setText(p.position);
        //dataMan.setTypeface(tv, Typeface.NORMAL);

        //Log.appendLog("ArrivalListAdapter getView 2");
        tv = (TextView) view.findViewById(R.id.txtArrivalListRoute);
        tv.setText(p.routeDesc);


        //Log.appendLog("ArrivalListAdapter getView 3");
        tv = (TextView) view.findViewById(R.id.txtArrivalListTime);
        tv.setText(String.valueOf(p.time));
        //dataMan.setTypeface(tv, Typeface.NORMAL);

        tv = (TextView) view.findViewById(R.id.txtArrivalListVehicle);
        tv.setText(p.model + " | " + p.vehicleID);

        int transType = p.typeID;

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

        return view;
    }



}
