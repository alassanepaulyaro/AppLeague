package com.yaropaul.fdjapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaropaul.fdjapp.data.model.Leagues
import com.yaropaul.fdjapp.data.model.Teams
import com.yaropaul.fdjapp.usecase.LeagueUseCase
import com.yaropaul.fdjapp.usecase.TeamUseCase
import com.yaropaul.fdjapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MainViewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val leagueUseCase: LeagueUseCase,
    private val teamUseCase: TeamUseCase
) : ViewModel() {

    private val _leagueData = MutableLiveData<Resource<Leagues>>()
    var leagueData: LiveData<Resource<Leagues>> = _leagueData

    private val _teamsData = MutableLiveData<Resource<Teams>>()
    var teamsData: LiveData<Resource<Teams>> = _teamsData

    /**
     * function to get data of leagues
     */
    fun getLeaguesData() {
        _leagueData.postValue(Resource.Loading())
        viewModelScope.launch {
            _leagueData.value = leagueUseCase.invoke()
        }
    }

    /**
     * function to get data of teams
     */
    fun getTeamsData(searchByID: String) {
        if (searchByID.isEmpty()) {
            return
        }
        _teamsData.postValue(Resource.Loading())
        viewModelScope.launch {
            _teamsData.value = teamUseCase.invoke(searchByID)
        }
    }
}