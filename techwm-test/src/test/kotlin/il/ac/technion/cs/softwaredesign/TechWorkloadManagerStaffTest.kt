package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.misfitlabs.kotlinguice4.getInstance
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TechWorkloadManagerStaffTest {
    private val injector = Guice.createInjector(SifriTaubModule())
    private val manager = injector.getInstance<SifriTaub>()

    @Test
    fun `a non-existing user throws exception on authenticate`() {
        val username = "non-existing"
        val password = "non-existing"

        assertThrows<IllegalArgumentException> {
            manager.authenticate(username, password)
        }
    }

    @Test
    fun `first user is successfully registered`() {
        val username = "user-a"
        val password = "123456"

        assertDoesNotThrow {
            manager.register(username, password,true, 25)
        }
    }
}