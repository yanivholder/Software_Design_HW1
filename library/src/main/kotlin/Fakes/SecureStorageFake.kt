package Fakes

import il.ac.technion.cs.softwaredesign.storage.SecureStorage

class ByteArrayKey(private val key: ByteArray) {
    override fun equals(other: Any?): Boolean =
        this === other || other is ByteArrayKey && this.key contentEquals other.key
    override fun hashCode(): Int = this.key.contentHashCode()
    override fun toString(): String = this.key.contentToString()
}

class SecureStorageFake : SecureStorage {

    private var map = mutableMapOf<ByteArrayKey, ByteArray>()

    override fun write(key: ByteArray, value: ByteArray) {
        if(value.size > 100) {
            throw IllegalArgumentException()
        }
        map[ByteArrayKey(key)] = value
    }

    override fun read(key: ByteArray): ByteArray? {
        return map[ByteArrayKey(key)]
    }
}