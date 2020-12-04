package de.diedrichsen.superapps.sensoren

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.math.round


@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity(), SensorEventListener {


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.smenu, menu)
        return true
    }

    @SuppressLint("SetTextI18n")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Reset the counter to zero
        buttonRes.setOnClickListener {
            duration = 0.0F
            freeFall.text = "Dauer des freien Falls: ${duration}s"
            distTextView.text = "RESET"
            buttonRes.text = "RESET"
            start = true
        }

        val sStorage = applicationContext.getSharedPreferences("data.pr", Context.MODE_PRIVATE)
        // val sWriter = sStorage.edit()


        sensitivity = sStorage.getFloat("sensi", 5F)



        // Write latest freefall data with time stamp and update output
        buttonWrite.setOnClickListener {
            write(
                Context.MODE_APPEND,
                "Timestamp: ${LocalDateTime.now()}, Duration: ${duration}s, Dist: ${dist}m;\n",
                "data.txt"
            )
            showContent()
        }

        buttonWrite.setOnLongClickListener {
            write(Context.MODE_PRIVATE, "", "data.txt")
            textHistory.text = "DELETED"
            return@setOnLongClickListener true
        }




        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )


    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private lateinit var sensorManager: SensorManager

    private var xacc: Float = 0.0F
    private var yacc: Float = 0.0F
    private var zacc: Float = 0.0F
    private var xyzAcc: Float = 0.0F


    private var startTime = System.currentTimeMillis()
    private var stopTime = System.currentTimeMillis()
    private var started = false
    private var duration: Float = 0.0F
    private var dist: Float = 0.0F
    private var z = 0
    private var start: Boolean = true
    private var onStartUp: Boolean = true
    private var sensitivity: Float = 5F
    var slow_mode = false


    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        z++
        xacc = event!!.values[0]
        yacc = event.values[1]
        zacc = event.values[2]
        xyzAcc = xacc * xacc + yacc * yacc + zacc * zacc
        xyzAcc = xyzAcc.toDouble().pow(0.5).toFloat()


        if (onStartUp) {
            onStartUp = false
            showContent()
        }

        if (z % 100 == 0) {
            xyzAccelerometer.text = "Gesamtbeschleunigung: \n $xyzAcc"
            acceleroMeter_data.text = "X: $xacc \n Y: $yacc \n Z: $zacc"
            z = 0
        }

        if (!started && xyzAcc < sensitivity) {
            startTime = System.currentTimeMillis()
            started = true
        } else if (started) {
            stopTime = System.currentTimeMillis()
            if ((stopTime - startTime) / 1000F > duration) {
                // New fall record
                duration = (stopTime - startTime) / 1000F
                dist = round(
                    (0.5 * 9.81 * duration.toDouble().pow(2.0)).toFloat() * 100
                ) / 100
                freeFall.text = "Dauer des freien Falls: ${duration}s"
                distTextView.text = "ca. ${dist}m"

            }
            if (xyzAcc > sensitivity) started = false
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }




    fun write(datamode: Int, output: String, file: String) {
        val file = openFileOutput(file, datamode)
        val writer = OutputStreamWriter(file)
        writer.write(output)
        writer.close()
        file.close()
    }

    private fun content(file: String): String {
        var t: String
        try {
            val file: FileInputStream = openFileInput(file)
            val reader = InputStreamReader(file)
            t = reader.readText()
            reader.close()
            file.close()
        } catch (ex: Exception) {
            t = "(No Data)"
        }

        return t
    }

    private fun showContent() {
        start = false
        val t = content("data.txt")
        if (t.isNotEmpty()) {
            //val lines = t.split("\n")

            textHistory.movementMethod = ScrollingMovementMethod()
            textHistory.text = content("data.txt")
        }
    }



    override fun onRestart() {
        super.onRestart()
        this.recreate()
    }


}





