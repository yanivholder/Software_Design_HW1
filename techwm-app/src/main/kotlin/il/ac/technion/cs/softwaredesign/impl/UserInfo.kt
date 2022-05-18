package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.ByteSerializable
import il.ac.technion.cs.softwaredesign.User
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

/**
 * A class holding a single user's information in the system.
 *
 * @property password The user's password.
 * @property isFromCS Whether the student is from CS faculty or external.
 * @property age The age of the student.
 * @property tokenGenerated The Number of token generated for this user. Also the effective number of times the use authenticted.

 */
data class UserInfo(var password: String = "", var isFromCS: Boolean = true, var age: Int = 0) : ByteSerializable {
    private var tokenGenerated: Int = 0

    constructor(byteArray: ByteArray): this() {
        val bais = ByteArrayInputStream(byteArray)
        val ois = ObjectInputStream(bais)
        val obj = ois.readObject() as UserInfo

        this.password = obj.password
        this.isFromCS = obj.isFromCS
        this.age = obj.age
    }

    fun getNumTokensGenerated(): Int{
        return tokenGenerated
    }

    fun incNumTokensGenerated(): Unit{
        tokenGenerated += 1
    }
}



