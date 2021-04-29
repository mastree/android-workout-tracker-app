package com.saraga.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.saraga.workoutapp.tracker.TrackViewModel
import com.saraga.workoutapp.tracker.WordViewModelFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavMain)
        val navController = findNavController(R.id.fragmentMain)
        bottomNav.setupWithNavController(navController)
    }
}