import java.util.concurrent.CompletableFuture

interface PersistentMap {

    /**
     *
     * @param ?
     *
     * @throws ?
     */
    fun put(key: String, value: ByteArray): CompletableFuture<Boolean>

    /**
     *
     * @param ?
     *
     * @throws ?
     */
    fun get(key: String): CompletableFuture<ByteArray?>

    /**
     *
     * @param ?
     *
     * @throws ?
     */
    fun exists(key: String): CompletableFuture<Boolean>

    /**
     *
     * @param ?
     *
     * @throws ?
     */
    fun getAllMap(): CompletableFuture<Map<String, ByteArray?>>
}