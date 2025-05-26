package com.example.playlistmaker.search.presentation.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.presentation.MusicPlayerActivity
import com.example.playlistmaker.search.presentation.mapper.TrackMapper
import com.example.playlistmaker.search.presentation.models.TrackUI
import com.example.playlistmaker.search.presentation.stateHolders.SearchActivityViewModel
import com.example.playlistmaker.search.presentation.stateHolders.SearchState

class SearchActivity : AppCompatActivity() {

    private val searchRunnable = Runnable { sendToServer() }
    private var searchString = ""
    private lateinit var binding: ActivitySearchBinding
    private var isClickAllowed = true
    private var showHistory = true
    private var currentHistory: List<TrackUI> = emptyList()
    private val handler = Handler(Looper.getMainLooper())
    private val adapter by lazy { TrackAdapter() }
    private val viewModel: SearchActivityViewModel by viewModels {
        SearchActivityViewModel.Factory(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH, searchString)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchString = savedInstanceState.getString(SEARCH).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.sharedPrefsWork(USE_READ)

        viewModel.searchState.observe(this) { state ->
            currentHistory = when (state) {
                is SearchState.Success -> state.history
                is SearchState.Error -> state.history
                is SearchState.Loading -> state.history
                is SearchState.Empty -> state.history
            }.map { TrackMapper.mapToTrackUI(it) }

            when (state) {
                is SearchState.Success -> {
                    showHistory = false
                    showSearchResults(state.tracks.map { TrackMapper.mapToTrackUI(it) })
                }

                is SearchState.Error -> {
                    showError()
                    updateHistoryViews()
                }

                is SearchState.Loading -> {
                    showLoading()
                    updateHistoryViews()
                }

                is SearchState.Empty -> {
                    showEmptyResults()
                    updateHistoryViews()
                }
            }
        }

        fun clickDebounce(): Boolean {
            val current = isClickAllowed
            if (isClickAllowed) {
                isClickAllowed = false
                handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
            }
            return current
        }

        adapter.onClick = { trackUI ->
            if (clickDebounce()) {
                val domainTrack = TrackMapper.mapToTrack(trackUI)
                viewModel.sharedPrefsWork(USE_WRITE, domainTrack)
                val playerIntent = Intent(this, MusicPlayerActivity::class.java).apply {
                    putExtra(MusicPlayerActivity.getTrackKey(), trackUI)
                }
                startActivity(playerIntent)
            }
        }

        binding.rwTrack.adapter = adapter
        binding.etSearchText.setText(searchString)

        binding.toolBarSearch.setOnClickListener {
            finish()
        }

        binding.bClearHistorySearch.setOnClickListener {
            viewModel.sharedPrefsWork(USE_CLEAR)
        }

        binding.etSearchText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.etSearchText.text.isEmpty()) {
                showHistory = true
                updateHistoryViews()
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (binding.etSearchText.hasFocus() && s.isNullOrEmpty()) {
                    showHistory = true
                    updateHistoryViews()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
                binding.ivClearIcon.isVisible = !s.isNullOrEmpty()
                searchString = binding.etSearchText.toString()

                if (binding.etSearchText.hasFocus() && s?.isEmpty() == true) {
                    showHistory = true
                    updateHistoryViews()
                    binding.llHolderNothingOrWrong.isVisible = false
                } else {
                    showHistory = false
                    binding.llHolderNothingOrWrong.isVisible = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        binding.etSearchText.addTextChangedListener(simpleTextWatcher)

        binding.ivClearIcon.setOnClickListener {
            binding.etSearchText.setText("")
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.etSearchText.windowToken, 0)
            showHistory = true
            updateHistoryViews()
        }

        binding.btResearch.setOnClickListener {
            sendToServer()
        }

        binding.etSearchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendToServer()
            }
            false
        }
        setupInsets()
    }

    private fun showSearchResults(tracks: List<TrackUI>) {
        binding.progressBar.isVisible = false
        if (tracks.isNotEmpty()) {
            adapter.data = tracks.toMutableList() as ArrayList<TrackUI>
            binding.rwTrack.isVisible = true
            binding.llHolderNothingOrWrong.isVisible = false
        } else {
            showEmptyResults()
        }
        updateHistoryViews()
    }

    private fun showEmptyResults() {
        binding.progressBar.isVisible = false
        binding.rwTrack.isVisible = false
        binding.llHolderNothingOrWrong.isVisible = true
        binding.ivSunOrWiFi.setImageResource(R.drawable.sun_ic)
        binding.tvTextHolder.setText(R.string.nothing)
        binding.btResearch.isVisible = false
    }

    private fun showLoading() {
        binding.rwTrack.isVisible = false
        binding.llHolderNothingOrWrong.isVisible = false
        binding.btResearch.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun sendToServer() {
        if (binding.etSearchText.text.isNotBlank()) {
            viewModel.searchTracks(binding.etSearchText.text.toString())
        }
    }

    private fun showError() {
        binding.progressBar.isVisible = false
        binding.rwTrack.isVisible = false
        binding.llHolderNothingOrWrong.isVisible = true
        binding.ivSunOrWiFi.setImageResource(R.drawable.nointernet_ic)
        binding.tvTextHolder.setText(R.string.Wrong)
        binding.btResearch.isVisible = true
    }

    private fun updateHistoryViews() {
        val shouldShowHistory = showHistory && currentHistory.isNotEmpty()

        binding.tvHistorySearch.isVisible = shouldShowHistory
        binding.bClearHistorySearch.isVisible = shouldShowHistory

        if (shouldShowHistory) {
            adapter.data = currentHistory.toMutableList() as ArrayList<TrackUI>
            binding.rwTrack.isVisible = true
            binding.llHolderNothingOrWrong.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.etSearchText.text.isEmpty()) {
            showHistory = true
            updateHistoryViews()
        }
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

    private companion object {
        const val SEARCH = "SEARCH"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val USE_CLEAR = "clear"
        const val USE_READ = "read"
        const val USE_WRITE = "write"
    }
}