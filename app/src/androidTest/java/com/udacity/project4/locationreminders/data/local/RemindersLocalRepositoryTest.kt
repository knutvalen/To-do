package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        database = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java,
            "locationReminders.db"
        ).build()

        remindersLocalRepository = RemindersLocalRepository(database.reminderDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveReminder_getReminder() = runBlocking {
        val reminder1 = ReminderDTO(
            "title1",
            "description1",
            "location1",
            55.55,
            44.44
        )

        remindersLocalRepository.saveReminder(reminder1)
        val value = remindersLocalRepository.getReminder(reminder1.id)

        assertThat(value as Result.Success, notNullValue())
        assertThat(value.data.id, `is`(reminder1.id))
        assertThat(value.data.title, `is`(reminder1.title))
        assertThat(value.data.description, `is`(reminder1.description))
        assertThat(value.data.location, `is`(reminder1.location))
        assertThat(value.data.latitude, `is`(reminder1.latitude))
        assertThat(value.data.longitude, `is`(reminder1.longitude))
    }

}