package com.nikolaychernov.samaratransport;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import at.markushi.ui.CircleButton;

/**
 * Created by Nikolay on 12.12.2015.
 */
public class ColorPickerDialog extends DialogFragment implements android.view.View.OnClickListener
{
    Activity context;
    AlertDialog alertDialog;
    private static Tracker tracker;

    public static ColorPickerDialog create(Activity activity){
        ColorPickerDialog dialog = new ColorPickerDialog();
        dialog.context = activity;
        return dialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.theme_picker))
                .setView(getActivity().getLayoutInflater().inflate(R.layout.color_picker, null));

        alertDialog = builder.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        tracker = ((MyApplication) context.getApplication()).getDefaultTracker();
        tracker.setScreenName("SettingsActivity");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("UX")
                .setAction("show")
                .setLabel("SettingsActivity")
                .build());

        return alertDialog;
    }


    @Override
    public void onResume() {
        super.onResume();
        tracker.setScreenName("ColorPicker");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        setupButtons();
    }

    private void setupButtons(){
        Dialog dialog = this.getDialog();
        CircleButton redButton = (CircleButton) dialog.findViewById(R.id.redButton);
        redButton.setOnClickListener(this);
        CircleButton pinkButton = (CircleButton) dialog.findViewById(R.id.pinkButton);
        pinkButton.setOnClickListener(this);
        CircleButton purpleButton = (CircleButton) dialog.findViewById(R.id.purpleButton);
        purpleButton.setOnClickListener(this);
        CircleButton deepPurpleButton = (CircleButton) dialog.findViewById(R.id.deepPurpleButton);
        deepPurpleButton.setOnClickListener(this);
        CircleButton indigoButton = (CircleButton) dialog.findViewById(R.id.indigoButton);
        indigoButton.setOnClickListener(this);
        CircleButton blueButton = (CircleButton) dialog.findViewById(R.id.blueButton);
        blueButton.setOnClickListener(this);
        CircleButton lightBlueButton = (CircleButton) dialog.findViewById(R.id.lightBlueButton);
        lightBlueButton.setOnClickListener(this);
        CircleButton cyanButton = (CircleButton) dialog.findViewById(R.id.cyanButton);
        cyanButton.setOnClickListener(this);
        CircleButton tealButton = (CircleButton) dialog.findViewById(R.id.tealButton);
        tealButton.setOnClickListener(this);
        CircleButton greenButton = (CircleButton) dialog.findViewById(R.id.greenButton);
        greenButton.setOnClickListener(this);
        CircleButton lightGreenButton = (CircleButton) dialog.findViewById(R.id.lightGreenButton);
        lightGreenButton.setOnClickListener(this);
        CircleButton limeButton = (CircleButton) dialog.findViewById(R.id.limeButton);
        limeButton.setOnClickListener(this);
        CircleButton yellowButton = (CircleButton) dialog.findViewById(R.id.yellowButton);
        yellowButton.setOnClickListener(this);
        CircleButton amberButton = (CircleButton) dialog.findViewById(R.id.amberButton);
        amberButton.setOnClickListener(this);
        CircleButton orangeButton = (CircleButton) dialog.findViewById(R.id.orangeButton);
        orangeButton.setOnClickListener(this);
        CircleButton deepOrangeButton = (CircleButton) dialog.findViewById(R.id.deepOrangeButton);
        deepOrangeButton.setOnClickListener(this);
        CircleButton brownButton = (CircleButton) dialog.findViewById(R.id.brownButton);
        brownButton.setOnClickListener(this);
        CircleButton greyButton = (CircleButton) dialog.findViewById(R.id.greyButton);
        greyButton.setOnClickListener(this);
        CircleButton blueGreyButton = (CircleButton) dialog.findViewById(R.id.blueGreyButton);
        blueGreyButton.setOnClickListener(this);

    }

    public void onClick(View view) {
        int themeId = R.style.AppTheme_DeepPurple;
        switch (view.getId())
        {
            case R.id.redButton:
                dismiss();
                themeId = R.style.AppTheme_Red;
                break;
            case R.id.pinkButton:
                dismiss();
                themeId = R.style.AppTheme_Pink;
                break;
            case R.id.purpleButton:
                dismiss();
                themeId = R.style.AppTheme_Purple;
                break;
            case R.id.deepPurpleButton:
                dismiss();
                themeId = R.style.AppTheme_DeepPurple;
                break;
            case R.id.indigoButton:
                dismiss();
                themeId = R.style.AppTheme_Indigo;
                break;
            case R.id.blueButton:
                dismiss();
                themeId = R.style.AppTheme_Blue;
                break;
            case R.id.lightBlueButton:
                dismiss();
                themeId = R.style.AppTheme_LightBlue;
                break;
            case R.id.cyanButton:
                dismiss();
                themeId = R.style.AppTheme_Cyan;
                break;
            case R.id.tealButton:
                dismiss();
                themeId = R.style.AppTheme_Teal;
                break;
            case R.id.greenButton:
                dismiss();
                themeId = R.style.AppTheme_Green;
                break;
            case R.id.lightGreenButton:
                dismiss();
                themeId = R.style.AppTheme_LightGreen;
                break;
            case R.id.limeButton:
                dismiss();
                themeId = R.style.AppTheme_Lime;
                break;
            case R.id.yellowButton:
                dismiss();
                themeId = R.style.AppTheme_Yellow;
                break;
            case R.id.amberButton:
                dismiss();
                themeId = R.style.AppTheme_Amber;
                break;
            case R.id.orangeButton:
                dismiss();
                themeId = R.style.AppTheme_Orange;
                break;
            case R.id.deepOrangeButton:
                dismiss();
                themeId = R.style.AppTheme_DeepOrange;
                break;
            case R.id.brownButton:
                dismiss();
                themeId = R.style.AppTheme_Brown;
                break;
            case R.id.greyButton:
                dismiss();
                themeId = R.style.AppTheme_Grey;
                break;
            case R.id.blueGreyButton:
                dismiss();
                themeId = R.style.AppTheme_BlueGrey;
                break;

        }
        SharedPreferences prefs = getActivity().getSharedPreferences(MyApplication.THEME_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(MyApplication.THEME, themeId);
        editor.commit();
        MyApplication.setCurrentTheme(themeId);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.theme_picker))
                .setMessage(getString(R.string.color_picker_message))
                .setPositiveButton(R.string.reboot, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restart(context, 1000);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });

        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void restart(Context context, int delay) {
        if (delay == 0) {
            delay = 1;
        }
        Log.e("", "restarting app");
        try {
            Intent restartIntent = context.getPackageManager()
                    .getLaunchIntentForPackage(context.getPackageName());
            PendingIntent intent = PendingIntent.getActivity(
                    context, 0,
                    restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.set(AlarmManager.RTC, System.currentTimeMillis() + delay, intent);
            System.exit(2);
        } catch (Exception e){
        }
    }
}