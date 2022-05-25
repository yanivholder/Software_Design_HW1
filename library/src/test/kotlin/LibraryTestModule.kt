package library

import Fakes.SecureStorageFake
import PersistentMap
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import impl.DefaultPersistentMap

class LibraryTestModule : KotlinModule() {
    override fun configure() {
        bind<PersistentMap>().to<DefaultPersistentMap>()
        bind<SecureStorage>().to<SecureStorageFake>()
    }
}