package com.ciwrl.papergram.data.network

import com.ciwrl.papergram.data.model.api.ArxivFeed
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Defines the API endpoints for interacting with the ArXiv.org API.
 * The base URL is configured in [RetrofitInstance].
 */

interface ArxivApiService {

    /**
     * Fetches a list of recent papers from ArXiv.
     *
     * @param searchQuery The search query, typically based on categories (e.g., "cat:cs.AI OR cat:cs.LG").
     * @param sortBy The field to sort the results by (default: "submittedDate").
     * @param sortOrder The order of sorting (default: "descending").
     * @param start The index of the first result to return (for pagination).
     * @param maxResults The maximum number of results to return per page.
     * @return A Retrofit [Response] containing an [ArxivFeed].
     */

    @GET("query")
    suspend fun getRecentPapers(
        @Query("search_query") searchQuery: String,
        @Query("sortBy") sortBy: String = "submittedDate",
        @Query("sortOrder") sortOrder: String = "descending",
        @Query("start") start: Int = 0,
        @Query("max_results") maxResults: Int = 10
    ): Response<ArxivFeed>
}