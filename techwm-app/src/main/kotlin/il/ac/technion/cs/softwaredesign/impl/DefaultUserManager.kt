package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.TokenStore
import il.ac.technion.cs.softwaredesign.User
import il.ac.technion.cs.softwaredesign.UserManager
import PersistentMap
import java.security.MessageDigest
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import javax.inject.Inject
import kotlin.concurrent.thread


class DefaultUserManager @Inject constructor(private val persistentMap: PersistentMap, private val tokenStore: TokenStore) : UserManager {


    private val md = MessageDigest.getInstance("SHA-1")

    override fun isUsernameExists(username: String): Boolean {
        return persistentMap.exists(username)
    }

    override fun isUsernameAndPassMatch(username: String, password: String): Boolean {
        val usr = persistentMap.get(username).get()
        if (usr == null){
            return false
        }else{
            /* The UserInfo(usr) wrapper is for deserializing */
            return (UserInfo(usr).password == password)
        }
    }

    override fun isValidToken(token: String): Boolean {
        return tokenStore.isValid(token)
    }

    private fun calculateToken(str: String, num: Int): String{
        val TokenSeed = str + (num).toString()
        return md.digest(TokenSeed.toByteArray()).toList().toString()
    }

    override fun generateUserTokenAndInvalidateOld(username: String): CompletableFuture<String> {
        val future: CompletableFuture<String> = CompletableFuture()

        thread(start = true) {
            val user: UserInfo = UserInfo(persistentMap.get(username).get()!!)
            val numberForTokenGeneation = user.getNumTokensGenerated()

            var tokenInvalidationFuture: CompletableFuture<Unit> = completedFuture(Unit)

            if (numberForTokenGeneation != 0){

                val oldToken = calculateToken(username, numberForTokenGeneation-1)
                tokenInvalidationFuture = tokenStore.invalidate(oldToken)
            }

            val newToken = calculateToken(username, numberForTokenGeneation)

            user.incNumTokensGenerated()

            val tokenInsertFuture = tokenStore.insert(newToken)
            val userUpdateFuture = persistentMap.put(username, user.serialize())

            CompletableFuture.allOf(tokenInvalidationFuture, tokenInsertFuture, userUpdateFuture).get()

            future.complete(newToken)
        }

        return future
    }

    override fun register(username: String, password: String, isFromCS: Boolean, age: Int): CompletableFuture<Unit> {
        val newUser = UserInfo(User(username, isFromCS, age), password)
        return persistentMap.put(username, newUser.serialize())
    }

    /**
     * @note This function assumes that the user does exist
     * and it's behaviour is undefined if called for non-existing user
     */
    override fun getUserInformation(username: String): CompletableFuture<User?> {
        assert(isUsernameExists(username))

        val future: CompletableFuture<User?> = CompletableFuture()

        thread(start = true) {
            val usrInfo = UserInfo(persistentMap.get(username).get()!!)
            future.complete(User(username, usrInfo.user.isFromCS, usrInfo.user.age))
        }

        return future

    }
}