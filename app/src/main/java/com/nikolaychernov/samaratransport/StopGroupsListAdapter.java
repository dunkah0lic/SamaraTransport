package com.nikolaychernov.samaratransport;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class StopGroupsListAdapter extends BaseAdapter implements OnClickListener {

    DataController dataMan;
    LayoutInflater lInflater;
    StopGroup[] objects;
    Resources res;

    public StopGroupsListAdapter(Context context, StopGroup[] stopsDB) {
        // Log.appendLog("StopGroupsListAdapter constructor");
        this.dataMan = DataController.getInstance();
        objects = stopsDB;
        res = context.getResources();
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return objects != null ? objects.length : 0;
    }

    public StopGroup getItem(int position) {
        return objects != null ? objects[position] : null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.stopslist, parent, false);
        }


        StopGroup p = getItem(position);
        ToggleButton btn = ((ToggleButton) view.findViewById(R.id.toggleAddToFavor));

        // заполняем View в пункте списка данными
        btn.setOnClickListener(this);
        btn.setTag(position);
        btn.setChecked(DataController.getInstance().isInFavor(p.KS_IDs));
        view.findViewById(R.id.relLayoutStopIcon).setVisibility(View.VISIBLE);

        TextView tv = (TextView) view.findViewById(R.id.txtDirectionStopName);
        tv.setText(p.title);
        tv = (TextView) view.findViewById(R.id.txtDirectionStreet);
        tv.setText(p.adjacentStreet);

        tv = (TextView) view.findViewById(R.id.txtDirectionCommercial);
        tv.setText(p.busesCommercial);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            view.findViewById(R.id.ImgCommBus).setVisibility(View.INVISIBLE);
        } else {
            tv.setMaxHeight(1000);
            view.findViewById(R.id.ImgCommBus).setVisibility(View.VISIBLE);
        }

        tv = (TextView) view.findViewById(R.id.txtDirectionMunicipal);
        tv.setText(p.busesMunicipal);

        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            view.findViewById(R.id.imgBusIcon).setVisibility(View.INVISIBLE);
        } else {
            tv.setMaxHeight(1000);
            view.findViewById(R.id.imgBusIcon).setVisibility(View.VISIBLE);
        }


        tv = (TextView) view.findViewById(R.id.txtDirectionPrigorod);
        tv.setText(p.busesPrigorod);
        if (tv.getText().length() == 0) {
            ((ImageView) view.findViewById(R.id.imgPrigorodBus)).setVisibility(View.INVISIBLE);
            tv.setMaxHeight(0);
        } else {
            tv.setMaxHeight(1000);
            ((ImageView) view.findViewById(R.id.imgPrigorodBus)).setVisibility(View.VISIBLE);
        }

        tv = (TextView) view.findViewById(R.id.txtDirectionSeason);
        tv.setText(p.busesSeason);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            ((ImageView) view.findViewById(R.id.imgSeasonBus)).setVisibility(View.INVISIBLE);
        } else {
            tv.setMaxHeight(1000);
            ((ImageView) view.findViewById(R.id.imgSeasonBus)).setVisibility(View.VISIBLE);
        }

        tv = (TextView) view.findViewById(R.id.txtDirectionSpecial);
        tv.setText(p.busesSpecial);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            ((ImageView) view.findViewById(R.id.imgSpecialBus)).setVisibility(View.INVISIBLE);
        } else {
            tv.setMaxHeight(1000);
             view.findViewById(R.id.imgSpecialBus).setVisibility(View.VISIBLE);
        }

        tv = (TextView) view.findViewById(R.id.txtDirectionTrams);
        tv.setText(p.trams);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            ((ImageView) view.findViewById(R.id.imgTram)).setVisibility(View.INVISIBLE);
        } else {
            tv.setMaxHeight(1000);
            ((ImageView) view.findViewById(R.id.imgTram)).setVisibility(View.VISIBLE);
        }

        tv = (TextView) view.findViewById(R.id.txtDirectionTrolls);
        tv.setText(p.trolleybuses);
        if (tv.getText().length() == 0) {
            ((ImageView) view.findViewById(R.id.imgTroll)).setVisibility(View.INVISIBLE);
            tv.setMaxHeight(0);
        } else {
            tv.setMaxHeight(1000);
            ((ImageView) view.findViewById(R.id.imgTroll)).setVisibility(View.VISIBLE);
        }

        int dist = (int) Math.floor(dataMan.getDistTo(p.latitude, p.longitude, false));
        tv = (TextView) view.findViewById(R.id.txtDirectionDistance);
        tv.setText(dist > 0 ?  dist + " м" : "");
        return view;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // Log.appendLog("StopGroupsListAdapter onClick");
        StopGroup sg = getItem((Integer) v.getTag());
        dataMan.setFavor(sg.KS_IDs, ((ToggleButton) v).isChecked());
    }

}
