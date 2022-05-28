package il.ac.technion.cs.softwaredesign

import Fakes.SecureStorageFake
import LibraryProdModule
import PersistentMap
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.impl.DefaultBookManager
import il.ac.technion.cs.softwaredesign.impl.DefaultTokenManager
import il.ac.technion.cs.softwaredesign.impl.DefaultUserManager
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import impl.DefaultPersistentMap

class IntegrationalTestModule: KotlinModule() {

    override fun configure() {
        // TODO maybe use this in the future instead of binding SecureStorageFake
//        install(LibraryTestModule())
        bind<UserManager>().to<DefaultUserManager>()
        bind<BookManager>().to<DefaultBookManager>()
        bind<TokenManager>().to<DefaultTokenManager>()
        bind<PersistentMap>().to<DefaultPersistentMap>()

        bind<SecureStorage>().to<SecureStorageFake>()
    }
}