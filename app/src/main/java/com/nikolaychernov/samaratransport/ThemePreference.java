package com.nikolaychernov.samaratransport;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Created by Nikolay on 12.12.2015.
 */
public class ThemePreference extends Preference{
    public ThemePreference(Context context) {
        super(context);
    }

    public ThemePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        super.onClick();
        ColorPickerDialog dialog = ColorPickerDialog.create((Activity) getContext());
        dialog.show(((Activity) getContext()).getFragmentManager(), null);
    }
}
