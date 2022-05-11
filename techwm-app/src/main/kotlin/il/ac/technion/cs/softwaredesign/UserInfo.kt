package il.ac.technion.cs.softwaredesign

/**
 * A class holding a single user's information in the system.
 *
 * @property password The user's password.
 * @property isFromCS Whether the student is from CS faculty or external.
 * @property age The age of the student.
 * @property tokenGenerated The Number of token generated for this user. Also the effective number of times the use authenticted.

 */
data class UserInfo(val password: String, val isFromCS: Boolean, val age: Int){
    private var tokenGenerated: Int = 0

    fun getNumTokensGenerated(): Int{
        return tokenGenerated
    }

    fun incNumTokensGenerated(): Unit{
        tokenGenerated += 1
    }
}



