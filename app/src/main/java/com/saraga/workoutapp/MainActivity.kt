package com.saraga.workoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_SHOW_AND_START_TRACKER
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_SHOW_TRACKER_FRAGMENT
import com.saraga.workoutapp.utils.Constants.Companion.ACTION_STOP_TRACKER
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateToTrackerFragment(intent)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavMain)
        val navController = findNavController(R.id.fragmentMain)
        bottomNav.setupWithNavController(navController)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackerFragment(intent)
    }

    private fun navigateToTrackerFragment(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKER_FRAGMENT || intent?.action == ACTION_SHOW_AND_START_TRACKER || intent?.action == ACTION_STOP_TRACKER) {
            var intentRequestTrackerCode = 0
            if (intent.action == ACTION_SHOW_AND_START_TRACKER) {
                intentRequestTrackerCode = 1
            } else if (intent.action == ACTION_STOP_TRACKER) {
                intentRequestTrackerCode = 2
            }

            val bundle = Bundle()
            bundle.putInt("intentRequestTrackerCode", intentRequestTrackerCode)
            findNavController(R.id.fragmentMain).navigate(R.id.action_global_trackerfragment, bundle)
        }
    }
}