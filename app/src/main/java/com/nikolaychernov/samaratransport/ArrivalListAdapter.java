package com.nikolaychernov.samaratransport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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

    Activity activity;
    LayoutInflater lInflater;
    ArrayList<ArrivalInfo> objects;
    private DataController dataMan;

    public ArrivalListAdapter(Activity activity, ArrayList<ArrivalInfo> arrivalInfo) {
        //Log.appendLog("ArrivalListAdapter constructor");
        this.activity = activity;
        objects = arrivalInfo;
        lInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        final View view1 = view;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int KR_ID = objects.get(pos).getKR_ID();
                Log.d("ArrivalListAdapter", "btnOnClick " + KR_ID);

                Intent intent = new Intent(activity, MapsActivity.class);
                intent.putExtra("KR_ID", KR_ID);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
                boolean enableAnimations = sharedPref.getBoolean("enableAnimations", false);
                if (enableAnimations && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    View sharedView = view1.findViewById(R.id.txtArrivalListRoute);
                    String transitionName = "routeName";
                    Pair<View, String> p1 = Pair.create(sharedView, transitionName);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1);
                    activity.startActivity(intent, options.toBundle());

                } else {
                    activity.startActivity(intent);
                }
                //cont.startActivity(intent);
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
