package il.ac.technion.cs.softwaredesign

import il.ac.technion.cs.softwaredesign.impl.UserInfo
import java.util.concurrent.CompletableFuture

interface UserManager {


    fun isUsernameExists(username: String): Boolean

    fun isUsernameAndPassMatch(username: String, password: String): Boolean

    fun isValidToken(token: String): Boolean

    fun generateUserTokenAndInvalidateOld(username: String): CompletableFuture<String>

    fun register(username: String, password: String, isFromCS: Boolean, age: Int): CompletableFuture<Unit>

    /**
     * @note This function assumes that the user does exist
     * and it's behaviour is undefined if called for non-existing user
     */
    fun getUserInformation(username: String): CompletableFuture<User?>
}