package il.ac.technion.cs.softwaredesign.Fakes

import PersistentMap
import java.util.concurrent.CompletableFuture

class PersistentMapFake : PersistentMap {

    private var map = mutableMapOf<String, ByteArray>()

    override fun put(key: String, value: ByteArray): CompletableFuture<Unit> {
        map[key] = value
        return CompletableFuture.completedFuture(Unit)
    }
    override fun get(key: String): CompletableFuture<ByteArray?> {
        return CompletableFuture.completedFuture(map[key])
    }
    override fun exists(key: String): CompletableFuture<Boolean> {
        return CompletableFuture.completedFuture(map.contains(key))
    }

    override fun getAllMap(): CompletableFuture<Map<String, ByteArray?>> {
        return CompletableFuture.completedFuture(map)
    }
}