package com.jychy.aqidemo

import com.jychy.aqidemo.data.AqiData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AqiApiService {
    @GET("/api/v2/aqx_p_432")
    fun getAqi(
        @Query("limit") limit: Int,
        @Query("api_key") api_key: String,
        @Query("sort") sort: String,
        @Query("format") format: String
    ): Call<AqiData>
}