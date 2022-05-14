package il.ac.technion.cs.softwaredesign

import library.PersistentMap
import library.PersistentMapFactroy
import java.sql.Time

class BookManager(pmf: PersistentMapFactroy<BookInfo>){
    // Inject a storage dependency that implements [put, get, exists, getAllMap] to use below

    private val persistentMap: PersistentMap<BookInfo> = pmf.createPersistentMap()

    fun isIdExists(id: String): Boolean {
        return persistentMap.exists(id)
    }

    fun addBook(id: String, description: String, copiesAmount: Int): Unit {
        val bookToStore: BookInfo = BookInfo(description, copiesAmount)
        persistentMap.put(id, bookToStore)
    }

    /**
     * @note This function assumes that the book with this id does exist
     * and it's behaviour is undefined if called for non-existing book
     */
    fun getBookDescription(id: String): String {
        assert(isIdExists(id))
        return persistentMap.get(id).description;
    }

    fun getFirstBooksByAddTime(numOfBooks: Int): List<String> {

        val mapAsList = persistentMap.getAllMap().toList()
        val firstBooksByAddTime = mapAsList.sortedBy { it.second.timeOfLising }.take(numOfBooks);
        return firstBooksByAddTime.map { it.first /* The id's of the books only */ }
    }
}

