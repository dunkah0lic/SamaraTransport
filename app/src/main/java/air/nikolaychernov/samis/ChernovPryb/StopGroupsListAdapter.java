package air.nikolaychernov.samis.ChernovPryb;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
        // используем созданные, но не используемые view
        // Log.appendLog("StopGroupsListAdapter getView 0");
        View view = convertView;
        if (view == null) {
            // получаем LayoutInflater для работы с layout-ресурсами
            view = lInflater.inflate(R.layout.stopslist, parent, false);
        }

        // Log.appendLog("StopGroupsListAdapter getView 1");
        StopGroup p = getItem(position);
        ToggleButton btn = ((ToggleButton) view.findViewById(R.id.toggleAddToFavor));

        // заполняем View в пункте списка данными
        btn.setOnClickListener(this);
        btn.setTag(position);
        btn.setChecked(DataController.getInstance().isInFavor(p.KS_IDs));
        ((RelativeLayout) view.findViewById(R.id.relLayoutStopIcon)).setVisibility(View.VISIBLE);
        // Log.appendLog("StopGroupsListAdapter getView 2");
        TextView tv = (TextView) view.findViewById(R.id.txtDirectionStopName);
        tv.setText(p.title);
        //dataMan.setTypeface(tv, Typeface.BOLD);
        tv = (TextView) view.findViewById(R.id.txtDirectionStreet);
        tv.setText(p.adjacentStreet);
        //dataMan.setTypeface(tv, Typeface.NORMAL);

        // View lastView = view.findViewById(R.id.imgDirectionSeparator);
        // TextView tv;

        // ((TextView)
        // view.findViewById(R.id.txtDirectionCommercial)).setText(""
        // + p.busesCommercial);

        // Log.appendLog("StopGroupsListAdapter getView 3");
        tv = (TextView) view.findViewById(R.id.txtDirectionCommercial);
        tv.setText(p.busesCommercial);
        //dataMan.setTypeface(tv, HelveticaFont.Light);
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

        // Log.appendLog("StopGroupsListAdapter getView 10");
        // int dist = (int) Math.floor(dataMan.getDistTo(dataMan
        // .getStop(p.KS_IDs[0]),false));
        int dist = (int) Math.floor(dataMan.getDistTo(p.latitude, p.longitude, false));
        tv = (TextView) view.findViewById(R.id.txtDirectionDistance);
        tv.setText(dist > 0 ?  dist + " м" : "");
        //dataMan.setTypeface(tv, Typeface.ITALIC);
        // Log.appendLog("StopGroupsListAdapter getView END");
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
