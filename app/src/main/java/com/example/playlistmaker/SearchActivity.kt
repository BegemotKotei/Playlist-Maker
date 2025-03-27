package com.example.playlistmaker

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private var searchString = ""
    private lateinit var binding: ActivitySearchBinding
    private lateinit var trackAdapter: TrackAdapter
    private val searchHistory by lazy {
        val sharedPrefs = getSharedPreferences(SearchHistory.getHistoryMain(), MODE_PRIVATE)
        SearchHistory(sharedPrefs)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH, searchString)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchString = savedInstanceState.getString(SEARCH).toString()
    }

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolBarSearch)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        //получаю историю треков из памяти(sharedPrefs)
        var historyTracks = searchHistory.read()

        // Настраиваю адаптер
        val onItemClickListener = object : (Track) -> Unit {
            override fun invoke(item: Track) {
                historyTracks = searchHistory.addTrackToHistory(historyTracks, item)
            }
        }

        trackAdapter = TrackAdapter(tracks, onItemClickListener)
        binding.rwTrack.adapter = trackAdapter

        // Настраиваю поле ввода
        binding.etSearchText.setText(searchString)


        ViewCompat.setOnApplyWindowInsetsListener(binding.toolBarSearch) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Обрабатываю очистку истории
        binding.bClearHistorySearch.setOnClickListener {
            // Очищаю список треков
            historyTracks.clear()
            // Сохраняю пустую историю
            searchHistory.setHistory(historyTracks)

            // Скрываем элементы интерфейса, если история пуста
            if (historyTracks.isEmpty()) {
                binding.tvHistorySearch.isVisible = false
                binding.bClearHistorySearch.isVisible = false
            }
        }

        // Обрабатываю фокус поля поиска
        binding.etSearchText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.etSearchText.text.isEmpty()) {
                trackAdapter.updateData(historyTracks)
                if (historyTracks.isNotEmpty()) {
                    binding.tvHistorySearch.isVisible = true
                    binding.bClearHistorySearch.isVisible = true
                }
            } else {
                trackAdapter.updateData(tracks)
                binding.tvHistorySearch.isVisible = false
                binding.bClearHistorySearch.isVisible = false
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Показываю/скрываю иконку очистки
                binding.ivClearIcon.isVisible = !s.isNullOrEmpty()

                // Обновляю searchString
                searchString = s.toString()

                // Проверяю фокус и содержимое поля
                if (binding.etSearchText.hasFocus()) {
                    if (s.isNullOrEmpty()) {
                        // Если поле пустое и имеет фокус – показываю историю
                        if (historyTracks.isNotEmpty()) {
                            trackAdapter.updateData(historyTracks)
                            binding.tvHistorySearch.isVisible = true
                            binding.bClearHistorySearch.isVisible = true
                        }
                        binding.llHolderNothingOrWrong.isVisible = false
                    } else {
                        // Если есть текст - показываю результаты поиска
                        trackAdapter.updateData(tracks)
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
            trackAdapter.notifyDataSetChanged()
        }

        binding.toolBarSearch.setNavigationOnClickListener { finish() }

        binding.btResearch.setOnClickListener {
            sendToServer()
        }
        binding.etSearchText.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendToServer()
            }
            false
        }

        // Настраиваю взаимодействие с системными полями
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.setPadding(
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                0,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
            insets
        }
    }

    private fun sendToServer() {
        if (binding.etSearchText.text.isNotEmpty()) {
            iTunesService.search(binding.etSearchText.text.toString())
                .enqueue(object : Callback<TrackResponse> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        Log.i("SearchGetFragment", response.toString())

                        if (response.isSuccessful) {
                            tracks.clear()
                            val results = response.body()?.results
                            if (results?.isNotEmpty() == true) {
                                tracks.addAll(results)
                                trackAdapter.notifyDataSetChanged()
                                binding.rwTrack.isVisible = true
                                binding.llHolderNothingOrWrong.isVisible = false
                            } else {
                                showNoResults()
                            }
                        } else {
                            showError()
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        Log.i("SearchGetFragment", t.toString())
                        showError()
                    }
                })
        }
    }

    private fun showError() {
        binding.rwTrack.isVisible = false
        binding.llHolderNothingOrWrong.isVisible = true
        binding.ivSunOrWiFi.setImageResource(R.drawable.nointernet_ic)
        binding.tvTextHolder.setText(R.string.Wrong)
        binding.btResearch.isVisible = true
    }

    private fun showNoResults() {
        binding.rwTrack.isVisible = false
        binding.llHolderNothingOrWrong.isVisible = true
        binding.ivSunOrWiFi.setImageResource(R.drawable.sun_ic)
        binding.tvTextHolder.setText(R.string.nothing)
        binding.btResearch.isVisible = false
    }

    companion object {
        private const val SEARCH = "SEARCH"
    }
}
