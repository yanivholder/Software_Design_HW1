package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.ByteSerializable
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

/**
 * A class holding a single Token's information in the system (just a boolean wrapper).
 *
 * @property tokenValidity Whether this token is valid or not

 */
data class TokenInfo(private var tokenValidity: Boolean = false) : ByteSerializable {

    constructor(byteArray: ByteArray): this() {
        // TODO - remove
//        val bais = ByteArrayInputStream(byteArray)
//        val ois = ObjectInputStream(bais)
//        val obj = ois.readObject() as TokenInfo
        val obj = deserialize(byteArray) as TokenInfo
        this.tokenValidity = obj.getValidity()
    }

    fun getValidity(): Boolean{
        return tokenValidity
    }

}