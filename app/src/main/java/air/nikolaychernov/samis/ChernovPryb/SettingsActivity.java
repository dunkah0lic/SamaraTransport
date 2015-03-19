package air.nikolaychernov.samis.ChernovPryb;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnSeekBarChangeListener {

    private TextView progressText;
    private TextView metersText;

    private int radius = 600;
    private boolean isAutoUpdate = true;
//	private boolean requestAddPredict = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DataController dataMan = null;
        try {
            dataMan = DataController.getInstance();
        } catch (NullPointerException ex) {
            Intent mainIntent = new Intent(this, StopSearchActivity.class);
            finish();
            startActivity(mainIntent);
            return;
        }
        progressText = (TextView) findViewById(R.id.txtRadiusLabel);

        //dataMan.setTypeface(progressText, HelveticaFont.Medium);

        //dataMan.setTypeface((TextView) findViewById(R.id.txtRadiusLabel), HelveticaFont.Medium);

//		dataMan.setTypeface((TextView) findViewById(R.id.txtAddInfoLabel),
//				HelveticaFont.Medium);
        //dataMan.setTypeface((TextView) findViewById(R.id.txtAutoUpdateLabel), HelveticaFont.Medium);

        ActionBar ab = getActionBar();
        ab.setIcon(null);


        ((SeekBar) findViewById(R.id.seekRadius)).setOnSeekBarChangeListener(this);
        isAutoUpdate = getIntent().getBooleanExtra("updateFlag", true);
//		requestAddPredict = getIntent().getBooleanExtra("requestAdditionalPredict", true);
        radius = getIntent().getIntExtra("radius", 600);
        // progressText.setText(radius + "");
        ((SeekBar) findViewById(R.id.seekRadius)).setProgress(radius - 300);
        ((Switch) findViewById(R.id.toggleAutoUpdate)).setChecked(isAutoUpdate);
        ((CheckBox) findViewById(R.id.checkShowBuses)).setChecked(getIntent().getBooleanExtra("showBuses", true));
        ((CheckBox) findViewById(R.id.checkShowTrolls)).setChecked(getIntent().getBooleanExtra("showTrolls", true));
        ((CheckBox) findViewById(R.id.checkShowTrams)).setChecked(getIntent().getBooleanExtra("showTrams", true));
        ((CheckBox) findViewById(R.id.checkShowComm)).setChecked(getIntent().getBooleanExtra("showComm", true));
//		((ToggleButton) findViewById(R.id.toggleAddInfo))
//				.setChecked(requestAddPredict);
        setResults();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    public void cmdBack_click(View view) {
        this.finish();
    }

    private void setResults() {
        Intent data = new Intent();
        data.putExtra("updateFlag", ((Switch) findViewById(R.id.toggleAutoUpdate)).isChecked());
//		data.putExtra("requestAdditionalPredict", requestAddPredict);
        data.putExtra("radius", radius);
        data.putExtra("showBuses", ((CheckBox) findViewById(R.id.checkShowBuses)).isChecked());
        data.putExtra("showTrolls", ((CheckBox) findViewById(R.id.checkShowTrolls)).isChecked());
        data.putExtra("showTrams", ((CheckBox) findViewById(R.id.checkShowTrams)).isChecked());
        data.putExtra("showComm", ((CheckBox) findViewById(R.id.checkShowComm)).isChecked());
        // createPendingResult(1, data, PendingIntent.FLAG_ONE_SHOT);
        setResult(RESULT_OK, data);
    }

    public void toggleButtonClick(View view) {
        switch (view.getId()) {
            case R.id.toggleAutoUpdate:
                isAutoUpdate = ((Switch) view).isChecked();
        }

        setResults();
    }

    public void onCheckboxClicked(View view) {
        switch (view.getId()) {
            case R.id.toggleAutoUpdate:
                isAutoUpdate = ((Switch) view).isChecked();
        }

        setResults();
    }

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        // TODO Auto-generated method stub
        onStopTrackingTouch(arg0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        radius = (int) Math.round((seekBar.getProgress() + 300) / 100) * 100;
        if (radius >= 1000) {
            progressText.setText("Радиус поиска остановок: " + radius / 1000.0 + " км");
        } else {
            progressText.setText("Радиус поиска остановок: " + radius + " м");
        }
        seekBar.setProgress(radius - 300);
        setResults();
    }

}
