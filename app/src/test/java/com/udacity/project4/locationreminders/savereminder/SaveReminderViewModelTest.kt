package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
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

    @Test
    fun onClear_clearsLiveData() {
        saveReminderViewModel.onClear()

        assert(saveReminderViewModel.reminderTitle.value == null)
        assert(saveReminderViewModel.reminderDescription.value == null)
        assert(saveReminderViewModel.reminderSelectedLocationStr.value == null)
        assert(saveReminderViewModel.selectedPOI.value == null)
        assert(saveReminderViewModel.latitude.value == null)
        assert(saveReminderViewModel.longitude.value == null)
    }

}