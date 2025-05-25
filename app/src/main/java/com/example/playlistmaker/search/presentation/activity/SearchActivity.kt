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
    private var showHistory = false
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    val adapter by lazy { TrackAdapter() }
    private val viewModel: SearchActivityViewModel by viewModels {
        SearchActivityViewModel.Factory(this)
    }
    private val historyTracks: ArrayList<TrackUI> = arrayListOf()

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
        viewModel.tracksHistory.observe(this) { track ->
            historyTracks.clear()
            historyTracks.addAll(
                track.map {
                    trackD ->
                    TrackMapper.mapToTrackUI(trackD)
                }
            )
        }

        viewModel.searchState.observe(this) { state ->
            when (state) {
                is SearchState.Success -> {
                    showSearchResults(
                        state.tracks
                            .map { TrackMapper.mapToTrackUI(it) }
                    )
                }
                is SearchState.Error -> showError()
                SearchState.Loading -> showLoading()
                SearchState.Empty -> showEmptyResults()
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
                val playerIntent = Intent(this@SearchActivity, MusicPlayerActivity::class.java).apply {
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
            adapter.data.clear()
            viewModel.sharedPrefsWork(USE_CLEAR)
            binding.tvHistorySearch.isVisible = false
            binding.bClearHistorySearch.isVisible = false
        }

        binding.etSearchText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.etSearchText.text.isEmpty()) {
                showHistory = true
                adapter.data = historyTracks
                if (historyTracks.isNotEmpty()) {
                    binding.tvHistorySearch.isVisible = true
                    binding.bClearHistorySearch.isVisible = true
                }
            } else {
                binding.tvHistorySearch.isVisible = false
                binding.bClearHistorySearch.isVisible = false
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                showHistory = true
                adapter.data = historyTracks
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
                binding.ivClearIcon.isVisible = !s.isNullOrEmpty()
                searchString = binding.etSearchText.toString()

                if (binding.etSearchText.hasFocus() && s?.isEmpty() == true) {
                    if (historyTracks.isNotEmpty()) {
                        binding.rwTrack.isVisible = true
                        binding.tvHistorySearch.isVisible = true
                        binding.bClearHistorySearch.isVisible = true
                    }
                    binding.llHolderNothingOrWrong.isVisible = false
                } else {
                    showHistory = false
                    binding.tvHistorySearch.isVisible = false
                    binding.bClearHistorySearch.isVisible = false
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
            adapter.data = historyTracks
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
            binding.rwTrack.isVisible = true
            showHistory = false
            adapter.data = tracks as ArrayList<TrackUI>
            binding.llHolderNothingOrWrong.isVisible = false
        } else {
            showEmptyResults()
        }
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

    override fun onResume() {
        super.onResume()
        if (showHistory) {
            adapter.data = historyTracks
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