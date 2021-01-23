package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private var dataSource: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()

    private var shouldReturnError = false

//    TODO: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        val list: List<ReminderDTO> = dataSource.values.toList()
        return Result.Success(list)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        dataSource[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        dataSource[id]?.let {
            return Result.Success(it)
        }

        return Result.Error("No existing reminder with ID $id")
    }

    override suspend fun deleteAllReminders() {
        dataSource.clear()
    }


}