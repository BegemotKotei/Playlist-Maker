package com.example.playlistmaker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.musicPlayerFragment -> hideBottomNavigation()
                else -> showBottomNavigation()
            }
        }
        binding.bottomNavigationView.setupWithNavController(navController)
        setupInsets()
    }

    private fun hideBottomNavigation() {
        binding.bottomNavigationView.animate()
            .translationY(binding.bottomNavigationView.height.toFloat())
            .setDuration(300)
            .withEndAction {
                binding.bottomNavigationView.isVisible = false
            }
            .start()
    }

    private fun showBottomNavigation() {
        binding.bottomNavigationView.isVisible = true
        binding.bottomNavigationView.animate()
            .translationY(0f)
            .setDuration(300)
            .start()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBar.left,
                systemBar.top,
                systemBar.right,
                systemBar.bottom
            )
            insets
        }
    }
}