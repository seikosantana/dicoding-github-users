package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedUser::class], version = 1)
abstract class SavedUserRoomDatabase : RoomDatabase() {

    abstract fun savedUserDao(): SavedUserDao

    companion object {
        @Volatile
        private var INSTANCE: SavedUserRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): SavedUserRoomDatabase {
            if (INSTANCE == null) {
                synchronized(SavedUserRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        SavedUserRoomDatabase::class.java, "saved_user_database").build()
                }
            }
            return INSTANCE as SavedUserRoomDatabase
        }
    }
}