package com.example.playlistmaker.presentation.search

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.ResponseStatus
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.sharedpref.SharedPrefsInteractor
import com.example.playlistmaker.presentation.Creator
import com.example.playlistmaker.presentation.player.MusicPlayerActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val searchRunnable = Runnable { sendToServer() }
    private var searchString = ""
    private val sharedPrefs = Creator.SharedPrefsInteractor()
    private var showHistory = false
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val trackAdapter by lazy { TrackAdapter() }
    private val searchHistory: SharedPreferences by lazy {
        getSharedPreferences(
            HISTORY_MAIN,
            MODE_PRIVATE
        )
    }
    private val historyTracks: ArrayList<Track>
        get() {
            val answer = ArrayList<Track>()
            sharedPrefs.readWriteClear(
                searchHistory,
                USE_READ,
                null,
                consumer = object : SharedPrefsInteractor.SharedPrefsConsumer {
                    override fun consume(foundSharedPrefs: ArrayList<Track>) {
                        answer.addAll(foundSharedPrefs)
                    }
                })
            return answer
        }

    val tracks = ArrayList<Track>()


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

        setupToolBar()

        // Настраиваю адаптер
        trackAdapter.onClick = { item ->
            if (clickDebounce()) {
                sharedPrefs.readWriteClear(
                    searchHistory,
                    USE_WRITE,
                    item,
                    consumer = object : SharedPrefsInteractor.SharedPrefsConsumer {
                        override fun consume(foundSharedPrefs: ArrayList<Track>) {
                            Unit
                        }
                    })
                val playerIntent = Intent(
                    this@SearchActivity,
                    MusicPlayerActivity::class.java
                )
                playerIntent.putExtra(MusicPlayerActivity.getTrackKey(), item)
                startActivity(playerIntent)
            }
        }

        binding.rwTrack.adapter = trackAdapter

        // Настраиваю поле ввода
        binding.etSearchText.setText(searchString)


        // Обрабатываю очистку истории
        binding.bClearHistorySearch.setOnClickListener {
            // Очищаю список треков
            trackAdapter.getDate().clear()
            // Очищаю историю поиска
            sharedPrefs.readWriteClear(
                searchHistory,
                USE_CLEAR,
                null,
                consumer = object : SharedPrefsInteractor.SharedPrefsConsumer {
                    override fun consume(foundSharedPrefs: ArrayList<Track>) {
                    }
                })

            // Скрываем элементы интерфейса, если история пуста
            if (historyTracks.isEmpty()) {
                binding.tvHistorySearch.isVisible = false
                binding.bClearHistorySearch.isVisible = false
                binding.llHolderNothingOrWrong.isVisible = false //может убрать
            }
        }

        // Обрабатываю фокус поля поиска
        binding.etSearchText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.etSearchText.text.isEmpty()) {
                showHistory = true
                trackAdapter.setDate(historyTracks)
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
                trackAdapter.setDate(historyTracks)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()

                // Показываю/скрываю иконку очистки
                binding.ivClearIcon.isVisible = !s.isNullOrEmpty()

                // Обновляю searchString
                searchString = s.toString()

                // Проверяю фокус и содержимое поля
                if (binding.etSearchText.hasFocus()) {
                    if (s.isNullOrEmpty()) {
                        // Если поле пустое и имеет фокус – показываю историю
                        if (historyTracks.isNotEmpty()) {
                            binding.rwTrack.isVisible = true
                            binding.tvHistorySearch.isVisible = true
                            binding.bClearHistorySearch.isVisible = true
                        }
                        binding.llHolderNothingOrWrong.isVisible = false
                    } else {
                        showHistory = false
                        trackAdapter.setDate(tracks)
                        // Если есть текст - показываю результаты поиска
                        binding.tvHistorySearch.isVisible = false
                        binding.bClearHistorySearch.isVisible = false
                        binding.llHolderNothingOrWrong.isVisible = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        binding.etSearchText.addTextChangedListener(simpleTextWatcher)

        // Обрабатываю клик на иконку очистки
        binding.ivClearIcon.setOnClickListener {
            binding.etSearchText.setText("")
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.etSearchText.windowToken, 0)
            tracks.clear()
            showHistory = false
            trackAdapter.setDate(historyTracks)
        }

        binding.toolBarSearch.setNavigationOnClickListener { finish() }

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

    override fun onResume() {
        super.onResume()
        if (showHistory) {
            trackAdapter.setDate(historyTracks)
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.toolBarSearch)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
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

    private fun sendToServer() {
        if (binding.etSearchText.text.isNotEmpty()) {
            binding.rwTrack.isVisible = false
            binding.llHolderNothingOrWrong.isVisible = false
            binding.btResearch.isVisible = false
            binding.progressBar.isVisible = true
            val interactor = Creator.provideTracksInteractor()
            interactor.searchTracks(
                expression = binding.etSearchText.text.toString(),
                consumer = object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTrack: List<Track>, status: ResponseStatus) {
                        runOnUiThread {
                            binding.rwTrack.isVisible = true
                            binding.llHolderNothingOrWrong.isVisible = false
                            binding.progressBar.isVisible = false
                            tracks.clear()
                            when (status) {
                                ResponseStatus.SUCCESS -> {
                                    if (foundTrack.isNotEmpty()) {
                                        tracks.addAll(foundTrack)
                                        showHistory = false
                                        trackAdapter.setDate(tracks)
                                    }
                                    if (tracks.isEmpty()) {
                                        showNoResults()
                                    }
                                }

                                ResponseStatus.ERROR -> {
                                    showError()
                                }
                            }
                        }
                    }
                })
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

    private fun showNoResults() {
        binding.progressBar.isVisible = false
        binding.rwTrack.isVisible = false
        binding.llHolderNothingOrWrong.isVisible = true
        binding.ivSunOrWiFi.setImageResource(R.drawable.sun_ic)
        binding.tvTextHolder.setText(R.string.nothing)
        binding.btResearch.isVisible = false
    }

    private companion object {
        private const val SEARCH = "SEARCH"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val HISTORY_MAIN = "historyMain"
        private const val USE_CLEAR = "clear"
        private const val USE_READ = "read"
        private const val USE_WRITE = "write"
    }
}
