package com.example.weichenglin_simpleboggle

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import kotlin.math.abs

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val viewModel: GameViewModel by viewModels()

    private lateinit var gameBoardFragment: GameBoardFragment
    private lateinit var gameScoreFragment: GameScoreFragment
    private lateinit var fragmentManager: FragmentManager

    // For the sensors
    private var sensorManager: SensorManager? = null
    private var prevX = 0f
    private var prevY = 0f
    private var prevZ = 0f
    private var prevTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager = supportFragmentManager

        // Initialize fragments
        gameBoardFragment = GameBoardFragment()
        gameScoreFragment = GameScoreFragment()

        // Add the fragments to the layout
        fragmentManager.beginTransaction()
            .add(R.id.game_board_fragment_container, gameBoardFragment)
            .add(R.id.game_score_fragment_container, gameScoreFragment)
            .commit()

        setUpSensor()
    }

    fun restartGame() {
        // Remove existing fragments
        fragmentManager.beginTransaction().apply {
            remove(gameBoardFragment)
            remove(gameScoreFragment)
            commit()
        }

        // Reinitialize fragments
        gameBoardFragment = GameBoardFragment()
        gameScoreFragment = GameScoreFragment()

        // Add the newly created fragments to the layout
        fragmentManager.beginTransaction()
            .add(R.id.game_board_fragment_container, gameBoardFragment)
            .add(R.id.game_score_fragment_container, gameScoreFragment)
            .commit()
    }

    private fun setUpSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager?.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val curTime = System.currentTimeMillis()
        val timeDiff = curTime - prevTime
        if (timeDiff > 100) {
            val x = event!!.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val velocity = abs(x + y + z - prevX - prevY - prevZ)
//            Log.d(TAG, "onSensorChanged: velocity: $velocity")

            if (velocity > 12) {
                Log.d(TAG, "onSensorChanged: velocity: $velocity")
                restartGame()
            }
            prevX = x
            prevY = y
            prevZ = y
            prevTime = curTime
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }
}