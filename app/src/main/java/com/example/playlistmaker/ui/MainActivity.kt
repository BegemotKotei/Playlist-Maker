package com.example.playlistmaker.ui

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.rootFragmentContainerView.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.musicPlayerFragment -> hideBottomNavigation()
                R.id.aboutPlayListFragment -> hideBottomNavigation()
                R.id.createPlayList -> hideBottomNavigation()
                else -> showBottomNavigation()
            }
        }
        binding.bottomNavigationView.setupWithNavController(navController)
        requestPermissions()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String?>) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            Toast.makeText(
                this,
                "Все разрешения получены",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String?>) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                Toast.makeText(
                    this,
                    "Разрешения заблокированы, включите их в настройках",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun requestPermissions() {
        val permsNeeded = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!EasyPermissions.hasPermissions(
                    this,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                )
            ) {
                permsNeeded.add(android.Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (!EasyPermissions.hasPermissions(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                permsNeeded.add(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permsNeeded.isNotEmpty()) {
            EasyPermissions.requestPermissions(
                this,
                "Для работы музыки и уведомлений нужны разрешения",
                REQUEST_CODE_PERMISSIONS,
                *permsNeeded.toTypedArray()
            )
        }
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

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }
}