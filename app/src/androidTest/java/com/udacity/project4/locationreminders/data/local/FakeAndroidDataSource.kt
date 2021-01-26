package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeAndroidDataSource : ReminderDataSource {

    private var dataSource: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("ERROR:FakeAndroidDataSource:getReminders()")
        }

        val list: List<ReminderDTO> = dataSource.values.toList()
        return Result.Success(list)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        dataSource[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("ERROR:FakeAndroidDataSource:getReminder(id: $id)")
        }

        dataSource[id]?.let {
            return Result.Success(it)
        }

        return Result.Error("No existing reminder with ID $id")
    }

    override suspend fun deleteAllReminders() {
        dataSource.clear()
    }

}