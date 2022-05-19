package library

import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage

class PersistentMapTestModule : KotlinModule() {
    override fun configure() {
        bind<SecureStorage>().to<SecureStorageFake>()
    }
}