package il.ac.technion.cs.softwaredesign.impl


/**
 * A class holding a single user's information in the system.
 *
 * @property description - The book's description
 * @property copiesAmount - Number of copies held of this book
 * @property timeOfLising - Time when book was entered to system
 */
data class BookInfo(val description: String, val copiesAmount: Int){
    val timeOfLising = System.currentTimeMillis()
}

//class BookInfo constructor(desc: String, cAmout: Int){
//    val description: String = desc
//    val copiesAmount: Int = cAmout
//    val timeOfLising = System.currentTimeMillis()
//}
