package il.ac.technion.cs.softwaredesign
import java.util.concurrent.CompletableFuture

interface BookManager {


    fun isIdExists(id: String): CompletableFuture<Boolean>

    fun addBook(id: String, description: String, copiesAmount: Int): CompletableFuture<Unit>

    /**
     * @note This function assumes that the book with this id does exist
     * and it's behaviour is undefined if called for non-existing book
     */
    fun getBookDescription(id: String): CompletableFuture<String>

    /**
     * @note This function assumes that the book with this id does exist
     * and it's behaviour is undefined if called for non-existing book
     */
    fun getBookCopiesAmount(id: String): CompletableFuture<Int>

    fun getFirstBooksByAddTime(numOfBooks: Int): CompletableFuture<List<String>>
}