package com.example.pennydrop4.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pennydrop4.types.Player
import kotlinx.coroutines.CoroutineScope

@Database(
    // We'll add the Entity classes in a bit
    entities = [Game::class, Player::class, GameState::class],
    version = 1,
    exportSchema = false
)

abstract class PennyDropDatabase: RoomDatabase() {
    abstract fun pennyDropDao(): PennyDropDao

    companion object {
        @Volatile
        private var instance: PennyDropDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): PennyDropDatabase =
            this.instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    PennyDropDatabase::class.java,
                    "PennyDropDatabase"
                ).build()

                this.instance = instance

                instance // This is returned from the synchronized block
            }
    }
}