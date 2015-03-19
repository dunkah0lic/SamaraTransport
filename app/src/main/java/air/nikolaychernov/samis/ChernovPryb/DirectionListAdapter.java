package air.nikolaychernov.samis.ChernovPryb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
            // РїРѕР»СѓС‡Р°РµРј LayoutInflater РґР»СЏ СЂР°Р±РѕС‚С‹ СЃ
            // layout-СЂРµСЃСѓСЂСЃР°РјРё
            view = lInflater.inflate(R.layout.stopslist, parent, false);
        }

        // Log.appendLog("DirectionListAdapter getView 1");
        Stop p = getItem(position);
        ToggleButton btn = ((ToggleButton) view.findViewById(R.id.toggleAddToFavor));

        // Р·Р°РїРѕР»РЅСЏРµРј View РІ РїСѓРЅРєС‚Рµ СЃРїРёСЃРєР° РґР°РЅРЅС‹РјРё
        btn.setOnClickListener(this);
        btn.setTag(position);
        btn.setChecked(DataController.getInstance().isInFavor(p.KS_ID));
        // Р·Р°РїРѕР»РЅСЏРµРј View РІ РїСѓРЅРєС‚Рµ СЃРїРёСЃРєР° РґР°РЅРЅС‹РјРё
        // Log.appendLog("DirectionListAdapter getView 2");
        TextView tv = (TextView) view.findViewById(R.id.txtDirectionStopName);
        tv.setText(p.direction);
        //ataMan.setTypeface(tv, Typeface.BOLD);
        // ((TextView) view.findViewById(R.id.txtDirectionStreet)).setText("");
        ((TextView) view.findViewById(R.id.txtDirectionStreet)).setMaxHeight(0);
        ((RelativeLayout) view.findViewById(R.id.relLayoutDirectionsListDirectionIcon)).setVisibility(View.VISIBLE);

        // Log.appendLog("StopGroupsListAdapter getView 3");
        tv = (TextView) view.findViewById(R.id.txtDirectionCommercial);
        tv.setText(p.busesCommercial);
       // dataMan.setTypeface(tv, HelveticaFont.Light);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            // ((ImageView) view.findViewById(R.id.ImgCommBus)).setMaxHeight(0);
            ((ImageView) view.findViewById(R.id.ImgCommBus)).setVisibility(View.INVISIBLE);
            // tv.setPadding(0, 0, 0, 0);
        } else {
            tv.setMaxHeight(1000);
            // ((ImageView)
            // view.findViewById(R.id.ImgCommBus)).setMaxHeight(100);
            ((ImageView) view.findViewById(R.id.ImgCommBus)).setVisibility(View.VISIBLE);
            // tv.setPadding(0, 0, 0, 4);
        }

        // Log.appendLog("StopGroupsListAdapter getView 4");
        tv = (TextView) view.findViewById(R.id.txtDirectionMunicipal);
        tv.setText(p.busesMunicipal);
        //dataMan.setTypeface(tv, HelveticaFont.Light);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            // ((ImageView) view.findViewById(R.id.imgBusIcon)).setMaxHeight(0);
            ((ImageView) view.findViewById(R.id.imgBusIcon)).setVisibility(View.INVISIBLE);
            // tv.setPadding(0, 0, 0, 0);
        } else {
            tv.setMaxHeight(1000);
            // ((ImageView)
            // view.findViewById(R.id.imgBusIcon)).setMaxHeight(100);
            ((ImageView) view.findViewById(R.id.imgBusIcon)).setVisibility(View.VISIBLE);
            // tv.setPadding(0, 0, 0, 4);
        }

        // Log.appendLog("StopGroupsListAdapter getView 5");
        tv = (TextView) view.findViewById(R.id.txtDirectionPrigorod);
        tv.setText(p.busesPrigorod);
        //dataMan.setTypeface(tv, HelveticaFont.Light);
        if (tv.getText().length() == 0) {
            // ((LinearLayout)
            // view.findViewById(R.id.linLayoutRoutesBusPrigorod))
            ((ImageView) view.findViewById(R.id.imgPrigorodBus)).setVisibility(View.INVISIBLE);
            tv.setMaxHeight(0);
            // tv.setPadding(0, 0, 0, 0);
        } else {
            tv.setMaxHeight(1000);
            // ((ImageView) view.findViewById(R.id.imgPrigorodBus))
            // .setMaxHeight(100);
            ((ImageView) view.findViewById(R.id.imgPrigorodBus)).setVisibility(View.VISIBLE);
            // tv.setPadding(0, 0, 0, 4);
        }

        // Log.appendLog("StopGroupsListAdapter getView 6");
        tv = (TextView) view.findViewById(R.id.txtDirectionSeason);
        tv.setText(p.busesSeason);
        //dataMan.setTypeface(tv, HelveticaFont.Light);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            // ((ImageView)
            // view.findViewById(R.id.imgSeasonBus)).setMaxHeight(0);
            ((ImageView) view.findViewById(R.id.imgSeasonBus)).setVisibility(View.INVISIBLE);
            // tv.setPadding(0, 0, 0, 0);
        } else {
            tv.setMaxHeight(1000);
            // ((ImageView) view.findViewById(R.id.imgSeasonBus))
            // .setMaxHeight(100);
            ((ImageView) view.findViewById(R.id.imgSeasonBus)).setVisibility(View.VISIBLE);
            // tv.setPadding(0, 0, 0, 4);
        }

        // Log.appendLog("StopGroupsListAdapter getView 7");
        tv = (TextView) view.findViewById(R.id.txtDirectionSpecial);
        tv.setText(p.busesSpecial);
        //dataMan.setTypeface(tv, HelveticaFont.Light);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            // ((ImageView)
            // view.findViewById(R.id.imgSpecialBus)).setMaxHeight(0);
            ((ImageView) view.findViewById(R.id.imgSpecialBus)).setVisibility(View.INVISIBLE);
            // tv.setPadding(0, 0, 0, 0);
        } else {
            tv.setMaxHeight(1000);
            // ((ImageView) view.findViewById(R.id.imgSpecialBus))
            // .setMaxHeight(100);
            ((ImageView) view.findViewById(R.id.imgSpecialBus)).setVisibility(View.VISIBLE);
            // tv.setPadding(0, 0, 0, 4);
        }

        // Log.appendLog("StopGroupsListAdapter getView 8");
        tv = (TextView) view.findViewById(R.id.txtDirectionTrams);
        tv.setText(p.trams);
        //dataMan.setTypeface(tv, HelveticaFont.Light);
        if (tv.getText().length() == 0) {
            tv.setMaxHeight(0);
            // ((ImageView) view.findViewById(R.id.imgTram)).setMaxHeight(0);
            ((ImageView) view.findViewById(R.id.imgTram)).setVisibility(View.INVISIBLE);
            // tv.setPadding(0, 0, 0, 0);
        } else {
            tv.setMaxHeight(1000);
            // ((ImageView) view.findViewById(R.id.imgTram)).setMaxHeight(100);
            ((ImageView) view.findViewById(R.id.imgTram)).setVisibility(View.VISIBLE);
            // tv.setPadding(0, 0, 0, 4);
        }

        // Log.appendLog("StopGroupsListAdapter getView 9");
        tv = (TextView) view.findViewById(R.id.txtDirectionTrolls);
        tv.setText(p.trolleybuses);
        //dataMan.setTypeface(tv, HelveticaFont.Light);
        if (tv.getText().length() == 0) {
            // ((ImageView) view.findViewById(R.id.imgTroll)).setMaxHeight(0);
            ((ImageView) view.findViewById(R.id.imgTroll)).setVisibility(View.INVISIBLE);
            tv.setMaxHeight(0);
        } else {
            tv.setMaxHeight(1000);
            // ((ImageView) view.findViewById(R.id.imgTroll)).setMaxHeight(100);
            ((ImageView) view.findViewById(R.id.imgTroll)).setVisibility(View.VISIBLE);
            // tv.setPadding(0, 0, 0, 4);
        }
        // tv = (TextView) view.findViewById(R.id.txtDirectionCommercial);
        // tv.setText(p.busesCommercial);
        // dataMan.setTypeface(tv, HelveticaFont.Light);
        // if (tv.getText().length() == 0) {
        // tv.setMaxHeight(0);
        // //tv.setPadding(0, 0, 0, 0);
        // }else {
        // tv.setMaxHeight(100);
        // //tv.setPadding(0, 0, 0, 4);
        // }
        //
        // //Log.appendLog("DirectionListAdapter getView 4");
        // tv = (TextView) view.findViewById(R.id.txtDirectionMunicipal);
        // tv.setText(p.busesMunicipal);
        // dataMan.setTypeface(tv, HelveticaFont.Light);
        // if (tv.getText().length() == 0) {
        // tv.setMaxHeight(0);
        // //tv.setPadding(0, 0, 0, 0);
        // }else {
        // tv.setMaxHeight(100);
        // //tv.setPadding(0, 0, 0, 4);
        // }
        //
        // //Log.appendLog("DirectionListAdapter getView 5");
        // tv = (TextView) view.findViewById(R.id.txtDirectionPrigorod);
        // tv.setText(p.busesPrigorod);
        // dataMan.setTypeface(tv, HelveticaFont.Light);
        // if (tv.getText().length() == 0) {
        // tv.setMaxHeight(0);
        // //tv.setPadding(0, 0, 0, 0);
        // }else {
        // tv.setMaxHeight(100);
        // //tv.setPadding(0, 0, 0, 4);
        // }
        //
        // //Log.appendLog("DirectionListAdapter getView 6");
        // tv = (TextView) view.findViewById(R.id.txtDirectionSeason);
        // tv.setText(p.busesSeason);
        // dataMan.setTypeface(tv, HelveticaFont.Light);
        // if (tv.getText().length() == 0) {
        // tv.setMaxHeight(0);
        // //tv.setPadding(0, 0, 0, 0);
        // }else {
        // tv.setMaxHeight(100);
        // //tv.setPadding(0, 0, 0, 4);
        // }
        //
        // //Log.appendLog("DirectionListAdapter getView 7");
        // tv = (TextView) view.findViewById(R.id.txtDirectionSpecial);
        // tv.setText(p.busesSpecial);
        // dataMan.setTypeface(tv, HelveticaFont.Light);
        // if (tv.getText().length() == 0) {
        // tv.setMaxHeight(0);
        // //tv.setPadding(0, 0, 0, 0);
        // }else {
        // tv.setMaxHeight(100);
        // //tv.setPadding(0, 0, 0, 4);
        // }
        //
        // //Log.appendLog("DirectionListAdapter getView 8");
        // tv = (TextView) view.findViewById(R.id.txtDirectionTrams);
        // tv.setText(p.trams);
        // dataMan.setTypeface(tv, HelveticaFont.Light);
        // if (tv.getText().length() == 0) {
        // tv.setMaxHeight(0);
        // //tv.setPadding(0, 0, 0, 0);
        // }else {
        // tv.setMaxHeight(100);
        // //tv.setPadding(0, 0, 0, 4);
        // }
        //
        // //Log.appendLog("DirectionListAdapter getView 9");
        // tv = (TextView) view.findViewById(R.id.txtDirectionTrolls);
        // tv.setText(p.trolleybuses);
        // dataMan.setTypeface(tv, HelveticaFont.Light);
        // if (tv.getText().length() == 0) {
        // tv.setMaxHeight(0);
        // }else {
        // tv.setMaxHeight(100);
        // //tv.setPadding(0, 0, 0, 4);
        // }
        // ((TextView) view.findViewById(R.id.txtDirectionMetro)).setText(""
        // + p.metros != "" ? p.metros : " ");

        // Log.appendLog("DirectionListAdapter getView 10");
        tv = (TextView) view.findViewById(R.id.txtDirectionDistance);
        int dist = (int) Math.floor(DataController.getInstance().getDistTo(p, false));
        tv.setText(dist > 0 ?  dist + " м" : "");
        //dataMan.setTypeface(tv, Typeface.ITALIC);

        // Log.appendLog("DirectionListAdapter getView END");
        return view;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // Log.appendLog("DirectionListAdapter onClick");
        ToggleButton btn = (ToggleButton) v;
        int index = getItem((Integer) (btn).getTag()).KS_ID;
        DataController.getInstance().setFavor(index, btn.isChecked());
    }

}
