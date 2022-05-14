package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class SifriTaubTest {

    // TODO - create mock for UserManager and BookManager and send it to SifriTaub

    private val injector = Guice.createInjector(SifriTaubModule())
    private val sifri = injector.getInstance<SifriTaub>()


    @Test
    fun `authenticate`() {

    }

    @Test
    fun `register`() {

    }

    @Test
    fun `user Information`() {

    }

    @Test
    fun `add Book To Catalog`() {

    }

    @Test
    fun `get Book Description`() {

    }

    @Test
    fun `list Book Ids`() {

    }

}