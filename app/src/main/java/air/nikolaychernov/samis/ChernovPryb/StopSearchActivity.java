package air.nikolaychernov.samis.ChernovPryb;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.Serializable;

public class StopSearchActivity extends Activity implements Serializable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private ProgressDialog mConnectionProgressDialog;
    private ConnectionResult mConnectionResult;

    private DataController dataMan;
    private StopGroup[] grp;
    private PullToRefreshListView list;
    private GoogleApiClient mGoogleApiClient;

    public final static String MESSAGE_KS_ID = "com.markikokik.transarrival63.KS_ID";
    public final static String MESSAGE_STOP = "com.markikokik.transarrival63.stop";
    public final static String MESSAGE_ARRIVAl_INFO = "com.markikokik.transarrival63.arrivalInfo";
    public final static String MESSAGE_STOPGROUP = "com.markikokik.transarrival63.stopGroup";
    public final static String MESSAGE_DATAMAN = "com.markikokik.transarrival63.dataMan";

    private boolean searchByNav = true;
    private boolean searchInFavor = false;
    private boolean firstStart = true;
    public static String accountName = "";

    private Menu menu;

    private SearchNearMeTask snmt;

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("onConnected","mGoogleApiClient.connect()");
        accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
        //ActivationService.startActionFoo(this, accountName, " ");
        //Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnectionSuspended(int arg0) {
        Log.d("onConnectionSuspended","mGoogleApiClient.connect()");
        //mGoogleApiClient.connect();
        //updateUI(false);

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("onConnectionFailed",""+ result.getErrorCode());
        try {
            result.startResolutionForResult(this,10);
        } catch (SendIntentException e) {
            //mIntentInProgress = false;
            mGoogleApiClient.connect();
        }
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the next section.
    }

    private class AutoSearchTimer extends CountDownTimer {

        public AutoSearchTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // Do something...
            if (!searchByNav) {
                cmdFind_click(null);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Log.logInit(this);
        Log.d("STOPSEARCHACTIVITY","StopSearchActivity onCreate");
        setContentView(R.layout.activity_stopsearch);
        // try {
        // DataController.loadArrivalData2("", 9);
        // } catch (IOException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        //mPlusClient = new PlusClient.Builder(this, this, this).setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity").build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        //Intent serviceIntent = new Intent(this, ActivationService.class);
        //startService(serviceIntent);


        /*if (isPortrait()) {
            findViewById(R.id.linLayoutSplashPortrait).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.linLayoutSplashLandscape).setVisibility(View.VISIBLE);
        }*/

        dataMan = DataController.getInstance(this);

        snmt = new SearchNearMeTask();

        //dataMan.setTypeface((TextView) findViewById(R.id.lblToStop), HelveticaFont.Bold);
        // ((EditText)
        // findViewById(R.id.txtStopName)).setTypeface(Typeface.createFromAsset(getAssets(),
        // "fonts/Helvetica"
        // + "Medium" + ".otf"));

        list = (PullToRefreshListView) findViewById(R.id.list);
        list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshView.getLoadingLayoutProxy().setTextTypeface(DataController.getInstance().getTypeface(DataController.HelveticaFont.Medium));
                searchNearMe(false, "refresh");
            }
        });
        list.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ListView>() {

            @Override
            public void onPullEvent(PullToRefreshBase<ListView> refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {
                // TODO Auto-generated method stub
                //refreshView.setLoadingDrawable(null);
                refreshView.getLoadingLayoutProxy().setLoadingDrawable(null);
                //refreshView.getLoadingLayoutProxy().setRefreshingLabel(null);

            }
        });

        ActionBar ab = getActionBar();
        ab.setIcon(null);

        //mGoogleApiClient.connect();
        //Log.d("onStart","mGoogleApiClient.connect()");
        handleIntent(getIntent());
        // dataMan.navInit();
        // searchNearMe();
        if (!dataMan.navInit()) {
            // msgBox(this, "Навигация отключена в настройках!",
            // "Ошибочка вышла");
            openLocationSettings();
        }
        // cmdFindNearMe_click(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.stopsearch_activity_menu, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
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
                searchInFavor = !searchInFavor;
                cmdSearchInFavor_click();
                if(searchInFavor) {
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_important));
                } else {
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_action_not_important_white));
                }
                return true;
            case R.id.action_settings:
                cmdSettings_click(null);
                return true;
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Log.appendLog("StopSearchActivity onStart");
        // The activity is about to become visible.
        //((ToggleButton) findViewById(R.id.toggleSearchInFavor)).setChecked(searchInFavor);
        // if (!dataMan.isNavWorking())
        // dataMan.navInit();
        Log.d("STOPSEARCHACTIVITY","StopSearchActivity onStart");
        mGoogleApiClient.connect();
        Log.d("onStart","mGoogleApiClient.connect()");
        if (!searchInFavor) {
            // Log.appendLog("StopSearchActivity onStart !searchInFavor");
            // Log.appendLog("StopSearchActivity onStart searchByNav=" +
            // searchByNav);
            if (searchByNav && dataMan.isLocationChanged() || firstStart) {
                // Log.appendLog("StopSearchActivity onStart calling searchNearMe");
                // searchNearMe(true, "onStart");
                searchNearMe(false, "onStart");
                // dataMan.requestNewLocation();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Log.appendLog("StopSearchActivity onResume");
        // The activity has become visible (it is now "resumed").
        if (!dataMan.isNavWorking()) {
            // Log.appendLog("StopSearchActivity onResume !dataMan.isNavWorking()");
            dataMan.navInit();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        Log.d("STOPSEARCHACTIVITY","StopSearchActivity onStop");
        // The activity is no longer visible (it is now "stopped")
        dataMan.navTerminate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("STOPSEARCHACTIVITY","StopSearchActivity onDestroy");
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("authkey");
        editor.commit();
        // The activity is about to be destroyed.
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // // DataController.getInstance().createNewDB();
    // msgBox(this, "Completed", "");
    //
    // return false;
    // }

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

    // public void cmdCreateDB_click(View view) {
    // try {
    // dataMan.createDBfromXML(true, true);
    // msgBox(this, "Alles", "OK");
    // } catch (IOException e) { // TODO Auto-generated catch block
    // msgBox(this, e.getLocalizedMessage(), "error");
    // } catch (XmlPullParserException e) { // TODO Auto-generated catch block
    // msgBox(this, e.getLocalizedMessage(), "error");
    // }
    // }

    public void notifyMoved() {
        // Log.appendLog("StopSearchActivity notifyMoved");
        if (searchByNav && !searchInFavor) {
            searchNearMe(false, "notifyMoved");
        }
    }

    public void say(String msg) {
        msgBox(this, msg, "");
    }

    public void sayMoved(boolean isGPS) {
        // msgBox(this, "Notified", "");
        // moveCounter++;
        if (isGPS) {
            msgBox(this, "Обновление точного местоположения", "");
        } else {
            msgBox(this, "Обновление неточного местоположения", "");
        }
    }

    public void cmdSearchInFavor_click() {
        // Log.appendLog("StopSearchActivity cmdSearchInFavor_click");
        //searchInFavor = ((ToggleButton) view).isChecked();
        if (searchInFavor) {
            new FavorTask().execute(true, false);
        } else {
            if (searchByNav) {
                searchNearMe(false, "cmdSearchInFavor_click");
            }
            // else
            // cmdFind_click(null);
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
            // searchByNav = false;
            // dataMan.navTerminate();
            // Log.appendLog("StopSearchActivity FavorTask onPostExecute");
            if (isCancelled()) {
                return;
            }
            grp = result;
            fillList();
        }
    }

    public void cmdErase_click(View view) {
        //((TextView) findViewById(R.id.txtStopName)).setText("");
    }

    public void cmdSettings_click(View view) {
        Log.d("","StopSearchActivity cmdSettings_click");
        Intent intent = new Intent(this, SettingsActivity.class);
        DataController dataMan = DataController.getInstance();
        intent.putExtra("radius", dataMan.getRadius());
//		intent.putExtra("requestAdditionalPredict",
//				dataMan.isRequestAddPredict());
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
            DataController.getInstance().setSettings(data.getIntExtra("radius", 600), data.getBooleanExtra("updateFlag", true), data.getBooleanExtra("showBuses", true), data.getBooleanExtra("showTrolls", true), data.getBooleanExtra("showTrams", true), data.getBooleanExtra("showComm", true));
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

    public void cmdFindNearMe_click(View view) {
        // Log.appendLog("StopSearchActivity cmdFindNearMe_click");
        if (!dataMan.navInit()) {
            // msgBox(this, "Навигация отключена в настройках!",
            // "Ошибочка вышла");
            openLocationSettings();
        }
        // dataMan.requestNewLocation();
        searchNearMe(true, "cmdFindNearMe_click");
    }

    public void searchNearMe(boolean force, String who) {
        // msgBox(this, who, "");
        // Log.appendLog("StopSearchActivity searchNearMe from " + who);
        if (!dataMan.isNavWorking()) {
            dataMan.navInit();
        }
        searchByNav = true;
        firstStart = false;
        if (dataMan.isNavWorking()) {
            if (snmt.getStatus() != AsyncTask.Status.RUNNING) {
                snmt.execute(true, force); // true =
                // dataMan.isNavWorking();
                // false = no force
                // use last network
                // location
            }
        } else {
            findViewById(R.id.linLayoutSplashPortrait).setVisibility(View.INVISIBLE);
            findViewById(R.id.linLayoutSplashLandscape).setVisibility(View.INVISIBLE);
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
            // Log.appendLog("StopSearchActivity SearchNearMeTask onPostExecute");
            findViewById(R.id.linLayoutSplashPortrait).setVisibility(View.INVISIBLE);
            findViewById(R.id.linLayoutSplashLandscape).setVisibility(View.INVISIBLE);
            //findViewById(R.id.progressLoading).setVisibility(View.INVISIBLE);
            list.onRefreshComplete();
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
            // Log.appendLog("StopSearchActivity SearchByNameTask onPostExecute");
            findViewById(R.id.linLayoutSplashPortrait).setVisibility(View.INVISIBLE);
            findViewById(R.id.linLayoutSplashLandscape).setVisibility(View.INVISIBLE);
            // dataMan.navTerminate();
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
        //Log.d("","StopSearchActivity fillList");

        /*list.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ListView>() {

            @Override
            public void onPullEvent(PullToRefreshBase<ListView> refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {
                // TODO Auto-generated method stub
                refreshView.getLoadingLayoutProxy().setLoadingDrawable(null);

            }
        });*/
        StopGroupsListAdapter adapter = new StopGroupsListAdapter(this, grp);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                showDirections(position-1);
            }
        });
    }

    public void showDirections(int grpID) {
        Log.d("","StopSearchActivity showDirections");
        Intent intent = new Intent(this, DirectionSelectActivity.class);
        try {
            dataMan = DataController.getInstance();
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

//	public void showArrival(int KS_ID) {
//		// Log.appendLog("StopSearchActivity showArrival");
//		try {
//			Intent intent = new Intent(this, ArrivalActivity.class);
//			intent.putExtra(MESSAGE_STOP, dataMan.getStop(KS_ID));
//			intent.putExtra(MESSAGE_ARRIVAl_INFO, DataController
//					.getArrivalInfo(KS_ID));
//			startActivity(intent);
//		} catch (IOException e) {
//			// Log.appendLog("EXCEPTION in StopSearchActivity showArrival " +
//			// e.getLocalizedMessage());
//			msgBox(this, e.getLocalizedMessage(), "Error");
//		}
//
//	}

    private boolean isPortrait() {
        // Log.appendLog("StopSearchActivity isPortrait");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static void msgBox(Context context, String promt, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(promt).setTitle(title);
        builder.show();
    }



}
