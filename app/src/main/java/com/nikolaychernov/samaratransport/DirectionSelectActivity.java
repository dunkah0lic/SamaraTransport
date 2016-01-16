package com.nikolaychernov.samaratransport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Arrays;

public class DirectionSelectActivity extends AppCompatActivity {

    private StopGroup grp;
    Tracker tracker;
    Toolbar toolbar;
    ListView list;
    RelativeLayout closeLayout;
    ImageButton close;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(MyApplication.getCurrentTheme());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_direction_select);
        grp = (StopGroup) getIntent().getSerializableExtra(StopSearchActivity.MESSAGE_STOPGROUP);

        DataController dataMan = null;
        try {
            dataMan = DataController.getInstance();

        } catch (NullPointerException ex) {
            Log.d("DATABASE1", "FAIL");
            ex.printStackTrace();
            Intent mainIntent = new Intent(this, StopSearchActivity.class);
            finish();
            startActivity(mainIntent);
            return;
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) findViewById(R.id.title);
        TextView subtitle = (TextView) findViewById(R.id.subtitle);
        close = (ImageButton) findViewById(R.id.close);
        closeLayout = (RelativeLayout) findViewById(R.id.close_layout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.appodealBannerView).setVisibility(View.GONE);
                closeLayout.setVisibility(View.GONE);
            }
        });
        title.setText(grp.title);
        subtitle.setText(grp.adjacentStreet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        /*AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("D2BB690A1C36737474DDCC9FFAF4EFEE")
                .addTestDevice("F0108517ADF31A088E0C123160CFD0BE")
                .build();
        adView.loadAd(adRequest);*/
        //adView.setVisibility(View.GONE);

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.setBannerCallbacks(new BannerCallbacks() {

            @Override
            public void onBannerLoaded() {
                findViewById(R.id.appodealBannerView).setVisibility(View.VISIBLE);
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

        list = (ListView) findViewById(R.id.directionList);
        DirectionListAdapter adapter = new DirectionListAdapter(this, grp.stops);
        list.setAdapter(adapter); // отображаем все объекты

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("UX")
                        .setAction("click")
                        .setLabel("List item")
                        .build());
                showArrival((int) id, itemClicked);
            }
        });

        tracker = ((MyApplication) getApplication()).getDefaultTracker();
        tracker.setScreenName("DirectionSelectActivity");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("show")
                .setLabel("DirectionSelectActivity")
                .build());
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
            case R.id.action_rate:
                Intent googlePlayIntent = DataController.createIntentForGooglePlay(this);
                startActivity(googlePlayIntent);
                return true;
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);//
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

    public void cmdSettings_click() {
        Log.d("", "DirectionSelectActivity cmdSettings_click");
        Intent intent = new Intent(this, ProperSettingsActivity.class);
        startActivity(intent);
    }

    public void showArrival(int KS_ID, View view) {
        // Log.appendLog("DirectionSelectActivity showArrival");
        int ind = Arrays.binarySearch(grp.KS_IDs, KS_ID);
        Intent intent = new Intent(this, ArrivalActivity.class);
        intent.putExtra(StopSearchActivity.MESSAGE_STOP, grp.stops[ind]);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enableAnimations = sharedPref.getBoolean("enableAnimations", false);
        if (enableAnimations && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            View sharedView = findViewById(R.id.title);
            String transitionName = "stopName";
            Pair<View, String> p1 = Pair.create(sharedView, transitionName);

            sharedView = view.findViewById(R.id.txtDirectionStreet);
            transitionName = "direction";
            Pair<View, String> p2 = Pair.create(sharedView, transitionName);

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2);
            startActivity(intent, options.toBundle());

        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tracker.setScreenName("DirectionSelectActivity");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        Appodeal.show(this, Appodeal.BANNER_VIEW);
    }
}
