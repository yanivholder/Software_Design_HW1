package il.ac.technion.cs.softwaredesign

import library.PersistentMap

class PersistentMapMock : PersistentMap {

    private var map : MutableMap<String, ByteArray?> = mutableMapOf()

    override fun put(key: String, value: ByteArray): Boolean {
        map[key] = value
        return true
    }
    override fun get(key: String): ByteArray?{
        return map[key]
    }
    override fun exists(key: String): Boolean{
        return map.contains(key)
    }

    override fun getAllMap(): Map<String, ByteArray?>{
        return map
    }
}
