package com.ciwrl.papergram.data.network

import com.ciwrl.papergram.data.model.api.ArxivFeed
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ArxivApiService {

    // URL base dell'API di arXiv: http://export.arxiv.org/api/
    // Questo verrà specificato quando creiamo l'istanza di Retrofit.

    @GET("query")
    suspend fun getRecentPapers(
        @Query("search_query") searchQuery: String,
        @Query("sortBy") sortBy: String = "submittedDate",
        @Query("sortOrder") sortOrder: String = "descending",
        @Query("start") start: Int = 0,
        @Query("max_results") maxResults: Int = 10
    ): Response<ArxivFeed>
}