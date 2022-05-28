import com.google.inject.Provides
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import il.ac.technion.cs.softwaredesign.storage.SecureStorageModule
import impl.DefaultPersistentMap

class LibraryProdModule: KotlinModule() {
    override fun configure() {
        install(SecureStorageModule())
        bind<PersistentMap>().to<DefaultPersistentMap>()
    }

    @Provides
    fun provideSecureStorage(secureStorageFactory: SecureStorageFactory): SecureStorage {
        val secureStorage = secureStorageFactory.open(ByteArray(0))
        return secureStorage
    }
}