package com.example.pennydrop4.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.pennydrop4.data.PennyDropDatabase
import com.example.pennydrop4.data.PennyDropRepository
import com.example.pennydrop4.types.PlayerSummary

class RankingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PennyDropRepository

    val playerSummaries: LiveData<List<PlayerSummary>>

    init {
        this.repository = PennyDropDatabase
            .getDatabase(application, viewModelScope)
            .pennyDropDao()
            .let { dao ->
                PennyDropRepository.getInstance(dao)
            }

        playerSummaries =
            this.repository.getCompletedGameStatusesWithPlayers().map { statusesWithPlayers ->
                statusesWithPlayers
                    .groupBy { it.player }
                    .map { (player, statuses) ->
                        PlayerSummary(
                            player.playerId,
                            player.playerName,
                            statuses.count(),
                            statuses.count { it.gameStatus.pennies == 0 },
                            player.isHuman
                        )
                    }
                    .sortedWith(compareBy({ -it.wins }, { -it.gamesPlayed }))
            }
    }
}