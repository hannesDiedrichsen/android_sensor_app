package de.diedrichsen.superapps.sensoren

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.math.round


class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager

    private var xacc: Float = 0.0F
    private var yacc: Float = 0.0F
    private var zacc: Float = 0.0F
    private var xyzAcc: Float = 0.0F

    private var startTime = System.currentTimeMillis()
    private var stopTime = System.currentTimeMillis()
    private var started = false
    private var durationInMs: Float = 0.0F
    private var dist: Float = 0.0F
    private var z = 0
    private var start: Boolean = true


    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        z++
        xacc = event!!.values[0]
        yacc = event.values[1]
        zacc = event.values[2]
        xyzAcc = xacc * xacc + yacc * yacc + zacc * zacc
        xyzAcc = xyzAcc.toDouble().pow(0.5).toFloat()

        if (z % 100 == 0 && start) {
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
                // Free fall ended
                durationInMs = (stopTime - startTime) / 1000F
                dist = round(
                    (0.5 * 9.81 * durationInMs.toDouble().pow(2.0)).toFloat() * 100
                ) / 100
                freeFall.text = "Dauer des freien Falls: ${durationInMs}s"
                distTextView.text = "ca. ${dist}m"

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
            distTextView.text = "RESET"
            buttonRes.text = "RESET"
            start = true
        }

        buttonRes.setOnLongClickListener {
            start = false
            buttonRes.text = "Resume"
            val t = content()
            if (t.isNotEmpty()) {
                val lines = t.split("\n")

                textHistory.movementMethod = ScrollingMovementMethod()
                textHistory.text = content()
            }

            return@setOnLongClickListener true


        }

        buttonWrite.setOnClickListener {
            write(
                Context.MODE_APPEND,
                "Timestamp: ${LocalDateTime.now()}, Duration: ${durationInMs}s, Dist: ${dist}m;\n"
            )
        }




        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )


    }

    private fun write(datamode: Int, output: String) {
        val file = openFileOutput("data.txt", datamode)
        val writer = OutputStreamWriter(file)
        writer.write(output)
        writer.close()
        file.close()
    }

    private fun content(): String {
        var t: String
        try {
            val file = openFileInput("data.txt")
            val reader = InputStreamReader(file)
            t = reader.readText()
            reader.close()
            file.close()
        } catch (ex: Exception) {
            t = "(No Data)"
        }

        return t
    }


}


