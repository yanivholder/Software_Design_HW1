package il.ac.technion.cs.softwaredesign

import library.PersistentMap
import library.PersistentMapFactroy
import java.security.MessageDigest


class UserManager() {

    private val persistentMap: PersistentMap<UserInfo> = PersistentMapFactroy<UserInfo>::createPersistentMap()
    private val tokenManager: TokenManager = TokenManager() // TODO - maybe switch to getInstance()
    private val md = MessageDigest.getInstance("SHA-1")


    fun isUsernameExists(username: String): Boolean {
        return persistentMap.exists(username)
    }
    fun isUsernameAndPassMatch(username: String, password: String): Boolean {
        return persistentMap.get(username).password == password;
    }

    fun isValidToken(token: String): Boolean {
        return tokenManager.isValid(token)
    }

    fun generateUserToken(username: String): String {
        val user = persistentMap.get(username)
        val numberForTokenGeneation = user.getNumTokensGenerated()
        if (numberForTokenGeneation != 0){
            // TODO
            val oldTokenSeed = username + (numberForTokenGeneation-1).toString()
            val oldToken = md.digest(oldTokenSeed.toByteArray()).toString()
            if(tokenManager.invalidate(oldToken) == false){
                // debugging purposes only, getting here means something went wrong
                assert(false)
            }
        }

        val newTokenSeed = username + numberForTokenGeneation.toString()
        val newToken = md.digest(newTokenSeed.toByteArray()).toString()
        if (tokenManager.insert(newToken) == false){
            // debugging purposes only, getting here means something went wrong
            assert(false)
        }

        user.incNumTokensGenerated()
        return newToken
    }

    fun register(username: String, password: String, isFromCS: Boolean, age: Int): Unit {
        val newUser = UserInfo(password, isFromCS, age)
        persistentMap.put(username, newUser)
    }

    /**
     * @note This function assumes that the user does exist
     * and it's behaviour is undefined if called for non-existing user
     */
    fun getUserInformation(username: String): User {
        assert(isUsernameExists(username))
        val usr = persistentMap.get(username)
        return User(username, usr.isFromCS, usr.age)
    }
}