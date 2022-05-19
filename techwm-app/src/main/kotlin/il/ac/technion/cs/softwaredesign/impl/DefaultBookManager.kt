package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.BookManager
import library.PersistentMap
import javax.inject.Inject

class DefaultBookManager @Inject constructor(private val persistentMap: PersistentMap) : BookManager {

    override fun isIdExists(id: String): Boolean {
        return persistentMap.exists(id)
    }

    override fun addBook(id: String, description: String, copiesAmount: Int) {
        val bookToStore = BookInfo(description, copiesAmount)
        persistentMap.put(id, bookToStore.serialize())
    }

    /**
     * @note This function assumes that the book with this id does exist
     * and it's behaviour is undefined if called for non-existing book
     */
    override fun getBookDescription(id: String): String {
        assert(isIdExists(id))
        val book = BookInfo(persistentMap.get(id)!!)
        return book.description
    }

    override fun getFirstBooksByAddTime(numOfBooks: Int): List<String> {

        val mapAsList = persistentMap.getAllMap().map { Pair(it.key, BookInfo(it.value!!)) }.toList()
        val firstBooksByAddTime = mapAsList.sortedBy { it.second.timeOfLising }.take(numOfBooks)
        return firstBooksByAddTime.map { it.first /* The id's of the books only */ }.toList()
    }
}

