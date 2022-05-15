package library

import com.google.inject.Provides
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.impl.SecureStorageFactoryImpl

class PersistantMapModule: KotlinModule() {
    override fun configure() { }

    @Provides
    fun provideSecureStorage(): SecureStorage {
        val secureStorage = SecureStorageFactoryImpl().open(ByteArray(0));
        return secureStorage;
    }
}