package com.udacity.project4.locationreminders.data.local

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        database = Room.databaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java,
            "locationReminders.db"
        ).build()
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

        database.reminderDao().saveReminder(reminder1)
        val value = database.reminderDao().getReminderById(reminder1.id)

        assertThat(value as ReminderDTO, notNullValue())
        assertThat(value.id, `is`(reminder1.id))
        assertThat(value.title, `is`(reminder1.title))
        assertThat(value.description, `is`(reminder1.description))
        assertThat(value.location, `is`(reminder1.location))
        assertThat(value.latitude, `is`(reminder1.latitude))
        assertThat(value.longitude, `is`(reminder1.longitude))
    }

}