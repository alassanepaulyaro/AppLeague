package com.yaropaul.fdjapp.repository

import com.yaropaul.fdjapp.BuildConfig
import com.yaropaul.fdjapp.data.DataApi
import com.yaropaul.fdjapp.data.model.Leagues
import com.yaropaul.fdjapp.data.model.Teams
import com.yaropaul.fdjapp.utils.Resource
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(private val dataApi: DataApi) : MainRepository {
    override suspend fun getAllLeagues(): Resource<Leagues> {
        return try {
            val response = dataApi.getAllLeagues(BuildConfig.API_KEY)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.Success(it)
                } ?: Resource.Error("Error",null)
            } else {
                Resource.Error("Error No data!",null)
            }
        } catch (e: Exception) {
            Resource.Error("Couldn't connect to  the servers. Check your internet connection",null)
        }
    }

    override suspend fun getAllTeams(searchByID :String): Resource<Teams> {
        return try {
            val response = dataApi.getAllTeams(BuildConfig.API_KEY, searchByID)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.Success(it)
                } ?: Resource.Error("Error",null)
            } else {
                Resource.Error("Error No data!",null)
            }
        } catch (e: Exception) {
            Resource.Error("Couldn't connect to  the servers. Check your internet connection",null)
        }
    }
}


