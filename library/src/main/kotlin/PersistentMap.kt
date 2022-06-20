import java.util.concurrent.CompletableFuture

interface PersistentMap {

    /**
     *
     * @param key - the key to use for put
     * @param value - the value to save under this key
     *
     */
    fun put(key: String, value: ByteArray): CompletableFuture<Unit>

    /**
     *
     * @param key - get the value corresponding to this key
     *
     */
    fun get(key: String): CompletableFuture<ByteArray?>

    /**
     *
     * @param key - check if there's a key like this in the map
     *
     */
    fun exists(key: String): CompletableFuture<Boolean>

    /**
     * get the entire map, keys and values
     */
    fun getAllMap(): CompletableFuture<Map<String, ByteArray?>>
}