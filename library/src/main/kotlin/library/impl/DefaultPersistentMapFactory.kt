package library.impl

import library.PersistentMap
import library.PersistentMapFactroy

class DefaultPersistentMapFactory<T>() : PersistentMapFactroy<T> {

    override fun createPersistentMap(): PersistentMap<T> {
        return DefaultPersistentMap<T>();
    }
}