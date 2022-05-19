package il.ac.technion.cs.softwaredesign

import library.PersistentMap

class PersistentMapFake : PersistentMap {

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