package library

class PersistentMapFactroy {

    companion object {
        fun <T> createPersistentMap(): PersistentMap<T> {
            return DefaultPersistentMap<T>();
        }
    }
}