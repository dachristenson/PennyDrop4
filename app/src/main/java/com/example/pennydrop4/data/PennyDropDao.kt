package com.example.pennydrop4.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pennydrop4.types.Player
import java.time.OffsetDateTime

@Dao
abstract class PennyDropDao {
    @Query("SELECT * FROM players WHERE playerName = :playerName")
    abstract fun getPlayer(playerName: String): Player?

    @Transaction
    @Query(
        """
        SELECT * FROM game_statuses gs
        WHERE gs.gameId IN (
            SELECT gameId FROM games
            WHERE gameState = :finishedGameState
        )
        """
    )
    abstract fun getCompletedGameStatusesWithPlayers(
        finishedGameState: GameState = GameState.Finished
    ): LiveData<List<GameStatusWithPlayer>>

    @Insert
    abstract suspend fun insertGame(game: Game): Long

    @Insert
    abstract suspend fun insertPlayer(player:Player): Long

    @Insert
    abstract suspend fun insertPlayers(players: List<Player>): List<Long>

    @Insert
    abstract suspend fun insertGameStatuses(gameStatuses: List<GameStatus>)

    @Update
    abstract suspend fun updateGameStatuses(gameStatuses: List<GameStatus>)

    @Update
    abstract suspend fun updateGame(game: Game)

    @Transaction
    @Query("SELECT * FROM games ORDER BY startTime DESC LIMIT 1")
    abstract fun getCurrentGameWithPLayers(): LiveData<GameWithPlayers>

    @Transaction
    @Query(
        """
        SELECT * FROM game_statuses
        WHERE gameId = (
            SELECT gameId FROM games
            ORDER BY startTime DESC
            LIMIT 1)
        ORDER BY gamePlayerNumber
        """
    )
    abstract fun getCurrentGameStatuses(): LiveData<List<GameStatus>>

    @Query("""
        UPDATE games
        SET endTime = :endDate, gameState = :gameState
        WHERE endTime IS NULL
        """)
    abstract suspend fun closeOpenGames(
        endDate: OffsetDateTime = OffsetDateTime.now(),
        gameState: GameState = GameState.Cancelled
    )

    @Transaction
    open suspend fun startGame(players: List<Player>, pennyCount: Int?): Long {
        this.closeOpenGames()

        val gameId = this.insertGame(
            Game(
                gameState = GameState.Started,
                currentTurnText = "This game has begun!\n",
                canRoll = true
            )
        )

        val playerIds = players.map { player ->
            getPlayer(player.playerName)?.playerId ?: insertPlayer(player)
        }

        this.insertGameStatuses(
            playerIds.mapIndexed { index, playerId ->
                GameStatus(
                    gameId,
                    playerId,
                    index,
                    index == 0,
                    pennyCount ?: Player.defaultPennyCount
                )
            }
        )

        return gameId
    }

    @Transaction
    open suspend fun updateGameAndStatuses(
        game: Game,
        statuses: List<GameStatus>
    ) {
        this.updateGame(game)
        this.updateGameStatuses(statuses)
    }
}
