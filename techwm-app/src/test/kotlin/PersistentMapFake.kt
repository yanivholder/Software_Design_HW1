package il.ac.technion.cs.softwaredesign

import library.PersistentMap

class PersistentMapFake<T> : PersistentMap<T> {

    private var map = mutableMapOf<String, T?>()

    override fun put(key: String, value: T?): Boolean {
        map[key] = value;
        return true;
    }
    override fun get(key: String): T?{
        return map[key];
    }
    override fun exists(key: String): Boolean{
        return map.contains(key);
    }

    override fun getAllMap(): Map<String, T?>{
        return map;
    }
}