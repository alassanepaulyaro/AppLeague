package com.yaropaul.fdjapp.data

import com.yaropaul.fdjapp.data.model.Leagues
import com.yaropaul.fdjapp.data.model.Teams
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DataApi {
    @GET("/api/v1/json/{apiKey}/all_leagues.php")
    suspend fun getAllLeagues(@Path("apiKey") apiKey: String) : Response<Leagues>

    @GET("/api/v1/json/{apiKey}/lookup_all_teams.php")
    suspend fun getAllTeams(
        @Path("apiKey") apiKey: String,
        @Query("id") searchByID :String) : Response<Teams>
}