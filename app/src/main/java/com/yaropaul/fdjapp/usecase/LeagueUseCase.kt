package com.yaropaul.fdjapp.usecase

import com.yaropaul.fdjapp.repository.MainRepositoryImpl
import javax.inject.Inject

class LeagueUseCase @Inject constructor(private val repository: MainRepositoryImpl)  {
    suspend operator fun invoke() = repository.getAllLeagues()
}