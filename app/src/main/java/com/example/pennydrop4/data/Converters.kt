package com.example.pennydrop4.data

import androidx.room.TypeConverter
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import android.text.TextUtils
import com.example.pennydrop4.game.AI

class Converters {
    private val formatter = ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun toOffsetDateTime(value: String?) = value?.let {
        formatter.parse(it, OffsetDateTime::from)
    }

    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime?) = date?.format(formatter)

    @TypeConverter
    fun fromGateStateToInt(gameState: GameState?) =
        (gameState ?: GameState.Unknown).ordinal

    @TypeConverter
    fun fromIntToGameState(gameStateInt: Int?) =
        GameState.values().let { gameStateValues ->
            if (gameStateInt != null &&
                gameStateValues.any { it.ordinal == gameStateInt }
                ) {
                GameState.values()[gameStateInt]
            } else GameState.Unknown
        }

    @TypeConverter
    fun toIntList(value: String?) = value?.split(",")?.let {
        it
            .filter { numberString -> !TextUtils.isEmpty(numberString) }
            .map { numberString -> numberString.toInt() }
    } ?: emptyList()

    @TypeConverter
    fun fromListOfIntToString(numbers: List<Int>?) =
        numbers?.joinToString(",") ?: ""

    @TypeConverter
    fun toAI(aiId: Long?) = AI.basicAI.firstOrNull { it.aiId == aiId }

    @TypeConverter
    fun fromAiToId(ai: AI?) = ai?.aiId
}
