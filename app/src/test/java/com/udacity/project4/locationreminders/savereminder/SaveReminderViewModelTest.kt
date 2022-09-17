package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest {

    //TODO: provide testing to the SaveReminderView and its live data objects

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    var mainCoroutineRule = MainCoroutineRule()


    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var datasource: FakeDataSource

    @Before
    fun setUpTest() {
        stopKoin()
        datasource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), datasource)
    }

    val reminderDataItem = ReminderDataItem(
        "Title",
        "Description",
        "Location",
        0.0,
        0.0)

    val reminderDataItem2 = ReminderDataItem(
        "",
        "Description",
        "Location",
        0.0,
        0.0)

    //checking if saveReminder fun work right when it's called
    // by checking showLoading value when saveReminder called

    @Test
    fun check_loading() = mainCoroutineRule.runBlockingTest {

        mainCoroutineRule.pauseDispatcher()

        saveReminderViewModel.saveReminder(reminderDataItem)

        assertThat(saveReminderViewModel.showLoading.value, `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.value, `is`(false))

    }

    //checking if validateEnteredData fun work right when the title is empty
    @Test
    fun shouldReturnError() = mainCoroutineRule.runBlockingTest {

        val isDataValid = saveReminderViewModel.validateEnteredData(reminderDataItem2)

        assertThat(isDataValid, `is`(false))

        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title)
        )
    }
}