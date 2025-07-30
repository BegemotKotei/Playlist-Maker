package com.example.playlistmaker.settings.presentation

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(layoutInflater)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            buttonShareApp.setOnClickListener {
                viewModel.shareApp()
            }
            buttonWriteSupport.setOnClickListener {
                viewModel.sendToSupport()
            }
            buttonUserAgreement.setOnClickListener {
                viewModel.userPolicy()
            }
            switchDayOrNight.setOnCheckedChangeListener { _, checked ->
                val theme = if (checked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                viewModel.updateTheme(theme)
            }
        }

        viewModel.theme.observe(viewLifecycleOwner) {
            val checked = if (it == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
                getSystemNightMode()
            } else {
                it == AppCompatDelegate.MODE_NIGHT_YES
            }
            binding.switchDayOrNight.isChecked = checked
            switchTheme(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getSystemNightMode() = resources
        .configuration
        .uiMode
        .and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    private fun switchTheme(theme: Int) {
        AppCompatDelegate.setDefaultNightMode(
            theme
        )
    }
}