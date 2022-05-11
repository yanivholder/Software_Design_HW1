package library

interface PersistentMapFactroy <T> {

    fun createPersistentMap(): PersistentMap<T>;

}