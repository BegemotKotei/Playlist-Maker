package com.example.playlistmaker

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.inputmethod.EditorInfo
import android.text.TextWatcher
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

        val trackAdapter = TrackAdapter(tracks)

        binding.rwTrack.adapter = trackAdapter

        binding.etSearchText.setText(searchString)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tool_bar_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivClearIcon.isVisible = !s.isNullOrEmpty()
                searchString = binding.etSearchText.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }

        }

        fun sendToServer() {
            if (binding.etSearchText.text.isNotEmpty()) {
                iTunesService.search(binding.etSearchText.text.toString())
                    .enqueue(object : Callback<TrackResponse> {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(
                            call: Call<TrackResponse>,
                            response: Response<TrackResponse>
                        ) {
                            binding.rwTrack.isVisible = true
                            binding.llHolderNothingOrWrong.isVisible = false

                            if (response.isSuccessful) {
                                tracks.clear()
                                if (response.body()?.results?.isNotEmpty() == true) {
                                    tracks.addAll(response.body()?.results!!)
                                    trackAdapter.notifyDataSetChanged()
                                }
                                if (tracks.isEmpty()) {
                                    binding.rwTrack.isVisible = false
                                    binding.llHolderNothingOrWrong.isVisible = true
                                    binding.ivSunOrWiFi.setImageResource(R.drawable.sun_ic)
                                    binding.tvTextHolder.setText(R.string.nothing)
                                    binding.btReserch.isVisible = false
                                }
                            } else {
                                binding.rwTrack.isVisible = false
                                binding.llHolderNothingOrWrong.isVisible = true
                                binding.ivSunOrWiFi.setImageResource(R.drawable.nointernet_ic)
                                binding.tvTextHolder.setText(R.string.Wrong)
                                binding.btReserch.isVisible = true
                            }
                        }

                        override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                            binding.rwTrack.isVisible = false
                            binding.llHolderNothingOrWrong.isVisible = true
                            binding.ivSunOrWiFi.setImageResource(R.drawable.nointernet_ic)
                            binding.tvTextHolder.setText(R.string.Wrong)
                            binding.btReserch.isVisible = true
                        }
                    })
            }
        }

        binding.etSearchText.addTextChangedListener(simpleTextWatcher)

        binding.ivClearIcon.setOnClickListener {
            binding.etSearchText.setText("")

            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.etSearchText.windowToken, 0)
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        binding.toolBarSearch.setNavigationOnClickListener { finish() }

        binding.btReserch.setOnClickListener {
            sendToServer()
        }

        binding.etSearchText.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendToServer()
            }
            false
        }
    }

    companion object {
        const val SEARCH = "SEARCH"
    }
}