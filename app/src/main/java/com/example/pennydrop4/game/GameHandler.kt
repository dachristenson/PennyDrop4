package com.example.pennydrop4.game

import com.example.pennydrop4.types.Player
import com.example.pennydrop4.types.Slot
import kotlin.random.Random

object GameHandler {
    fun roll(
        players: List<Player>,
        currentPlayer: Player,
        slots: List<Slot>
    ): TurnResult =
        rollDie().let { lastRoll ->
            slots.getOrNull(lastRoll - 1)?.let { slot ->
                if (slot.isFilled) {
                    // Player busts, play continues to next player
                    TurnResult(
                        lastRoll,
                        coinChangeCount = slots.count { it.isFilled },
                        clearSlots = true,
                        turnEnd = TurnEnd.Bust,
                        previousPlayer = currentPlayer,
                        currentPlayer = nextPlayer(players, currentPlayer),
                        playerChanged = true,
                        canRoll = true,
                        canPass = false
                    )
                } else {
                    if (!currentPlayer.penniesLeft(true)) {
                        // Player wins
                        TurnResult(
                            lastRoll,
                            currentPlayer = currentPlayer,
                            coinChangeCount = -1,
                            isGameOver = true,
                            turnEnd = TurnEnd.Win,
                            canRoll = false,
                            canPass = false
                        )
                    } else {
                        // Game continues
                        TurnResult(
                            lastRoll,
                            currentPlayer= currentPlayer,
                            canRoll = true,
                            canPass = true,
                            coinChangeCount = -1
                            )
                    }
                }
            } ?: TurnResult(isGameOver = true)
        }

    fun pass(
        players: List<Player>,
        currentPlayer: Player
    ) = TurnResult(
        previousPlayer = currentPlayer,
        currentPlayer = nextPlayer(players, currentPlayer),
        playerChanged = true,
        turnEnd = TurnEnd.Pass,
        canRoll = true,
        canPass = false
    )

    private fun rollDie(sides: Int = 6) = Random.nextInt(1, sides + 1)

    private fun nextPlayer(
        players: List<Player>,
        currentPlayer: Player
    ): Player? {
        val currentIndex = players.indexOf(currentPlayer)
        val nextIndex = (currentIndex + 1) % players.size

        return players[nextIndex]
    }

    fun playAITurn(
        players: List<Player>,
        currentPlayer: Player,
        slots: List<Slot>,
        canPass: Boolean = false
    ): TurnResult? =
        currentPlayer.selectedAI?.let { ai ->
            if (!canPass || ai.rollAgain(slots)) {
                roll(players, currentPlayer, slots)
            } else {
                pass(players, currentPlayer)
            }
        }
}