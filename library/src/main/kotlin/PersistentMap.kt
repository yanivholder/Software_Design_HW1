interface PersistentMap {

    /**
     *
     * @param ?
     *
     * @throws ?
     */
    fun put(key: String, value: ByteArray): Boolean

    /**
     *
     * @param ?
     *
     * @throws ?
     */
    fun get(key: String): ByteArray?

    /**
     *
     * @param ?
     *
     * @throws ?
     */
    fun exists(key: String): Boolean

    /**
     *
     * @param ?
     *
     * @throws ?
     */
    fun getAllMap(): Map<String, ByteArray?>
}