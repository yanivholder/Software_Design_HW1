package il.ac.technion.cs.softwaredesign

import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.impl.BookInfo
import il.ac.technion.cs.softwaredesign.impl.DefaultBookManager
import library.PersistentMap
import library.impl.DefaultPersistentMap

class BookManagerModule: KotlinModule() {
    override fun configure() {
        bind<PersistentMap>().to<DefaultPersistentMap>()
    }
}