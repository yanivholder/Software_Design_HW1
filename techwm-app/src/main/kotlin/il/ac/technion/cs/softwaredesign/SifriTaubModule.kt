package il.ac.technion.cs.softwaredesign

import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.impl.DefaultBookManager
import il.ac.technion.cs.softwaredesign.impl.DefaultTokenManager
import il.ac.technion.cs.softwaredesign.impl.DefaultUserManager
import library.*
import il.ac.technion.cs.softwaredesign.storage.SecureStorageFactory
import il.ac.technion.cs.softwaredesign.storage.SecureStorageModule
import library.PersistentMap
import library.impl.DefaultPersistentMap


class MapMock : PersistentMap {
    private var map = mutableMapOf<String, ByteArray>()

    override fun put(key: String, value: ByteArray): Boolean {
        map[key] = value
        return true;
    }
    override fun get(key: String): ByteArray? {
        return map[key]
    }
    override fun exists(key: String): Boolean {
        return map.contains(key)
    }

    override fun getAllMap(): Map<String, ByteArray> {
        return map
    }
}
class SifriTaubModule: KotlinModule() {

    override fun configure() {
//        bind<UserManager>().to<UserMock>()
//        bind<BookManager>().to<BookMock>()
        bind<UserManager>().to<DefaultUserManager>()
        bind<BookManager>().to<DefaultBookManager>()
        bind<TokenManager>().to<DefaultTokenManager>()
        install(SecureStorageModule)

//        bind<SecureStorageFactory>().to<SecureStorageFactoryImpl>()
//        bind<PersistentMap>().to<MapMock>()

//        bind<SecureStorage>().to<SecureStorage>()
    }
//    override fun configure() {
//        bind<UserManager>().to<DefaultUserManager>()
//        bind<BookManager>().to<DefaultBookManager>()
//    }
}