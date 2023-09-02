package com.example.pennydrop4.game

import com.example.pennydrop4.types.Player
import com.example.pennydrop4.types.Slot
import com.example.pennydrop4.types.fullSlots

data class AI(
    val name: String,
    val rollAgain: (slots: List<Slot>) -> Boolean
) {
    override fun toString() = name

    fun toPlayer() = Player(
        playerName = name,
        isHuman = false,
        selectedAI = this
    )

    companion object {
        @JvmStatic
        val basicAI = listOf(
            AI("TwoFace") { slots -> slots.fullSlots() < 3 || (slots.fullSlots() == 3 && coinFlipIsHeads()) },
            AI("No Go Noah") { slots -> slots.fullSlots() == 0 },
            AI("Bail Out Beulah") { slots -> slots.fullSlots() <= 1 },
            AI("Fearful Fred") { slots -> slots.fullSlots() <= 2 },
            AI("Even Steven") { slots -> slots.fullSlots() <= 3 },
            AI("Riverboat Ron") { slots -> slots.fullSlots() <= 4 },
            AI("Sammy Sixes") { slots -> slots.fullSlots() <= 5 },
            AI("Random Rachael") { coinFlipIsHeads() }
        )
    }
}

fun coinFlipIsHeads() = (Math.random() * 2).toInt() == 0
