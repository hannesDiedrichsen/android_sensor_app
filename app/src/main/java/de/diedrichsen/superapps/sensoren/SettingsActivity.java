package de.diedrichsen.superapps.sensoren;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View-Elemente aus XML-Layout Datei erzeugen lassen
        setContentView(R.layout.activity_settings);
        SeekBar seekBar = findViewById(R.id.settingsSeekbar);
        final TextView sView = findViewById(R.id.settingsView);
        // Initialisieren der App Bar und Aktivieren des Up-Buttons
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

//        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
//        final SharedPreferences.Editor editor = pref.edit();
//


        // actionBar.setTitle(getString(R.string.action_settings));


        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sView.setText("Start counting xyz acc under: " + (float) progress / 10);
                //editor.putFloat("Limit", progress / 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
