package il.ac.technion.cs.softwaredesign
import il.ac.technion.cs.softwaredesign.LoanStatus
import il.ac.technion.cs.softwaredesign.LoanRequestInformation
import il.ac.technion.cs.softwaredesign.ObtainedLoan
import il.ac.technion.cs.softwaredesign.impl.BookCacheItem
import java.util.*
import java.util.concurrent.CompletableFuture


interface LoanManager {

    fun addBook(id: String, copiesAmount: Int)

    fun bookInfoMissing(bookId: String): Boolean

    fun loanBook(bookId: String, copiesAmount: Int = -1)

    fun returnBook(bookId: String)

    fun createNewLoan(loanName: String, ownerId: String, bookIds: List<String>): String

    fun loanExists(loanId: String): Boolean

    fun getLoanInfo(loanId: String): LoanRequestInformation ?

    fun cancelLoan(loanId: String): Unit

    fun waitForLoan(loanId: String): CompletableFuture<ObtainedLoan>
}