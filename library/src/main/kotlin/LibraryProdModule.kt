import Fakes.SecureStorageFake
import com.google.inject.Provides
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import il.ac.technion.cs.softwaredesign.storage.SecureStorageModule
import impl.DefaultPersistentMap
import java.util.concurrent.CompletableFuture

class LibraryProdModule: KotlinModule() {
    override fun configure() {
        install(SecureStorageModule())
//        bind<SecureStorage>().to<SecureStorageFake>()
        bind<PersistentMap>().to<DefaultPersistentMap>()
    }

    @Provides
    fun provideSecureStorage(secureStorageFactory: SecureStorageFactory): CompletableFuture<SecureStorage> {
        return secureStorageFactory.open(ByteArray(0))
    }
}