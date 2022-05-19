package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import org.junit.jupiter.api.Test

class TempTests {
    private val injector = Guice.createInjector(SifriTaubModule())
    private val manager = injector.getInstance<SifriTaub>()

    @Test
    fun temp() {

    }
}