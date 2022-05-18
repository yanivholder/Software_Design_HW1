package il.ac.technion.cs.softwaredesign

import java.io.ByteArrayInputStream
import java.io.ObjectInputStream


/**
 * A class holding a single user's information in the system.
 *
 * @property username A unique username identifying the user throughout the system
 * @property isFromCS Whether the student is from CS faculty or external.
 * @property age The age of the student.
 */
data class User(var username: String = "", var isFromCS: Boolean = true, var age: Int = 0) : ByteSerializable {

}