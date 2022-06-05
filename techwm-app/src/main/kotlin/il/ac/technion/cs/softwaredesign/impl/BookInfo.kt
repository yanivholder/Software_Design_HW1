package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.ByteSerializable
import il.ac.technion.cs.softwaredesign.User
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream


/**
 * A class holding a single user's information in the system.
 *
 * @property description - The book's description
 * @property copiesAmount - Number of copies held of this book
 * @property timeOfLising - Time when book was entered to system
 */
data class BookInfo(var description: String = "", var copiesAmount: Int = 0) : ByteSerializable {
    val timeOfLising = System.currentTimeMillis()

    constructor(byteArray: ByteArray): this() {
        // TODO - remove
//        val bais = ByteArrayInputStream(byteArray)
//        val ois = ObjectInputStream(bais)
//        val obj = ois.readObject() as BookInfo
        val obj = deserialize(byteArray) as BookInfo
        this.description = obj.description
        this.copiesAmount = obj.copiesAmount
    }
}
