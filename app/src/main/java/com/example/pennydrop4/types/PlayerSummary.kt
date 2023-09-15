package com.example.pennydrop4.types

data class PlayerSummary(
    val id: Long,
    val name: String,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val isHuman: Boolean = true
)
