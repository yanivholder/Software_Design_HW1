package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance

class SifriTaubIntegrationTest {

    private val injector = Guice.createInjector(SifriTaubModule())
    private val manager = injector.getInstance<SifriTaub>()


}