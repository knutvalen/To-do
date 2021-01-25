package com.udacity.project4

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.runBlocking

object ServiceLocator {

    private val lock = Any()
    private var dao: RemindersDao? = null
    @Volatile
    var repository: ReminderDataSource? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): ReminderDataSource {
        synchronized(this) {
            return repository ?: createRepository(context)
        }
    }

    private fun createRepository(context: Context): ReminderDataSource {
        val dao = dao ?: LocalDB.createRemindersDao(context)
        val repository = RemindersLocalRepository(dao)
        this.repository = repository
        return repository
    }

    @VisibleForTesting
    fun resetDataSource() {
        synchronized(lock) {
            runBlocking {
                dao?.deleteAllReminders()
            }

            dao = null
            repository = null
        }
    }

}