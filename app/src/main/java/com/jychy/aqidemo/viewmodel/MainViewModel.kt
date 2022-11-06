package com.jychy.aqidemo.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.jychy.aqidemo.AqiApiService
import com.jychy.aqidemo.Config
import com.jychy.aqidemo.Config.Companion.PM_25_THRESHOLD
import com.jychy.aqidemo.R
import com.jychy.aqidemo.data.AqiData
import com.jychy.aqidemo.data.Record
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainViewModel: ViewModel() {
    companion object {
        const val TAG = "AQIDemo-MainViewModel"
    }

    @SuppressLint("StaticFieldLeak")
    var liveData: MutableLiveData<List<Record>> = MutableLiveData()
    var searchData: MutableLiveData<List<Record>> = MutableLiveData()
    var horizontalData: MutableLiveData<List<Record>> = MutableLiveData()
    var verticalData: MutableLiveData<List<Record>> = MutableLiveData()
    var searchHint: String = ""

    init {
        getData()
    }

    fun query(context: Context, text: String) {
        // query text empty, showing enter keyword hint
        if (text.isEmpty()) {
            searchHint = context.getString(R.string.enter_keyword)
            searchData.value = null
            return
        }
        // has query text, filter the data
        searchData.value = liveData.value?.filter {
            it.sitename.contains(text) || it.county.contains(text)
        }
        // has query text and search result is empty, showing no result hint
        if (searchData.value?.isEmpty()!!) {
            searchHint = context.getString(R.string.search_not_found, text)
        }
    }

    private fun getData() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://data.epa.gov.tw/")
            //.client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val aqiApiService = retrofit.create(AqiApiService::class.java)

        val call = aqiApiService.getAqi(Config.QUERY_LIMIT, Config.QUERY_API_KEY, Config.QUERY_SORT, Config.QUERY_FORMAT)
        call.enqueue(object : Callback<AqiData> {
            override fun onResponse(call: Call<AqiData>, response: Response<AqiData>) {
                viewModelScope.launch {
                    liveData.value = response.body()?.records
                    horizontalData.value = response.body()?.records?.filter {
                        Log.d(TAG, "${it.sitename}: ${it.pm25}")
                        (it.pm25.toIntOrNull() ?: 0) <= PM_25_THRESHOLD
                    }
                    verticalData.value = response.body()?.records?.filter {
                        Log.d(TAG, "${it.sitename}: ${it.pm25}")
                        (it.pm25.toIntOrNull() ?: 0) > PM_25_THRESHOLD
                    }
                }
            }

            override fun onFailure(call: Call<AqiData>, throwable: Throwable) {
                Log.e(TAG, "Request data fail: $throwable")
            }
        })
    }

    fun toastTouch(view: View, recordName: String) {
        Toast.makeText(view.context, recordName, Toast.LENGTH_LONG).show()
    }
}