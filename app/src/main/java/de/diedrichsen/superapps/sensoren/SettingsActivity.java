package de.diedrichsen.superapps.sensoren;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

public class SettingsActivity extends AppCompatActivity {


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // View-Elemente aus XML-Layout Datei erzeugen lassen
        setContentView(R.layout.activity_settings);
        SeekBar seekBar = findViewById(R.id.settingsSeekbar);
        final TextView sView = findViewById(R.id.settingsView);
        final CheckBox checkBox = findViewById(R.id.checkBox);

        // Initialisieren der App Bar und Aktivieren des Up-Buttons
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        final SharedPreferences pref = getApplicationContext().getSharedPreferences("data.pr", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        sView.setText(getString(R.string.settingsSensiText) + pref.getFloat("sensi", 5F));
        seekBar.setProgress((int) (pref.getFloat("sensi", 5F) * 10));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("slow_mode", b);
                editor.apply();
            }
        });


        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sView.setText(getString(R.string.settingsSensiText) + (float) progress / 10);
                editor.putFloat("sensi", (float) progress / 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.apply();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
