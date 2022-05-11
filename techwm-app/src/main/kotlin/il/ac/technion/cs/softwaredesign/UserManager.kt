package il.ac.technion.cs.softwaredesign

import library.PersistentMap
import library.PersistentMapFactroy
import java.security.MessageDigest

class UserManager() {

    private val persistentMap: PersistentMap<UserInfo> = PersistentMapFactroy.createPersistentMap()
    private val md = MessageDigest.getInstance("SHA-1")

    fun isUsernameExists(username: String): Boolean {
        return persistentMap.exists(username)
    }
    fun isUsernameAndPassMatch(username: String, password: String): Boolean {
        return persistentMap.get(username).password == password;
    }

    fun generateUserToken(username: String): String {
        val user = persistentMap.get(username)
        val numberForTokenGeneation = user.getNumTokensGenerated()
        if (numberForTokenGeneation != 0){
            // TODO
            invalidateOldToken()
        }
        user.incNumTokensGenerated()

        val newTokenSeed = username + numberForTokenGeneation.toString()
        val newToken = md.digest(newTokenSeed.toByteArray()).toString()
    }

    fun isValidToken(token: String): Boolean {
        return false;
    }

    fun register(username: String, password: String, isFromCS: Boolean, age: Int): Unit {

    }

    fun getUserInformation(username: String): User {
        return User(age = 0, isFromCS = false, username = username);
    }

}