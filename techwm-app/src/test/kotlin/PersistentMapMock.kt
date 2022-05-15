package il.ac.technion.cs.softwaredesign

import library.PersistentMap

class PersistentMapMock<T> : PersistentMap<T> {

    private var map : MutableMap<String, T> = mutableMapOf<String, T>()

    override fun put(key: String, value: T): Boolean {
        map[key] = value
        return true
    }
    override fun get(key: String): T?{
        return map[key]
    }
    override fun exists(key: String): Boolean{
        return map.contains(key)
    }

    override fun getAllMap(): Map<String, T>{
        return map
    }
}
