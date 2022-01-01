package database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SavedUserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(user: SavedUser): Long

    @Delete
    fun delete(user: SavedUser)

    @Query("SELECT * from saved_users ORDER BY username ASC")
    fun getAllSavedUsers(): LiveData<List<SavedUser>>

    @Query("SELECT COUNT(*)!=0 FROM saved_users WHERE username = :username")
    fun getIsUsernameInFavourites(username: String): Boolean
}