package library

interface Library <T> {

    /**
     *
     * @param ?
     *
     * @throws ?
     */
    fun put(key: String, value: T): Boolean;

    /**
     *
     * @param ?
     *
     * @throws ?
     */
    fun get(key: String): T;

    /**
     *
     * @param ?
     *
     * @throws ?
     */
    fun exists(key: String): Boolean;
}