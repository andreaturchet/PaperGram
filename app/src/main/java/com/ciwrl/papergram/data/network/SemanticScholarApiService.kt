package com.ciwrl.papergram.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class SemanticScholarPaper(val paperId: String?, val primaryImage: String?)


data class BatchRequest(val ids: List<String>)

interface SemanticScholarApiService {
    @GET("paper/arXiv:{paperId}")
    suspend fun getPaperDetails(
        @Path("paperId") paperId: String,
        @Query("fields") fields: String = "primaryImage"
    ): Response<SemanticScholarPaper>

    @POST("paper/batch")
    suspend fun getPaperDetailsBatch(
        @Body requestBody: BatchRequest,
        @Query("fields") fields: String = "paperId,primaryImage"
    ): Response<List<SemanticScholarPaper?>>
}