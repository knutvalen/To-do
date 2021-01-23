package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var reminderDataSource: FakeDataSource

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        reminderDataSource = FakeDataSource()
        FirebaseApp.initializeApp(getApplicationContext())
        remindersListViewModel = RemindersListViewModel(getApplicationContext(), reminderDataSource)
    }

    @Test
    fun loadReminders_loadsSuccessfully() {
        val reminder1 = ReminderDTO("title1", "description1", "location1", 55.55, 44.44)
        val reminder2 = ReminderDTO("title2", "description2", "location2", 55.55, 44.44)
        val reminder3 = ReminderDTO("title3", "description3", "location3", 55.55, 44.44)

        runBlocking {
            reminderDataSource.saveReminder(reminder1)
            reminderDataSource.saveReminder(reminder2)
            reminderDataSource.saveReminder(reminder3)
        }

        remindersListViewModel.loadReminders()

        val value = remindersListViewModel.remindersList.getOrAwaitValue()

        assertThat(value, not(emptyList()))
        assertThat(value.count(), `is`(3))
        assertThat(value[0].id, `is`(reminder1.id))
        assertThat(value[1].id, `is`(reminder2.id))
        assertThat(value[2].id, `is`(reminder3.id))
    }

}