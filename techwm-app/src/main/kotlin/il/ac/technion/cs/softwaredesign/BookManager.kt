package il.ac.technion.cs.softwaredesign

class BookManager {

    val persistentMap: PersistentMap

    init {
        persistentMap = PersistentMapFactory.create()
    }



    companion object {
        fun isIdExists(id: String): Boolean {
            return false;
        }

        fun addBook(id: String, description: String, copiesAmount: Int): Unit {

        }

        fun getBookDescription(id: String): String {
            return "";
        }

        fun getFirstBooksByAddTime(numOfBooks: Int): List<String> {
            // remember to sort by time and limit by numOf...
            return listOf();
        }
    }
}

class BookInfo co{
    val description: String
    val 
}