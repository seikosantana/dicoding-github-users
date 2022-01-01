package database

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SavedUsersRepository(application: Application) {
    private val mSavedUsersDao: SavedUserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = SavedUserRoomDatabase.getDatabase(application)
        mSavedUsersDao = db.savedUserDao()
    }

    fun getAllSavedUsers(): LiveData<List<SavedUser>> = mSavedUsersDao.getAllSavedUsers()

    fun insert(user: SavedUser) {
        executorService.execute {
            mSavedUsersDao.insert(user) != 1L
        }
    }

    fun delete(user: SavedUser) {
        executorService.execute { mSavedUsersDao.delete(user)}
    }

    fun findIfUserIsInFavourite(username: String, resultCallback: (Boolean) -> Unit) {
        executorService.execute {
            var result: Boolean = mSavedUsersDao.getIsUsernameInFavourites(username)
            resultCallback(result)
        }
    }

}