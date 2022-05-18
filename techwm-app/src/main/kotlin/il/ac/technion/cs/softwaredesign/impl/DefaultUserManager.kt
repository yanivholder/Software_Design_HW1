package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.TokenManager
import il.ac.technion.cs.softwaredesign.User
import il.ac.technion.cs.softwaredesign.UserManager
import library.PersistentMap
import java.security.MessageDigest
import javax.inject.Inject


class DefaultUserManager @Inject constructor(private val persistentMap: PersistentMap, private val tokenManager: TokenManager) : UserManager {

    // Inject a storage dependency that implements [put, get, exists] to use below

//    private val persistentMap: PersistentMap<UserInfo> = persistentMap
//    private val tokenManager: TokenManager = tokenManager
    private val md = MessageDigest.getInstance("SHA-1")


    override fun isUsernameExists(username: String): Boolean {
        return persistentMap.exists(username)
    }
    override fun isUsernameAndPassMatch(username: String, password: String): Boolean {
        return UserInfo(persistentMap.get(username)!!).password == password;
    }

    override fun isValidToken(token: String): Boolean {
        return tokenManager.isValid(token)
    }

    private fun calculateToken(str: String, num: Int): String{
        val TokenSeed = str + (num).toString()
        return md.digest(TokenSeed.toByteArray()).toList().toString()
    }

    override fun generateUserTokenAndInvalidateOld(username: String): String {
        val user: UserInfo = UserInfo(persistentMap.get(username)!!)
        val numberForTokenGeneation = user.getNumTokensGenerated()

        if (numberForTokenGeneation != 0){

            val oldToken = calculateToken(username, numberForTokenGeneation-1)
            if(tokenManager.invalidate(oldToken) == false){
                // debugging purposes only, getting here means something went wrong
                assert(false)
            }
        }

        val newToken = calculateToken(username, numberForTokenGeneation)
        if (tokenManager.insert(newToken) == false){
            // debugging purposes only, getting here means something went wrong
            assert(false)
        }

        user.incNumTokensGenerated()
        persistentMap.put(username, user.serialize())

        return newToken
    }

    override fun register(username: String, password: String, isFromCS: Boolean, age: Int): Unit {
        val newUser = UserInfo(password, isFromCS, age)
        persistentMap.put(username, newUser.serialize())
    }

    /**
     * @note This function assumes that the user does exist
     * and it's behaviour is undefined if called for non-existing user
     */
    override fun getUserInformation(username: String): User {
        assert(isUsernameExists(username))
        val usr = UserInfo(persistentMap.get(username)!!)
        return User(username, usr.isFromCS, usr.age)
    }
}