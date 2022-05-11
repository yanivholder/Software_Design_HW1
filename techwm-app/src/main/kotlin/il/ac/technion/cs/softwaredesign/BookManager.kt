package il.ac.technion.cs.softwaredesign

import library.PersistentMap
import java.sql.Time

class BookManager{

    private val persistentMap: PersistentMap<BookInfo>

    init {
        persistentMap = PersistentMapFactory.create()
    }


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

class BookInfo constructor(desc: String, cAmout: Int){
    val description: String = desc
    val copiesAmount: Int = cAmout
    val timeOfLising = System.currentTimeMillis()
}