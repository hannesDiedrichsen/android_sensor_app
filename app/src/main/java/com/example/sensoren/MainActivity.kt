package com.example.sensoren

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.round

class MainActivity : AppCompatActivity(), SensorEventListener {
    lateinit var sensorManager: SensorManager

    var xacc: Float = 0.0F
    var yacc: Float = 0.0F
    var zacc: Float = 0.0F
    var xyzAcc: Float = 0.0F

    var startTime = System.currentTimeMillis()
    var stopTime = System.currentTimeMillis()
    var started = false
    var durationInMs: Float = 0.0F
    var z = 0

    var cxt: Context = this


    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        z++
        xacc = event!!.values[0]
        yacc = event.values[1]
        zacc = event.values[2]
        xyzAcc = xacc * xacc + yacc * yacc + zacc * zacc
        xyzAcc = Math.pow(xyzAcc.toDouble(), 0.5).toFloat()

        if (z % 100 == 0) {
            acceleroMeter_data.text = "X: $xacc \n Y: $yacc \n Z: $zacc"
        }

        if (z % 100 == 0) {
            xyzAccelerometer.text = "Gesamtbeschleunigung: \n $xyzAcc"
            z = 0
        }

        if (!started && xyzAcc < 5) {
            startTime = System.currentTimeMillis()
            started = true
        } else if (started) {
            stopTime = System.currentTimeMillis()


            if ((stopTime - startTime) / 1000F > durationInMs) {
                durationInMs = (stopTime - startTime) / 1000F
                freeFall.text = "Dauer des freien Falls: ${durationInMs}s"
                dist.text = "ca. ${round(0.5 * 9.81 * Math.pow(durationInMs.toDouble(), 2.0) * 100) / 100}m"
            }
            if (xyzAcc > 5) started = false
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonRes.setOnClickListener {
            durationInMs = 0.0F
            freeFall.text = "Dauer des freien Falls: ${durationInMs}s"
            dist.text = "RESET"
        }



        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )


    }

    private fun saveToFile(dateiname: String, text: String): Boolean {

        //Wahrheitswert, um zu prüfen, ob das Speichern erfolgreich war
        var b = true
        var fos: FileOutputStream? = null
        try {

            //Öffne privaten FileOutputStream
            fos = cxt.openFileOutput(dateiname, MODE_PRIVATE)
        } catch (e: FileNotFoundException) {

            //Fehler beim Speichern
            b = false
        }
        try {

            //Schreibe Text in die Datei
            if (fos != null) {
                fos.write(text.toByteArray())
            }
        } catch (e: IOException) {

            //Fehler beim Speichern
            b = false
        }
        try {

            //Schließe den FileOutputStream
            if (fos != null) {
                fos.close()
            }
        } catch (e: IOException) {

            //Fehler beim Speichern
            b = false
        }
        return b
    }
}
