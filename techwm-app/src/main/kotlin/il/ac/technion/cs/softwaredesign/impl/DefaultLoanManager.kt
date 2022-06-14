package il.ac.technion.cs.softwaredesign.impl
//package il.ac.technion.cs.softwaredesign
import Fakes.LoanServiceFake
import il.ac.technion.cs.softwaredesign.LoanManager
import il.ac.technion.cs.softwaredesign.LoanStatus
import il.ac.technion.cs.softwaredesign.LoanRequestInformation
import il.ac.technion.cs.softwaredesign.ObtainedLoan
import java.time.temporal.TemporalAmount
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.Comparator
import kotlin.collections.HashMap

typealias LoanId = String
typealias BookId = String

data class LoanInfo(var loanId: String = "N.A", var loanReq: LoanRequestInformation = LoanRequestInformation("", listOf(""), "", LoanStatus.QUEUED))
data class BookCacheItem (val libraryTotalAmount: Int, val takenTotal: Int)


class DefaultLoanManager : LoanManager{

    // TODO - solve this
    private val loanService: LoanServiceFake = LoanServiceFake()

    private var loanIdRunner: Int = 0
    private var queue: LinkedList<LoanInfo> = LinkedList(listOf())
    private var dequeuedLoans: HashMap<LoanId, LoanRequestInformation> = HashMap()
    private var bookLibraryAmountCache: HashMap<BookId, BookCacheItem> = HashMap()

    override fun addBook(id: String, copiesAmount: Int){
        bookLibraryAmountCache[id] = BookCacheItem(copiesAmount, 0)
    }

    override fun bookInfoMissing(bookId: String): Boolean{
        // we might be dealing with a post-crash system
        bookLibraryAmountCache[bookId] ?: return true
        return false
    }

    override fun loanBook(bookId: String, copiesAmount: Int){
        if (copiesAmount > 0){
            // we are dealing with a post-crash system
            bookLibraryAmountCache[bookId] = BookCacheItem(copiesAmount, 1 /* for this loan */)
            return
        }

        val cachedBook = bookLibraryAmountCache[bookId]
        if (cachedBook != null) {
            bookLibraryAmountCache[bookId] = BookCacheItem(cachedBook.libraryTotalAmount, cachedBook.takenTotal+1)
        }
    }

    override fun returnBook(bookId: String){
        // we know that a book is being returned then the system did not crash
        val cachedBook = bookLibraryAmountCache[bookId]
        if (cachedBook != null) {
            bookLibraryAmountCache[bookId] = BookCacheItem(cachedBook.libraryTotalAmount, cachedBook.takenTotal-1)
        }else{
            // should never get here
            assert(false)
        }
    }


    override fun createNewLoan(loanName: String, ownerId: String, bookIds: List<String>): String{
        val newLoanId = loanIdRunner.toString()
        val newLoan = LoanInfo(newLoanId, loanReq = LoanRequestInformation(loanName, bookIds, ownerId, LoanStatus.QUEUED))
        queue.addLast(newLoan)
        loanIdRunner += 1
        return newLoanId
    }

    override fun loanExists(loanId: String): Boolean{
        return ( (dequeuedLoans[loanId] != null) || (queue.firstOrNull { it.loanId == loanId } != null) )
    }

    override fun getLoanInfo(loanId: String): LoanRequestInformation ?{
        val loanInfo = dequeuedLoans[loanId]
        if (loanInfo != null){
            return loanInfo
        }
        // The following syntax means that if it's null return null, and if not get it's loanReq and return that
        return queue.firstOrNull() { it.loanId == loanId }?.loanReq
    }

    override fun cancelLoan(loanId: String): Unit {
        val elem = queue.first { it.loanId == loanId }
        queue.remove(elem)
        val loanReq = elem.loanReq
        dequeuedLoans[elem.loanId] = LoanRequestInformation(loanReq.loanName, loanReq.requestedBooks, loanReq.ownerUserId, LoanStatus.CANCELED)
    }

    fun bookReturnFun(loanId: String){
        val loan = dequeuedLoans[loanId]
        if (loan != null) {
            dequeuedLoans[loanId] =
                LoanRequestInformation(loan.loanName, loan.requestedBooks, loan.ownerUserId, LoanStatus.RETURNED)
        }else{
            // should never get here
            assert(false)
        }
        val obtainedBooks = dequeuedLoans[loanId]?.requestedBooks
        if (obtainedBooks != null) {
            for(bookId in obtainedBooks){
                loanService.returnBook(bookId)
                returnBook(bookId)
            }
        }else{
            // should never get here
            assert(false)
        }
    }

//    override fun waitForLoan(loanId: String): CompletableFuture<ObtainedLoan> {
//        return CompletableFuture<ObtainedLoan>().thenApply {
//            // TODO - try to get all the books, and try again each time a book is returned
//            DefaultObtainedLoan(loanId, this)
//        }
//    }

    override fun waitForLoan(loanId: String): CompletableFuture<ObtainedLoan> = TODO()
}