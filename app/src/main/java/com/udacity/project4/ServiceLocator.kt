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
    var dataSource: ReminderDataSource? = null
        @VisibleForTesting set

    fun provideDataSource(context: Context): ReminderDataSource {
        synchronized(this) {
            return dataSource ?: createDataSource(context)
        }
    }

    private fun createDataSource(context: Context): ReminderDataSource {
        val dao = dao ?: LocalDB.createRemindersDao(context)
        val dataSource = RemindersLocalRepository(dao)
        this.dataSource = dataSource
        return dataSource
    }

    @VisibleForTesting
    fun resetDataSource() {
        synchronized(lock) {
            runBlocking {
                dao?.deleteAllReminders()
            }

            dao = null
            dataSource = null
        }
    }

}