package il.ac.technion.cs.softwaredesign

interface BookManager {

    fun isIdExists(id: String): Boolean

    fun addBook(id: String, description: String, copiesAmount: Int): Unit

    /**
     * @note This function assumes that the book with this id does exist
     * and it's behaviour is undefined if called for non-existing book
     */
    fun getBookDescription(id: String): String

    fun getFirstBooksByAddTime(numOfBooks: Int): List<String>
}