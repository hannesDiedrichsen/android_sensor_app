package de.diedrichsen.superapps.sensoren;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {



    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sStorage = getApplicationContext().getSharedPreferences("data.pr", Context.MODE_PRIVATE);
        final SharedPreferences.Editor sEditor = sStorage.edit();


        // View-Elemente aus XML-Layout Datei erzeugen lassen
        setContentView(R.layout.activity_settings);
        SeekBar seekBar = findViewById(R.id.settingsSeekbar);
        final TextView sView = findViewById(R.id.settingsView);
        final EditText gAcc = findViewById(R.id.settings_gAcc);
        final TextView exportData = findViewById(R.id.export_data);
        @SuppressLint("UseSwitchCompatOrMaterialCode") final Switch throwSwitch = findViewById(R.id.throwSwitch);

        // Initialisieren der App Bar und Aktivieren des Up-Buttons
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        sView.setText(getString(R.string.settingsSensiText) + sStorage.getFloat("sensi", 5F));
        seekBar.setProgress((int) (sStorage.getFloat("sensi", 5F) * 10));
        gAcc.setText(String.valueOf(sStorage.getFloat("gAcc", 9.81F)));
        throwSwitch.setChecked(sStorage.getBoolean("throw", false));




        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sView.setText(getString(R.string.settingsSensiText) + (float) progress / 10);
                sEditor.putFloat("sensi", (float) progress / 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sEditor.apply();
            }
        });

        gAcc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (gAcc.getText() != null && gAcc.getText().length() > 0 && !Objects.equals(gAcc.getText().toString(), "-")) {
                    sEditor.putFloat("gAcc", Float.parseFloat(gAcc.getText().toString()));
                    sEditor.apply();
                }

            }
        });

        throwSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sEditor.putBoolean("throw", b);
                sEditor.apply();
            }
        });


    }

    public void onClick(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        final SharedPreferences sStorage = getApplicationContext().getSharedPreferences("data.pr", Context.MODE_PRIVATE);

        String t;
        t = sStorage.getString("t", "(No data)");

        sendIntent.putExtra(Intent.EXTRA_TEXT, t);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

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
