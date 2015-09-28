package com.nikolaychernov.samaratransport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DirectionListAdapter extends BaseAdapter implements OnClickListener {

    LayoutInflater lInflater;
    Stop[] grp;
    private DataController dataMan;

    // Map<Integer, Stop> stops;

    public DirectionListAdapter(Context context, Stop[] grp) {
        // Log.appendLog("DirectionListAdapter constructor");
        this.grp = grp;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dataMan = DataController.getInstance();
    }

    public int getCount() {
        // return stops.size();
        return grp.length;
    }

    public Stop getItem(int position) {
        return grp[position];
    }

    public long getItemId(int position) {
        return grp[position].KS_ID;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Log.appendLog("DirectionListAdapter getView 0");
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.directionstoplist, parent, false);
        }

        // Log.appendLog("DirectionListAdapter getView 1");
        Stop p = getItem(position);
        ToggleButton btn = ((ToggleButton) view.findViewById(R.id.toggleAddToFavor));
        btn.setOnClickListener(this);
        btn.setTag(position);
        btn.setChecked(DataController.getInstance().isInFavor(p.KS_ID));

        TextView tv = (TextView) view.findViewById(R.id.txtDirectionStopName);
        tv.setText(p.direction);

        ((TextView) view.findViewById(R.id.txtDirectionStreet)).setMaxHeight(0);
        view.findViewById(R.id.relLayoutDirectionsListDirectionIcon).setVisibility(View.VISIBLE);

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
             view.findViewById(R.id.imgPrigorodBus).setVisibility(View.INVISIBLE);
            tv.setMaxHeight(0);
        } else {
            tv.setMaxHeight(1000);
             view.findViewById(R.id.imgPrigorodBus).setVisibility(View.VISIBLE);
        }

        tv = (TextView) view.findViewById(R.id.txtDirectionSeason);
        tv.setText(p.busesSeason);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            view.findViewById(R.id.imgSeasonBus).setVisibility(View.INVISIBLE);
        } else {
            tv.setMaxHeight(1000);
            view.findViewById(R.id.imgSeasonBus).setVisibility(View.VISIBLE);
        }

        tv = (TextView) view.findViewById(R.id.txtDirectionSpecial);
        tv.setText(p.busesSpecial);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            view.findViewById(R.id.imgSpecialBus).setVisibility(View.INVISIBLE);
        } else {
            tv.setMaxHeight(1000);
            view.findViewById(R.id.imgSpecialBus).setVisibility(View.VISIBLE);
        }

        tv = (TextView) view.findViewById(R.id.txtDirectionTrams);
        tv.setText(p.trams);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            view.findViewById(R.id.imgTram).setVisibility(View.INVISIBLE);
        } else {
            tv.setMaxHeight(1000);
            view.findViewById(R.id.imgTram).setVisibility(View.VISIBLE);
        }

        tv = (TextView) view.findViewById(R.id.txtDirectionTrolls);
        tv.setText(p.trolleybuses);
        if (tv.getText().length() == 0) {
             view.findViewById(R.id.imgTroll).setVisibility(View.INVISIBLE);
            tv.setMaxHeight(0);
        } else {
            tv.setMaxHeight(1000);
            view.findViewById(R.id.imgTroll).setVisibility(View.VISIBLE);
        }
        
        tv = (TextView) view.findViewById(R.id.txtDirectionDistance);
        int dist = (int) Math.floor(DataController.getInstance().getDistTo(p, false));
        tv.setText(dist > 0 ?  dist + " м" : "");
        return view;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        ToggleButton btn = (ToggleButton) v;
        int index = getItem((Integer) (btn).getTag()).KS_ID;
        DataController.getInstance().setFavor(index, btn.isChecked());
    }

}
