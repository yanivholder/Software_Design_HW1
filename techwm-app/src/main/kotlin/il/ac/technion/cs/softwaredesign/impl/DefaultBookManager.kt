package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.BookManager
import PersistentMap
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.concurrent.thread

class DefaultBookManager @Inject constructor(private val persistentMap: PersistentMap) : BookManager {

    override fun isIdExists(id: String): Boolean {
        return persistentMap.exists(id).get()
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
        assert(isIdExists(id))

        val bookDesc: CompletableFuture<String> = persistentMap.get(id)
            .thenApply { serilizedBookInfo ->
                BookInfo(serilizedBookInfo!!).description
        }
        return bookDesc
    }

    override fun getFirstBooksByAddTime(numOfBooks: Int): CompletableFuture<List<String>> {
        val future: CompletableFuture<List<String>> = CompletableFuture()

        thread(start = true) {
            val mapAsList = persistentMap.getAllMap().get().map { Pair(it.key, BookInfo(it.value!!)) }.toList()
            val firstBooksByAddTime = mapAsList.sortedBy { it.second.timeOfLising }.take(numOfBooks)
            val res = firstBooksByAddTime.map { it.first /* The id's of the books only */ }.toList()

            future.complete(res)
        }

        return future
    }
}

