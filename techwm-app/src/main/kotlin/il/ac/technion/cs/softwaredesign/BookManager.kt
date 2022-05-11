package il.ac.technion.cs.softwaredesign

import library.PersistentMap
import library.PersistentMapFactroy
import java.sql.Time

class BookManager(){
    private val persistentMap: PersistentMap<BookInfo> =  PersistentMapFactroy.createPersistentMap()


    fun isIdExists(id: String): Boolean {
        return persistentMap.exists(id)
    }

    fun addBook(id: String, description: String, copiesAmount: Int): Unit {
        val bookToStore: BookInfo = BookInfo(description, copiesAmount)
        persistentMap.put(id, bookToStore)
    }

    fun getBookDescription(id: String): String {
        return persistentMap.get(id).description;
    }

    fun getFirstBooksByAddTime(numOfBooks: Int): List<String> {

        val mapAsList = persistentMap.getAllMap().toList()
        val firstBooksByAddTime = mapAsList.sortedBy { it.second.timeOfLising }.take(numOfBooks);
        return firstBooksByAddTime.map { it.first /* The id's of the books only */ }
    }
}

