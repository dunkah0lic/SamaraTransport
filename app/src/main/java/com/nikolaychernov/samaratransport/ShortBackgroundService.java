package com.nikolaychernov.samaratransport;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Nikolay on 04.07.2015.
 */
public class ShortBackgroundService extends Service {

    private static final String TAG = "ShortBackgroundService";

    TransportDBContract.MainReaderDbHelper mainReaderDbHelper;
    DataController dataMan;
    Context context;
    int KS_ID;
    ArrayList<ArrivalInfo> arrInfo;
    int mId =0;
    NotificationManager mNotificationManager;
    Stop stop;

    String text;
    String title;

    @Override
    public void onStart(Intent intent, int startid)
    {
        //Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");

        context = this;
        TransportDBContract contr = new TransportDBContract();
        mainReaderDbHelper = contr.new MainReaderDbHelper(this);
        dataMan = DataController.getInstance(this);

        Location location = new Location("");
        if (intent!=null) {
            location.setLongitude(intent.getDoubleExtra("longitude", 0));
            location.setLatitude(intent.getDoubleExtra("latitude", 0));
        }

        Stop[] stops = mainReaderDbHelper.searchNearMe(location.getLatitude(), location.getLongitude(), 100);

        StopGroup[] res = DataController.mergeStops(stops, true, false);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (res!=null){
            final Stop[] stopsInGroup = dataMan.getStops(res[0].KS_IDs);
            Arrays.sort(stopsInGroup, new stopComparator(location));

            text = stopsInGroup[0].direction;
            title = "" + res[0].title;
            KS_ID = stopsInGroup[0].KS_ID;

            stop = stopsInGroup[0];

            new DownloadArrivalInfoTask().execute();

        } else {
            //Toast.makeText(getApplicationContext(), "" + mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            mNotificationManager.cancel(mId);
            this.stopSelf();
        }


    }

    private class DownloadArrivalInfoTask extends AsyncTask<Context, ArrayList<ArrivalInfo>, ArrayList<ArrivalInfo>> {

        @Override
        protected ArrayList<ArrivalInfo> doInBackground(Context... params) {
            ArrayList<ArrivalInfo> arrivalInfos = null;
            try{
                arrivalInfos = dataMan.getArrivalInfo(KS_ID);
            } catch (Exception e){
                Log.d(TAG, "Exception");
            }

            return arrivalInfos;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrivalInfo> infos) {
            arrInfo = infos;
            if (arrInfo!=null&&arrInfo.size()!=0) {

                ArrivalInfo arr = arrInfo.get(0);
                //text += arr.routeDesc + /*" " + arr.position + " " + arr.nextStopName +*/ " " + arr.time + " мин.";

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_stat_name)
                                .setContentTitle(title)
                                .setContentText(text)
                                .setColor(getResources().getColor(R.color.green));

                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle();
                String[] events = new String[arrInfo.size()];
                inboxStyle.setBigContentTitle(title);

                for (int i = 0; i < events.length; i++) {
                    arr = arrInfo.get(i);
                    events[i] =  arr.routeDesc + " " + arr.time + " мин.";
                    inboxStyle.addLine(events[i]);
                }
                mBuilder.setStyle(inboxStyle);

                Intent resultIntent = new Intent(context, ArrivalActivity.class);
                resultIntent.putExtra(StopSearchActivity.MESSAGE_STOP, stop);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

                stackBuilder.addParentStack(DirectionSelectActivity.class);
                // Adds the Intent to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                // Gets a PendingIntent containing the entire back stack
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


                mBuilder.setContentIntent(resultPendingIntent);


                mNotificationManager.notify(mId, mBuilder.build());

                stopThisService();
            }
        }
    }

    public void stopThisService(){
        this.stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
