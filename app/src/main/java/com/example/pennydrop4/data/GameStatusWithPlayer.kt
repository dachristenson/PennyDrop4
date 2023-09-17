package com.example.pennydrop4.data

import androidx.room.Embedded
import androidx.room.Relation
import com.example.pennydrop4.types.Player

data class GameStatusWithPlayer(
    @Embedded val gameStatus: GameStatus,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "playerId"
    )
    val player: Player
)
