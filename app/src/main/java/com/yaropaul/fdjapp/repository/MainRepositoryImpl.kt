package com.yaropaul.fdjapp.repository

import com.yaropaul.fdjapp.BuildConfig
import com.yaropaul.fdjapp.data.DataApi
import com.yaropaul.fdjapp.data.model.Leagues
import com.yaropaul.fdjapp.data.model.Teams
import retrofit2.Response
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(private val dataApi: DataApi) : MainRepository {
    override suspend fun getAllLeagues(): Response<Leagues> {
        return dataApi.getAllLeagues(BuildConfig.API_KEY)
    }

    override suspend fun getAllTeams(searchByID :String): Response<Teams> {
        return dataApi.getAllTeams(BuildConfig.API_KEY ,searchByID)
    }
}


