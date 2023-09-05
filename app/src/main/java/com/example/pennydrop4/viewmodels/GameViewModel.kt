package com.example.pennydrop4.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.pennydrop4.data.*
import com.example.pennydrop4.game.GameHandler
import com.example.pennydrop4.game.TurnEnd
import com.example.pennydrop4.game.TurnResult
import com.example.pennydrop4.types.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.OffsetDateTime


class GameViewModel(application: Application): AndroidViewModel(application) {
    private var clearText = false
    private val repository: PennyDropRepository

    val currentGame = MediatorLiveData<GameWithPlayers>()
    val currentGameStatuses: LiveData<List<GameStatus>>
    val currentPlayer: LiveData<Player>
    val currentStandingsText: LiveData<String>
    val slots: LiveData<List<Slot>>
    val canRoll: LiveData<Boolean>
    val canPass: LiveData<Boolean>

    init {
        this.repository =
            PennyDropDatabase
                .getDatabase(application, viewModelScope)
                .pennyDropDao()
                .let { dao ->
                    PennyDropRepository.getInstance(dao)
                }

        this.currentGameStatuses = this.repository.getCurrentGameStatuses()

        this.currentGame.addSource(
            this.repository.getCurrentGameWithPlayers()
        ) { gameWithPlayers ->
            updateCurrentGame(gameWithPlayers, this.currentGameStatuses.value)
        }

        this.currentGame.addSource(this.currentGameStatuses) { gameStatuses ->
            updateCurrentGame(this.currentGame.value, gameStatuses)
        }

        this.currentPlayer =
            Transformations.map(this.currentGame) { gameWithPlayers ->
                gameWithPlayers?.players?.firstOrNull { it.isRolling }
            }

        this.currentStandingsText =
            Transformations.map(this.currentGame) { gameWithPlayers ->
                gameWithPlayers?.players?.let { players ->
                    this.generateCurrentStandings(players)
                }
            }

        this.slots =
            Transformations.map(this.currentGame) { gameWithPlayers ->
                Slot.mapFromGame(gameWithPlayers?.game)
            }

        this.canRoll = Transformations.map(this.currentPlayer) { player ->
            player?.isHuman == true && currentGame.value?.game?.canRoll == true
        }

        this.canPass = Transformations.map(this.currentPlayer) { player ->
            player?.isHuman == true && currentGame.value?.game?.canPass == true
        }
    }

    suspend fun startGame(playersForNewGame: List<Player>) {
        repository.startGame(playersForNewGame)
    }

    fun roll() {
        val game = this.currentGame.value?.game
        val players = this.currentGame.value?.players
        val currentPlayer = this.currentPlayer.value
        val slots = this.slots.value

        if (game != null && players != null && currentPlayer != null &&
            slots != null && game.canRoll) {
            updateFromGameHandler(
                GameHandler.roll(players, currentPlayer, slots)
            )
        }
    }

    fun pass() {
        val game = this.currentGame.value?.game
        val players = this.currentGame.value?.players
        val currentPlayer = this.currentPlayer.value

        if (game != null && players != null &&
            currentPlayer != null && game.canPass) {
            updateFromGameHandler(
                GameHandler.pass(players, currentPlayer)
            )
        }
    }

    private fun updateFromGameHandler(result: TurnResult) {
        if (result.currentPlayer != null) {
            currentPlayer.value?.addPennies(result.coinChangeCount ?: 0)
            currentPlayer.value = result.currentPlayer

            this.players.forEach { player ->
                player.isRolling = result.currentPlayer == player
            }
        }

        if (result.lastRoll != null) {
            slots.value?.let { currentSlots ->
                updateSlots(result, currentSlots, result.lastRoll)
            }
        }

        currentTurnText.value = generateTurnText(result)
        currentStandingsText.value = generateCurrentStandings(this.players)

        canRoll.value = result.canRoll
        canPass.value = result.canPass

        if (!result.isGameOver && result.currentPlayer?.isHuman == false) {
            canRoll.value = false
            canPass.value = false
        }

        if (!result.isGameOver && result.currentPlayer?.isHuman == false) {
            canRoll.value = false
            canPass.value = false
            playAITurn()
        }
    }

    private fun playAITurn() {
        viewModelScope.launch {
            delay(1000)
            slots.value?.let { currentSlots ->
                val currentPlayer = players.firstOrNull { it.isRolling }

                if (currentPlayer != null && !currentPlayer.isHuman) {
                    GameHandler.playAITurn(
                        players,
                        currentPlayer,
                        currentSlots,
                        canPass.value == true
                    )?.let { result ->
                        updateFromGameHandler(result)
                    }
                }
            }
        }
    }

    private fun updateSlots(
        result: TurnResult,
        currentSlots: List<Slot>,
        lastRoll: Int
    ) {
        if (result.clearSlots) {
            currentSlots.clear()
        }

        currentSlots.firstOrNull { it.lastRolled }?.apply { lastRolled = false }

        currentSlots.getOrNull(lastRoll - 1)?.also { slot ->
            if (!result.clearSlots && slot.canBeFilled) slot.isFilled = true

            slot.lastRolled = true
        }

        slots.notifyChange()
    }

    private fun generateTurnText(result: TurnResult): String {
        if (clearText) currentTurnText.value = ""
        clearText = result.turnEnd != null

        val currentText = currentTurnText.value ?: ""
        val currentPlayerName = result.currentPlayer?.playerName ?: "???"

        return when {
            result.isGameOver ->
                """
                    |Game Over!
                    |$currentPlayerName is the winner!
                    |
                    |${generateCurrentStandings(this.players, "Final Scores:\n")}
                """.trimMargin()
            result.turnEnd == TurnEnd.Bust ->
                "Oh, no!  ${result.previousPlayer?.playerName} rolled a ${result.lastRoll}. They collected ${result.coinChangeCount} pennies for a total of ${result.previousPlayer?.pennies}.\n$currentText"
            result.turnEnd == TurnEnd.Pass ->
                "${result.previousPlayer?.playerName} passed.  They currently have ${result.previousPlayer?.pennies} pennies.\n$currentText"
            result.lastRoll != null ->
                "$currentText\n$currentPlayerName rolled a ${result.lastRoll}."
            else -> ""
        }
    }

    private fun<T> MutableLiveData<List<T>>.notifyChange() {
        this.value = this.value
    }

    private fun generateCurrentStandings(
        players: List<Player>,
        headerText: String = "Current Standings:"
    ) =
        players.sortedBy { it.pennies }.joinToString(
            separator = "\n",
            prefix = "$headerText\n"
        ) {
            "\t${it.playerName} - ${it.pennies} pennies"
        }

    private fun updateCurrentGame(
        gameWithPlayers: GameWithPlayers?,
        gameStatuses: List<GameStatus>?
    ) {
        this.currentGame.value = gameWithPlayers?.updateStatuses(gameStatuses)
    }

    fun updateStatuses(gameStatuses: List<GameStatus>?) {
        if (gameStatuses != null) {
            this.copy(
                players = players.map { player ->
                    gameStatuses
                        .firstOrNull { it.playerId == player.playerId }
                        ?.let { gameStatus ->
                            player.apply {
                                pennies = gameStatus.pennies
                                isRolling = gameStatus.isRolling
                                gamePlayerNumber = gameStatus.gamePlayerNumber
                            }
                        } ?: player
                }.sortedBy { it.gamePlayerNumber }
            )
        } else this
    }
}