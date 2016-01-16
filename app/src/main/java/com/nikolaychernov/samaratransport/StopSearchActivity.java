package com.nikolaychernov.samaratransport;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

    public final static String FAVE_PREFERENCE = "fave";

    private boolean searchByNav = true;
    private boolean searchInFavor = false;
    private boolean firstStart = true;

    private Menu menu;

    private SearchNearMeTask snmt;

    Tracker tracker;

    RelativeLayout closeLayout;
    ImageButton close;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);
        // Log.logInit(this);
        Log.d("STOPSEARCHACTIVITY", "StopSearchActivity onCreate");
        setContentView(R.layout.activity_stopsearch);

        dataMan = DataController.getInstance(this);
        snmt = new SearchNearMeTask();

        list = (ListView) findViewById(R.id.list);
        final SwipeRefreshLayout mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        searchInFavor = sharedPref.getBoolean(FAVE_PREFERENCE, false);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.theme_color, typedValue, true);
        int color = typedValue.data;
        mRefreshLayout.setColorSchemeColors(color);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchNearMe(false, "refresh");
                mRefreshLayout.setRefreshing(false);
            }
        });

        //searchNearMe(false, "refresh");
        if (searchInFavor) {
            new FavorTask().execute(true, false);
        } else {
            if (searchByNav) {
                searchNearMe(false, "cmdSearchInFavor_click");
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handleIntent(getIntent());

        if (!dataMan.navInit()) {
            openLocationSettings();
        }

        close = (ImageButton) findViewById(R.id.close);
        closeLayout = (RelativeLayout) findViewById(R.id.close_layout);
        closeLayout.setVisibility(View.GONE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.appodealBannerView).setVisibility(View.GONE);
                closeLayout.setVisibility(View.GONE);
            }
        });

        /*adView = (AdView) findViewById(R.id.adView);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
                closeLayout.setVisibility(View.VISIBLE);
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("UX")
                        .setAction("show")
                        .setLabel("ad")
                        .build());
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Ad")
                        .setAction("show")
                        .setLabel("DirectionSelectActivity")
                        .build());

            }
        });*/
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("D2BB690A1C36737474DDCC9FFAF4EFEE")
                .addTestDevice("F0108517ADF31A088E0C123160CFD0BE")
                .build();
        //adView.loadAd(adRequest);
        //adView.setVisibility(View.GONE);

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

        String appKey = "dca94df13b81a313d639fa98633a8671be49afeb15b7b129";
        Appodeal.initialize(this, appKey, Appodeal.BANNER | Appodeal.INTERSTITIAL);
        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.setBannerCallbacks(new BannerCallbacks() {

            @Override
            public void onBannerLoaded() {
                findViewById(R.id.appodealBannerView).setVisibility(View.VISIBLE);
                //closeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBannerFailedToLoad() {

            }

            @Override
            public void onBannerShown() {
                closeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBannerClicked() {

            }
        });

        tracker = ((MyApplication) getApplication()).getDefaultTracker();
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
        if(searchInFavor) {
            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.star));
        } else {
            menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.star_outline));
        }
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchByNameTask task = new SearchByNameTask();
                task.execute(newText);
                return true;
            }
        });
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
            case R.id.action_share:
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("UX")
                        .setAction("click")
                        .setLabel("Share")
                        .build());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Советую установить Самара Транспорт, отличная альтернатива Прибывалке. #СамараТранспорт " + "https://play.google.com/store/apps/details?id=com.nikolaychernov.samaratransport");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Рассказать друзьям"));
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
        Appodeal.show(this, Appodeal.BANNER_VIEW);
        tracker.setScreenName("StopSeacrhActivity");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("STOPSEARCHACTIVITY", "StopSearchActivity onStop");
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
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(FAVE_PREFERENCE, searchInFavor);
        editor.commit();
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
        Intent intent = new Intent(this, ProperSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != 10) {
            if (data != null) {
                //DataController.getInstance().setSettings(data.getIntExtra("radius", 600), data.getBooleanExtra("updateFlag", true), data.getBooleanExtra("backgroundFlag", true), data.getBooleanExtra("showBuses", true), data.getBooleanExtra("showTrolls", true), data.getBooleanExtra("showTrams", true), data.getBooleanExtra("showComm", true));
                if (searchByNav) {
                    if (dataMan.navInit()) {
                        //new SearchNearMeTask().execute(true, true);
                    }
                }
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
            StopGroup[] res = DataController.mergeStops(dataMan.searchNearMe(searchInFavor, param[1]), true, param[1]);
            return res;
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
            // Log.appendLog("StopSearchActivity SearchByNameTask doInBackground");
            return DataController.mergeStops(dataMan.searchByName(name[0], searchInFavor), dataMan.isNavWorking(), false);
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
                showDirections(position, itemClicked);
            }
        });
    }

    public void showDirections(int grpID, View view) {
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
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            boolean enableAnimations = sharedPref.getBoolean("enableAnimations", false);
            if (enableAnimations && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                View sharedView = view.findViewById(R.id.txtDirectionStopName);
                String transitionName = "stopName";
                Pair<View, String> p1 = Pair.create(sharedView, transitionName);

                sharedView = view.findViewById(R.id.txtDirectionStreet);
                transitionName = "streetName";
                Pair<View, String> p2 = Pair.create(sharedView, transitionName);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2);
                startActivity(intent, options.toBundle());

            } else {
                startActivity(intent);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            searchNearMe(true, "");
        }
    }

}
