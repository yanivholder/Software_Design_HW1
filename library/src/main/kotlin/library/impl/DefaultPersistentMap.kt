package library.impl

import library.PersistentMap

class DefaultPersistentMap<T>() : PersistentMap<T> {

    private val secureStorage = il.ac.technion.cs.softwaredesign.storage.impl.SecureStorageFactoryImpl().open(ByteArray(0));

    override fun put(key: String, value: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(key: String): T {
        TODO("Not yet implemented")
    }

    override fun exists(key: String): Boolean {
        return this.secureStorage.read(key.toByteArray()) != null;
    }

    override fun getAllMap(): Map<String, T> {
        TODO("Not yet implemented")
    }
}