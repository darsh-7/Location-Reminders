package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainAndroidTestCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

    //    TODO: Add testing implementation to the RemindersLocalRepository.kt

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainAndroidTestCoroutineRule()

    val reminder = ReminderDTO(
        title = "Reminder",
        description = "Description",
        location = "location",
        latitude = 0.0,
        longitude = 0.0
    )

    private lateinit var database: RemindersDatabase
    private lateinit var remindersDao: RemindersDao
    private lateinit var repository: RemindersLocalRepository

    @Before
    fun setupTest() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        remindersDao = database.reminderDao()
        repository = RemindersLocalRepository(remindersDao, Dispatchers.Main)
    }

    @After
    fun closeDB() {
        database.close()
    }

    //use the RemindersLocalRepository to add and get a reminder by id
    @Test
    fun saveReminderAndGetByID() = mainCoroutineRule.runBlockingTest {

        repository.saveReminder(reminder)
        val reminderLoaded = repository.getReminder(reminder.id) as Result.Success<ReminderDTO>
        val loaded = reminderLoaded.data


        assertThat(loaded, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.location, `is`(reminder.location))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    //use the RemindersLocalRepository to delete all reminders from the dao
    @Test
    fun deleteAllReminders() = mainCoroutineRule.runBlockingTest {
        repository.saveReminder(reminder)
        repository.deleteAllReminders()
        val reminders = repository.getReminders() as Result.Success<List<ReminderDTO>>
        val data = reminders.data
        assertThat(data.isEmpty(), `is`(true))

    }

    //use the RemindersLocalRepository to Query unexciting reminder and check for error
    @Test
    fun noRemindersFoundGetReminderById() = mainCoroutineRule.runBlockingTest {
        val reminder = repository.getReminder("3") as Result.Error
        assertThat(reminder.message, notNullValue())
        assertThat(reminder.message, `is`("Reminder not found!"))
    }
}