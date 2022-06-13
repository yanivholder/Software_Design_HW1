package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.TokenStore
import il.ac.technion.cs.softwaredesign.User
import il.ac.technion.cs.softwaredesign.UserManager
import PersistentMap
import java.security.MessageDigest
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import javax.inject.Inject


class DefaultUserManager @Inject constructor(private val persistentMap: PersistentMap, private val tokenStore: TokenStore) : UserManager {


    private val md = MessageDigest.getInstance("SHA-1")

    override fun isUsernameExists(username: String): CompletableFuture<Boolean> {
        return persistentMap.exists(username)
    }

    override fun isUsernameAndPassMatch(username: String, password: String): CompletableFuture<Boolean> {
        return persistentMap.get(username).thenCompose { rawUser ->
            if (rawUser == null) {
                completedFuture(false)
            }
            else {
                /* The UserInfo(usr) wrapper is for deserializing */
                completedFuture(UserInfo(rawUser).password == password)
            }
        }
    }

    override fun isValidToken(token: String): CompletableFuture<Boolean> {
        return tokenStore.isValid(token)
    }

    /**
     * We hash twice to avoid collisions between keys and numbers like "my_str"+"12" and "my_str1"+"2"
     */
    private fun calculateToken(str: String, num: Int): String{
        val hasedKey = md.digest(str.toByteArray()).toList().toString()
        val TokenSeed = hasedKey + (num).toString()
        return md.digest(TokenSeed.toByteArray()).toList().toString()
    }

    /**
     * @note This function assumes that the user does exist
     * and it's behaviour is undefined if called for non-existing user
     */
    override fun generateUserTokenAndInvalidateOld(username: String): CompletableFuture<String> {

        return persistentMap.get(username).thenCompose { serializedUser ->
            val user = UserInfo(serializedUser!!)
            val numberForTokenGeneation = user.getNumTokensGenerated()
            user.incNumTokensGenerated()

            val newToken = calculateToken(username, numberForTokenGeneation)

            val tokenInsertFuture = tokenStore.insert(newToken, username)
            val userUpdateFuture = persistentMap.put(username, user.serialize())

            var tokenInvalidationFuture: CompletableFuture<Unit> = completedFuture(Unit)
            if (numberForTokenGeneation != 0){
                val oldToken = calculateToken(username, numberForTokenGeneation-1)
                tokenInvalidationFuture = tokenStore.invalidate(oldToken, username)
            }

            CompletableFuture.allOf(tokenInvalidationFuture, tokenInsertFuture, userUpdateFuture).thenCompose{
                completedFuture(newToken)
            }
        }
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

        return persistentMap.get(username).thenCompose { serializedUser ->
            val usrInfo = UserInfo(serializedUser!!)
            completedFuture(User(username, usrInfo.user.isFromCS, usrInfo.user.age))
        }
    }

    override fun getUserNameByToken(token: String): CompletableFuture<String> {
        return tokenStore.getTokenOwner(token)
    }
}