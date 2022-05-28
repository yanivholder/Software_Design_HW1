package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.TokenStore
import il.ac.technion.cs.softwaredesign.User
import il.ac.technion.cs.softwaredesign.UserManager
import PersistentMap
import java.security.MessageDigest
import javax.inject.Inject


class DefaultUserManager @Inject constructor(private val persistentMap: PersistentMap, private val tokenStore: TokenStore) : UserManager {


    private val md = MessageDigest.getInstance("SHA-1")

    override fun isUsernameExists(username: String): Boolean {
        return persistentMap.exists(username)
    }

    override fun isUsernameAndPassMatch(username: String, password: String): Boolean {
        val usr = persistentMap.get(username)
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

    override fun generateUserTokenAndInvalidateOld(username: String): String {
        val user: UserInfo = UserInfo(persistentMap.get(username)!!)
        val numberForTokenGeneation = user.getNumTokensGenerated()

        if (numberForTokenGeneation != 0){

            val oldToken = calculateToken(username, numberForTokenGeneation-1)
            if(tokenStore.invalidate(oldToken) == false){
                // debugging purposes only, getting here means something went wrong
                assert(false)
            }
        }

        val newToken = calculateToken(username, numberForTokenGeneation)
        if (tokenStore.insert(newToken) == false){
            // debugging purposes only, getting here means something went wrong
            assert(false)
        }

        user.incNumTokensGenerated()
        persistentMap.put(username, user.serialize())

        return newToken
    }

    override fun register(username: String, password: String, isFromCS: Boolean, age: Int): Unit {
        val newUser = UserInfo(User(username, isFromCS, age), password)
        persistentMap.put(username, newUser.serialize())
    }

    /**
     * @note This function assumes that the user does exist
     * and it's behaviour is undefined if called for non-existing user
     */
    override fun getUserInformation(username: String): User {
        assert(isUsernameExists(username))
        val usrInfo = UserInfo(persistentMap.get(username)!!)
        return User(username, usrInfo.user.isFromCS, usrInfo.user.age)
    }
}