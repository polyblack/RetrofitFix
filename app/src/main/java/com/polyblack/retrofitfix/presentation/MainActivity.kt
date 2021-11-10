package com.polyblack.retrofitfix.presentation

import kotlin.system.measureTimeMillis
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.polyblack.retrofitfix.databinding.ActivityMainBinding
import com.polyblack.retrofitfix.model.CatFactResponse
import com.polyblack.retrofitfix.network.provideApiService
import com.polyblack.retrofitfix.network.provideApiServiceWithRetrofitFix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val scope = CoroutineScope(Dispatchers.Unconfined)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {
            btnTap.setOnClickListener { processClick() }
        }
    }

    private fun processClick() {
        Timber.tag("RetrofitFixLogs").d("button clicked")
        makeQuery()
        hideKeyboard()
    }

    private fun makeQuery() {
        val time = measureTimeMillis {
            scope.launch {
                val result = getRetrofitFixResult()
                // val result = getWithContextResult()
                // val result = getMainThreadResult()
                Timber.tag("RetrofitFixLogs").d("result = $result")
            }
        }

        Timber.tag("RetrofitFixLogs").d("time = $time")
    }

    private suspend fun getRetrofitFixResult() = provideApiServiceWithRetrofitFix()
        .getSomethingFromApi()

    private suspend fun getWithContextResult() = withContext(Dispatchers.IO) {
        getMainThreadResult()
    }

    private suspend fun getMainThreadResult(): CatFactResponse = provideApiService()
        .getSomethingFromApi()

    private fun hideKeyboard() {
        Timber.tag("RetrofitFixLogs").d("start hideKeyBoard()")
        binding.root.hideKeyboard()
    }
}
