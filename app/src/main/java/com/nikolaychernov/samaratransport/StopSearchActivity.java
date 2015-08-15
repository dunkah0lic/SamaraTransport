package com.nikolaychernov.samaratransport;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.Serializable;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class StopSearchActivity extends ActionBarActivity implements Serializable {

    private DataController dataMan;
    private StopGroup[] grp;
    private ListView list;

    public final static String MESSAGE_KS_ID = "com.markikokik.transarrival63.KS_ID";
    public final static String MESSAGE_STOP = "com.markikokik.transarrival63.stop";
    public final static String MESSAGE_ARRIVAl_INFO = "com.markikokik.transarrival63.arrivalInfo";
    public final static String MESSAGE_STOPGROUP = "com.markikokik.transarrival63.stopGroup";
    public final static String MESSAGE_DATAMAN = "com.markikokik.transarrival63.dataMan";

    private boolean searchByNav = true;
    private boolean searchInFavor = false;
    private boolean firstStart = true;

    private Menu menu;

    private SearchNearMeTask snmt;

    GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
    Tracker tracker = analytics.newTracker("UA-60775707-2"); // Send hits to tracker id UA-XXXX-Y



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Log.logInit(this);
        Log.d("STOPSEARCHACTIVITY", "StopSearchActivity onCreate");
        setContentView(R.layout.activity_stopsearch);

        dataMan = DataController.getInstance(this);
        snmt = new SearchNearMeTask();

        list = (ListView) findViewById(R.id.list);
        final SwipeRefreshLayout mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setColorSchemeResources(R.color.primary);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchNearMe(false, "refresh");
                mRefreshLayout.setRefreshing(false);
            }
        });
        searchNearMe(false, "refresh");

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setIcon(null);

        handleIntent(getIntent());

        if (!dataMan.navInit()) {
            openLocationSettings();
        }

        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(10) // default 10
                .setRemindInterval(2) // default 1
                .setShowNeutralButton(false) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(StopSearchActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();
        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);


        tracker.setScreenName("StopSeacrhActivity");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("show")
                .setLabel("StopSeacrhActivity")
                .build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.stopsearch_activity_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.d("","handleIntent");
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            SearchByNameTask task = new SearchByNameTask();
            task.execute(query);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_favourite:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("UX")
                        .setAction("click")
                        .setLabel("Favourite")
                        .build());
                searchInFavor = !searchInFavor;
                cmdSearchInFavor_click();
                if(searchInFavor) {
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.star));
                } else {
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.star_outline));
                }
                return true;
            case R.id.action_settings:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("UX")
                        .setAction("click")
                        .setLabel("Settings")
                        .build());
                cmdSettings_click(null);
                return true;
            case R.id.action_rate:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("UX")
                        .setAction("click")
                        .setLabel("Rate")
                        .build());
                Intent googlePlayIntent = DataController.createIntentForGooglePlay(this);
                startActivity(googlePlayIntent);
                return true;
            case R.id.action_about:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("UX")
                        .setAction("click")
                        .setLabel("About")
                        .build());
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!searchInFavor) {
            if (searchByNav && dataMan.isLocationChanged() || firstStart) {
                searchNearMe(false, "onStart");
            }
        }
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!dataMan.isNavWorking()) {
            dataMan.navInit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("STOPSEARCHACTIVITY","StopSearchActivity onStop");
        // The activity is no longer visible (it is now "stopped")
        dataMan.navTerminate();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("STOPSEARCHACTIVITY", "StopSearchActivity onDestroy");
        Navigation.getInstance(this).stopLocationUpdates();

        // The activity is about to be destroyed.
    }

    private void openLocationSettings() {
        // Log.appendLog("StopSearchActivity openLocationSettings - promt to open system location settings screen");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.TurnOnNavigationPromt);
        builder.setPositiveButton(R.string.YesText, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.NoText, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.show();
    }

    public void notifyMoved() {
        // Log.appendLog("StopSearchActivity notifyMoved");
        if (searchByNav && !searchInFavor) {
            searchNearMe(false, "notifyMoved");
        }
    }


    public void cmdSearchInFavor_click() {
        if (searchInFavor) {
            new FavorTask().execute(true, false);
        } else {
            if (searchByNav) {
                searchNearMe(false, "cmdSearchInFavor_click");
            }
        }
    }

    private class FavorTask extends AsyncTask<Boolean, Boolean, StopGroup[]> {

        // Do the long-running work in here
        protected StopGroup[] doInBackground(Boolean... param) {
            // Log.appendLog("StopSearchActivity FavorTask doInBackground");
            return DataController.mergeStops(dataMan.getFavor(), param[0], param[1]);
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Boolean... progress) {

        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(StopGroup[] result) {
            if (isCancelled()) {
                return;
            }
            grp = result;
            fillList();
        }
    }

    public void cmdSettings_click(View view) {
        Log.d("","StopSearchActivity cmdSettings_click");
        Intent intent = new Intent(this, SettingsActivity.class);
        DataController dataMan = DataController.getInstance();
        intent.putExtra("radius", dataMan.getRadius());
        intent.putExtra("backgroundFlag", dataMan.isBackgroundUpdate());
        intent.putExtra("updateFlag", dataMan.isAutoUpdate());
        intent.putExtra("showTrams", dataMan.isShowTrams());
        intent.putExtra("showTrolls", dataMan.isShowTrolls());
        intent.putExtra("showBuses", dataMan.isShowBuses());
        intent.putExtra("showComm", dataMan.isShowComm());
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            msgBox(this, "123", "");
        }
        if (requestCode != 10) {
            DataController.getInstance().setSettings(data.getIntExtra("radius", 600), data.getBooleanExtra("updateFlag", true), data.getBooleanExtra("backgroundFlag", true), data.getBooleanExtra("showBuses", true), data.getBooleanExtra("showTrolls", true), data.getBooleanExtra("showTrams", true), data.getBooleanExtra("showComm", true));
            if (searchByNav) {
                if (dataMan.navInit()) {
                    new SearchNearMeTask().execute(true, true);
                }
            }
        }
    }

    public void cmdFind_click(View view) {
        // Log.appendLog("StopSearchActivity cmdFind_click");
        String stopName ="temp"; //((EditText) findViewById(R.id.txtStopName)).getText().toString();
        if (stopName.length() >= 3) {
            if (!dataMan.isNavWorking()) {
                dataMan.navInit();
            }
            searchByNav = false;
            new SearchByNameTask().execute(stopName);
        } else if (stopName.length() == 0) {
            searchNearMe(false, "cmdFind_click");
        } else {
            if (view != null) {
                msgBox(this, "Название слишком короткое! Ограничение - 3 буквы", "Info");
            }
        }
    }


    public void searchNearMe(boolean force, String who) {
        if (!dataMan.isNavWorking()) {
            dataMan.navInit();
        }
        searchByNav = true;
        firstStart = false;
        if (dataMan.isNavWorking()) {
            if (snmt.getStatus() != AsyncTask.Status.RUNNING) {
                snmt.execute(true, force); // true =

            }
        }
    }

    private class SearchNearMeTask extends AsyncTask<Boolean, Boolean, StopGroup[]> {

        // Do the long-running work in here
        protected StopGroup[] doInBackground(Boolean... param) {
            // Log.appendLog("StopSearchActivity SearchNearMeTask doInBackground");
            try {
                Looper.prepare();
            } catch (Exception e) {
            }
            //if (param[0]) {
                StopGroup[] res = DataController.mergeStops(dataMan.searchNearMe(searchInFavor, param[1]), true, param[1]);
                return res;
            //} else {
            //    return null;
            //}
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Boolean... progress) {

        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(StopGroup[] result) {
            if (isCancelled()) {
                return;
            }
            grp = result;
            if (grp != null)
            // grp = new StopGroup[0];
            {
                fillList();
            } else {
                grp = new StopGroup[0];
            }
            // dataMan.requestNewLocation();
            snmt = new SearchNearMeTask();
        }
    }

    private class SearchByNameTask extends AsyncTask<String, Boolean, StopGroup[]> {

        // Do the long-running work in here
        protected StopGroup[] doInBackground(String... name) {
            // return dataMan.searchByNameMerged(name[0], false, true);
            // Log.appendLog("StopSearchActivity SearchByNameTask doInBackground");
            return dataMan.mergeStops(dataMan.searchByName(name[0], searchInFavor), dataMan.isNavWorking(), false);
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Boolean... progress) {

        }

        // This is called when doInBackground() is finished
        protected void onPostExecute(StopGroup[] result) {

            if (isCancelled()) {
                return;
            }
            grp = result;
            if (grp == null) {
                grp = new StopGroup[0];
            }
            fillList();
        }
    }

    private void fillList() {
        StopGroupsListAdapter adapter = new StopGroupsListAdapter(this, grp);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("UX")
                        .setAction("click")
                        .setLabel("List item")
                        .build());
                showDirections(position);
            }
        });
    }

    public void showDirections(int grpID) {
        Log.d("","StopSearchActivity showDirections");
        Intent intent = new Intent(this, DirectionSelectActivity.class);
        try {
            dataMan = DataController.getInstance(this);
        } catch (NullPointerException e) {
            dataMan = DataController.getInstance(this);
        }
        try {
            grp[grpID].stops = dataMan.getStops(grp[grpID].KS_IDs);
            intent.putExtra(MESSAGE_STOPGROUP, grp[grpID]);
            // intent.putExtra(MESSAGE_DATAMAN, dataMan);
            startActivity(intent);
        } catch (ArrayIndexOutOfBoundsException e) {
            searchNearMe(true, "");
        }
    }

    public static void msgBox(Context context, String promt, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(promt).setTitle(title);
        builder.show();
    }

}
