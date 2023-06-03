package com.yaropaul.fdjapp.repository

import com.yaropaul.fdjapp.data.model.Leagues
import com.yaropaul.fdjapp.data.model.Teams
import retrofit2.Response

interface MainRepository {
    suspend fun getAllLeagues() : Response<Leagues>
    suspend fun getAllTeams(searchByID :String) : Response<Teams>
}