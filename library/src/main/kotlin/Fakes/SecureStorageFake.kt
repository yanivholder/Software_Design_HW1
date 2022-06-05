package Fakes

import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import java.util.concurrent.CompletableFuture

class ByteArrayKey(private val key: ByteArray) {
    override fun equals(other: Any?): Boolean =
        this === other || other is ByteArrayKey && this.key contentEquals other.key
    override fun hashCode(): Int = this.key.contentHashCode()
    override fun toString(): String = this.key.contentToString()
}

class SecureStorageFake : SecureStorage {

    private var map = mutableMapOf<ByteArrayKey, ByteArray>()

    override fun write(key: ByteArray, value: ByteArray): CompletableFuture<Unit> {
        if(value.size > 100) {
            throw IllegalArgumentException()
        }
        map[ByteArrayKey(key)] = value
        return CompletableFuture.completedFuture(Unit)
    }

    override fun read(key: ByteArray): CompletableFuture<ByteArray?> {
        return CompletableFuture.completedFuture(map[ByteArrayKey(key)])
    }
}