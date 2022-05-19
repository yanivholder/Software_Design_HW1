package il.ac.technion.cs.softwaredesign

import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.impl.DefaultBookManager
import il.ac.technion.cs.softwaredesign.impl.DefaultTokenManager
import il.ac.technion.cs.softwaredesign.impl.DefaultUserManager
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import il.ac.technion.cs.softwaredesign.storage.SecureStorageModule
import il.ac.technion.cs.softwaredesign.storage.impl.SecureStorageFactoryImpl
import il.ac.technion.cs.softwaredesign.storage.impl.SecureStorageImpl
import library.PersistentMap
import library.impl.DefaultPersistentMap


class SifriTaubModule: KotlinModule() {

    override fun configure() {
        bind<UserManager>().to<DefaultUserManager>()
        bind<BookManager>().to<DefaultBookManager>()
        bind<TokenManager>().to<DefaultTokenManager>()
        bind<PersistentMap>().to<DefaultPersistentMap>()
//        bind<SecureStorage>().to<SSF>()
        bind<SecureStorage>().to<SecureStorageImpl>()
        bind<SecureStorageFactory>().to<SecureStorageFactoryImpl>()

    }
}