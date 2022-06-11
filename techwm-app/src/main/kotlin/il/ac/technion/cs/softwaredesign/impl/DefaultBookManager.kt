package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.BookManager
import PersistentMap
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.concurrent.thread

class DefaultBookManager @Inject constructor(private val persistentMap: PersistentMap) : BookManager {

    override fun isIdExists(id: String): CompletableFuture<Boolean> {
        return persistentMap.exists(id)
    }

    override fun addBook(id: String, description: String, copiesAmount: Int): CompletableFuture<Unit> {
        val bookToStore = BookInfo(description, copiesAmount)
        return persistentMap.put(id, bookToStore.serialize())
    }

    /**
     * @note This function assumes that the book with this id does exist
     * and it's behaviour is undefined if called for non-existing book
     */
    override fun getBookDescription(id: String): CompletableFuture<String> {

        return persistentMap.get(id).thenApply { serializedBookInfo ->
                BookInfo(serializedBookInfo!!).description
        }
    }

    override fun getFirstBooksByAddTime(numOfBooks: Int): CompletableFuture<List<String>> {

        return persistentMap.getAllMap().thenApply { booksMap ->
            val mapAsList = booksMap.map { Pair(it.key, BookInfo(it.value!!)) }.toList()
            val firstBooksByAddTime = mapAsList.sortedBy { it.second.timeOfLising }.take(numOfBooks)

            firstBooksByAddTime.map { it.first /* The id's of the books only */ }.toList()
        }
    }
}

