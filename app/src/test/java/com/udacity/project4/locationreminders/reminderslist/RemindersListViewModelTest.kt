package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var reminderDataSource: FakeDataSource
    private lateinit var reminderList: List<ReminderDTO>

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        reminderDataSource = FakeDataSource()
        FirebaseApp.initializeApp(getApplicationContext())
        remindersListViewModel = RemindersListViewModel(getApplicationContext(), reminderDataSource)

        val reminder1 = ReminderDTO(
            "title1",
            "description1",
            "location1",
            55.55,
            44.44
        )
        val reminder2 = ReminderDTO(
            "title2",
            "description2",
            "location2",
            55.55,
            44.44
        )
        val reminder3 = ReminderDTO(
            "title3",
            "description3",
            "location3",
            55.55,
            44.44
        )

        reminderList = listOf(reminder1, reminder2, reminder3)

        runBlocking {
            reminderDataSource.saveReminder(reminder1)
            reminderDataSource.saveReminder(reminder2)
            reminderDataSource.saveReminder(reminder3)
        }
    }

    @After
    fun destroy() {
        stopKoin()
    }

    @Test
    fun loadReminders_loadsSuccessfully() {
        remindersListViewModel.loadReminders()

        val value = remindersListViewModel.remindersList.getOrAwaitValue()

        assertThat(value.count(), `is`(3))
        assertThat(value[0].id, `is`(reminderList[0].id))
        assertThat(value[1].id, `is`(reminderList[1].id))
        assertThat(value[2].id, `is`(reminderList[2].id))
    }

    @Test
    fun loadReminders_dataSourceUnavailable_remindersListIsNull() {
        reminderDataSource.setReturnError(true)
        remindersListViewModel.loadReminders()

        assert(remindersListViewModel.showLoading.getOrAwaitValue() == false)
        assert(remindersListViewModel.remindersList.value == null)
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(), `is`("Test error"))
    }

}