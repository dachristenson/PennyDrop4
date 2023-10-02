package com.example.pennydrop4.data

import com.example.pennydrop4.types.Player

class PennyDropRepository(private val pennyDropDao: PennyDropDao) {

    fun getCompletedGameStatusesWithPlayers() = pennyDropDao.getCompletedGameStatusesWithPlayers()

    fun getCurrentGameWithPlayers() =
        pennyDropDao.getCurrentGameWithPLayers()

    fun getCurrentGameStatuses() =
        pennyDropDao.getCurrentGameStatuses()

    suspend fun startGame(players: List<Player>, pennyCount: Int? = null) =
        pennyDropDao.startGame(players, pennyCount)

    suspend fun updateGameAndStatuses(
        game: Game,
        statuses: List<GameStatus>
    ) = pennyDropDao.updateGameAndStatuses(game, statuses)

    companion object {
        @Volatile
        private var instance: PennyDropRepository? = null

        fun getInstance(pennyDropDao: PennyDropDao) =
            this.instance ?: synchronized(this) {
                instance ?: PennyDropRepository(pennyDropDao).also {
                    instance = it
                }
            }
    }
}
