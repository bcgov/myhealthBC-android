package ca.bc.gov.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import ca.bc.gov.data.local.entity.PatientEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.time.Instant
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Pinakin Kansara
 */
@HiltAndroidTest
abstract class BaseDataBaseTest {

    @get:Rule
    val hiltRule by lazy { HiltAndroidRule(this) }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var db: MyHealthDataBase

    abstract fun onCreate()

    abstract fun tearDown()

    @Before
    fun setUp() {
        hiltRule.inject()
        onCreate()
    }

    @After
    fun close() {
        db.close()
        tearDown()
    }

    protected fun getPatient() =
        PatientEntity(
            firstName = "Random",
            lastName = "random",
            dateOfBirth = Instant.now(),
            phn = "12333456",
            timeStamp = Instant.now()
        )
}