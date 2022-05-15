package library

import il.ac.technion.cs.softwaredesign.storage.SecureStorage

class SecureStorageFake : SecureStorage {

    private var map = mutableMapOf<ByteArray, ByteArray>()

    override fun write(key: ByteArray, value: ByteArray) {
        if(value.size > 100) {
            throw IllegalArgumentException()
        }
        map[key] = value;
    }

    override fun read(key: ByteArray): ByteArray? {
        return map[key];
    }
}