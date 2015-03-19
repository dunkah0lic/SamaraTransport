package air.nikolaychernov.samis.ChernovPryb;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

public class DirectionSelectActivity extends Activity {

    private StopGroup grp;

    // private Stop[] stops;

    // @Override
    // public void onSaveInstanceState(Bundle savedInstanceState) {
    // savedInstanceState.putSerializable("dm", DataController.getInstance());
    //
    // super.onSaveInstanceState(savedInstanceState);
    // }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Log.appendLog("DirectionSelectActivity onCreate");
        // Check whether we're recreating a previously destroyed instance
        // if (savedInstanceState != null) {
        // // Restore value of members from saved state
        // DataController.setInstance((DataController) savedInstanceState
        // .getSerializable("dm"));
        // }

        setContentView(R.layout.activity_direction_select);
        grp = (StopGroup) getIntent().getSerializableExtra(StopSearchActivity.MESSAGE_STOPGROUP);
        // stops = ((DataController) getIntent().getSerializableExtra(
        // StopSearchActivity.MESSAGE_DATAMAN)).getStops(grp.KS_IDs);

        DataController dataMan = null;
        try {
            dataMan = DataController.getInstance();
        } catch (NullPointerException ex) {
            Intent mainIntent = new Intent(this, StopSearchActivity.class);
            finish();
            startActivity(mainIntent);
            return;
        }

        ActionBar ab = getActionBar();
        ab.setIcon(null);
        ab.setTitle(grp.title);
        ab.setSubtitle(grp.adjacentStreet);

        TextView tv;
        /*tv = (TextView) findViewById(R.id.lblDirectionSelect);
        dataMan.setTypeface(tv, HelveticaFont.Bold);

        tv = (TextView) findViewById(R.id.txtDirectionStopName);
        tv.setText(grp.title);
        dataMan.setTypeface(tv, HelveticaFont.Bold);

        tv = (TextView) findViewById(R.id.txtDirectionStreet);
        tv.setText(grp.adjacentStreet);
        dataMan.setTypeface(tv, HelveticaFont.Medium);*/

        ListView list = (ListView) findViewById(R.id.directionList);
        DirectionListAdapter adapter = new DirectionListAdapter(this, grp.stops);
        list.setAdapter(adapter); // отображаем все объекты

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                showArrival((int) id);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.arrival_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                cmdSettings_click();
                return true;
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void cmdSettings_click() {
        Log.d("", "DirectionSelectActivity cmdSettings_click");
        Intent intent = new Intent(this, SettingsActivity.class);
        DataController dataMan = DataController.getInstance();
        intent.putExtra("radius", dataMan.getRadius());
        intent.putExtra("updateFlag", dataMan.isAutoUpdate());
        intent.putExtra("showTrams", dataMan.isShowTrams());
        intent.putExtra("showTrolls", dataMan.isShowTrolls());
        intent.putExtra("showBuses", dataMan.isShowBuses());
        intent.putExtra("showComm", dataMan.isShowComm());
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DataController.getInstance().setSettings(data.getIntExtra("radius", 600), data.getBooleanExtra("updateFlag", true), data.getBooleanExtra("showBuses", true), data.getBooleanExtra("showTrolls", true), data.getBooleanExtra("showTrams", true), data.getBooleanExtra("showComm", true));
    }

    public void cmdBack_click(View view) {
        // Log.appendLog("DirectionSelectActivity cmdBack_click");
        this.finish();
    }

    public void showArrival(int KS_ID) {
        // Log.appendLog("DirectionSelectActivity showArrival");
        int ind = Arrays.binarySearch(grp.KS_IDs, KS_ID);
        Intent intent = new Intent(this, ArrivalActivity.class);
        intent.putExtra(StopSearchActivity.MESSAGE_STOP, grp.stops[ind]);
        startActivity(intent);

    }
}
