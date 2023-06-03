package com.yaropaul.fdjapp.repository

import com.yaropaul.fdjapp.data.model.Leagues
import com.yaropaul.fdjapp.data.model.Teams
import com.yaropaul.fdjapp.utils.Resource

interface MainRepository {
    suspend fun getAllLeagues() : Resource<Leagues>
    suspend fun getAllTeams(searchByID :String) : Resource<Teams>
}