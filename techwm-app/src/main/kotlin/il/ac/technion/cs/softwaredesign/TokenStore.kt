package il.ac.technion.cs.softwaredesign
import java.util.concurrent.CompletableFuture


interface TokenStore {

    // return true if and only if invalidation occurred successfully
    fun invalidate(oldToken: String, ownerId: String): CompletableFuture<Unit>

    // return true if and only if insert occurred successfully
    fun insert(newToken: String, ownerId: String): CompletableFuture<Unit>

    fun isValid(token: String): CompletableFuture<Boolean>

    /** call only for valid tokens, behavior is undefined otherwise */
    fun getTokenOwner(token: String): CompletableFuture<String>
}