package com.yaropaul.fdjapp.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaropaul.fdjapp.data.model.Leagues
import com.yaropaul.fdjapp.data.model.Teams
import com.yaropaul.fdjapp.usecase.LeagueUseCase
import com.yaropaul.fdjapp.usecase.TeamUseCase
import com.yaropaul.fdjapp.utils.NetworkUtil
import com.yaropaul.fdjapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

/**
 * MainViewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor (
    private val leagueUseCase : LeagueUseCase,
    private val teamUseCase: TeamUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _leagueData = MutableLiveData<Resource<Leagues>>()
    var leagueData : LiveData<Resource<Leagues>> = _leagueData

    private val _teamsData = MutableLiveData<Resource<Teams>>()
    var teamsData : LiveData<Resource<Teams>> = _teamsData

    /**
     * function to get data of leagues
     */
    fun getLeaguesData() {
        _leagueData.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = leagueUseCase.invoke()
                if (NetworkUtil.hasInternetConnection(context)) {
                    _leagueData.value = handleDataResponseGeneric(response = response)
                }
                else {
                    _leagueData.value = Resource.Error("No Internet Connection")
                }
            } catch (ex: Exception) {
                when(ex){
                    is IOException -> _leagueData.value = Resource.Error("Network Failure")
                    else -> _leagueData.value = Resource.Error("Conversion Error")
                }
            }
        }
    }

    /**
     * function to get data of teams
     */
    fun getTeamsData(searchByID :String) {
        _teamsData.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val response = teamUseCase.invoke(searchByID)
                if (NetworkUtil.hasInternetConnection(context)) {
                    _teamsData.value =  handleDataResponseGeneric(response = response)
                }
                else {
                    _teamsData.value = Resource.Error("No Internet Connection")
                }
            } catch (ex: Exception) {
                when(ex){
                    is IOException -> _teamsData.value = Resource.Error("Network Failure")
                    else -> _teamsData.value = Resource.Error("Conversion Error")
                }
            }
        }
    }

    /**
     * function to handle data  response
     */
    private fun <T> handleDataResponseGeneric(response: Response<T>): Resource<T>? {
        return when {
            response.message().toString().contains("timeout") -> {
                Resource.Error("Timeout")
            }
            response.code() == 402 -> {
                Resource.Error("API Key Limited.")
            }
            response.code() == 404 -> {
                Resource.Error("File not found.")
            }
            response.isSuccessful -> {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("No data available")
            }
            else -> {
                Resource.Error(response.message())
            }
        }
    }
}