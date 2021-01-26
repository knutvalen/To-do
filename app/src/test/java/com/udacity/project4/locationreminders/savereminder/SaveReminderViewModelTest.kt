package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(maxSdk = Build.VERSION_CODES.P, minSdk = Build.VERSION_CODES.P)
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var reminderDataSource: FakeDataSource

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
        saveReminderViewModel = SaveReminderViewModel(getApplicationContext(), reminderDataSource)
    }

    @After
    fun destroy() {
        stopKoin()
    }

    @Test
    fun onClear_clearsLiveData() {
        saveReminderViewModel.onClear()

        assert(saveReminderViewModel.reminderTitle.getOrAwaitValue() == null)
        assert(saveReminderViewModel.reminderDescription.getOrAwaitValue() == null)
        assert(saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue() == null)
        assert(saveReminderViewModel.selectedPOI.getOrAwaitValue() == null)
        assert(saveReminderViewModel.latitude.getOrAwaitValue() == null)
        assert(saveReminderViewModel.longitude.getOrAwaitValue() == null)
    }

    @Test
    fun check_loading() {
        val reminderDataItem1 = ReminderDataItem(
            "title1",
            "description1",
            "location1",
            55.55,
            44.44
        )
        val reminderDataItem2 = ReminderDataItem(
            "title2",
            "description2",
            "location2",
            55.55,
            44.44
        )
        val reminderDataItem3 = ReminderDataItem(
            "title3",
            "description3",
            "location3",
            55.55,
            44.44
        )

        mainCoroutineRule.pauseDispatcher()

        runBlocking {
            saveReminderViewModel.validateAndSaveReminder(reminderDataItem1)
            saveReminderViewModel.validateAndSaveReminder(reminderDataItem2)
            saveReminderViewModel.validateAndSaveReminder(reminderDataItem3)
        }

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(saveReminderViewModel.navigationCommand.getOrAwaitValue(), `is`(NavigationCommand.Back))
    }

    @Test
    fun validateEnteredData_validData_returnsTrue() {
        val reminderDataItem1 = ReminderDataItem(
            "title1",
            "description1",
            "location1",
            55.55,
            44.44
        )

        val value = saveReminderViewModel.validateEnteredData(reminderDataItem1)

        assertThat(value, `is`(true))
    }

    @Test
    fun validateEnteredData_invalidTitle_returnsFalse() {
        val reminderDataItem1 = ReminderDataItem(
            null,
            "description1",
            "location1",
            55.55,
            44.44
        )

        val value = saveReminderViewModel.validateEnteredData(reminderDataItem1)

        assertThat(value, `is`(false))
    }

    @Test
    fun validateEnteredData_invalidLocation_returnsFalse() {
        val reminderDataItem1 = ReminderDataItem(
            "title1",
            "description1",
            "",
            55.55,
            44.44
        )

        val value = saveReminderViewModel.validateEnteredData(reminderDataItem1)

        assertThat(value, `is`(false))
    }

}