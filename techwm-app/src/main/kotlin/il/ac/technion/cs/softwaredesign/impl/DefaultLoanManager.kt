package il.ac.technion.cs.softwaredesign.impl
//package il.ac.technion.cs.softwaredesign
import Fakes.LoanServiceFake
import il.ac.technion.cs.softwaredesign.*
import il.ac.technion.cs.softwaredesign.loan.LoanService
import java.time.temporal.TemporalAmount
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import kotlin.Comparator
import kotlin.collections.HashMap

typealias LoanId = String
typealias BookId = String

data class LoanInfo(var loanId: String = "N.A", var loanReq: LoanRequestInformation = LoanRequestInformation("", listOf(""), "", LoanStatus.QUEUED), val future: CompletableFuture<ObtainedLoan>, var waitedOn: Boolean = false)
data class BookCacheItem (val libraryTotalAmount: Int, val takenTotal: Int)


class DefaultLoanManager @Inject constructor(private val loanService: LoanService) : LoanManager{


    private var loanIdRunner: Int = 0
    private var queue: LinkedList<LoanInfo> = LinkedList(listOf())
    private var dequeuedLoans: HashMap<LoanId, LoanRequestInformation> = HashMap()
    private var bookLibraryAmountCache: HashMap<BookId, BookCacheItem> = HashMap()

    override fun addBook(id: String, copiesAmount: Int){
        bookLibraryAmountCache[id] = BookCacheItem(copiesAmount, 0)
    }

    /** @note use only on a book that is known to be in the system! */
    override fun getBookAvailableAmount(bookId: String): Int {
        val bookCacheItem = bookLibraryAmountCache[bookId]!!
        return bookCacheItem.libraryTotalAmount - bookCacheItem.takenTotal
    }

    override fun bookInfoMissing(bookId: String): Boolean{
        // we might be dealing with a post-crash system
        bookLibraryAmountCache[bookId] ?: return true
        return false
    }

    override fun loanBook(bookId: String, copiesAmount: Int) {
        if (copiesAmount > 0){
            // we are dealing with a post-crash system
            bookLibraryAmountCache[bookId] = BookCacheItem(copiesAmount, 1 /* for this loan */)
            return
        }

        val cachedBook = bookLibraryAmountCache[bookId]
        if (cachedBook != null) {
            bookLibraryAmountCache[bookId] = BookCacheItem(cachedBook.libraryTotalAmount, cachedBook.takenTotal+1)
            loanService.loanBook(bookId)
        }
    }

    /** Used ONLY for queue head */
    private fun canCompleteLoan(loanId: String): Boolean{
        assert(queue.first.loanId == loanId)
        // get book list
        val bookList = queue.first.loanReq.requestedBooks
        // check that all have positive amount - hence loan can be obtained
        bookList.forEach {
            if (getBookAvailableAmount(it) == 0){
                return false
            }
        }
        return true
    }

    /** Used ONLY for queue head */
    private fun completeLoan(loanId: String): Unit {
        assert(queue.first.loanId == loanId)
        // get book list
        val bookList = queue.first.loanReq.requestedBooks
        // call `loanBook` for each
        bookList.forEach { loanBook(it) }

        // complete it's future
//        println("load "+loanId+"future completed")
        queue.first.future.complete(DefaultObtainedLoan(loanId, this))
    }

    override fun returnBook(bookId: String) {
        // we know that a book is being returned then the system did not crash
        val cachedBook = bookLibraryAmountCache[bookId]
        if (cachedBook != null) {
            bookLibraryAmountCache[bookId] = BookCacheItem(cachedBook.libraryTotalAmount, cachedBook.takenTotal-1)
            loanService.returnBook(bookId)
        }else{
            // should never get here
            assert(false)
        }
    }

    override fun loanExists(loanId: String): Boolean{
        return ( (dequeuedLoans[loanId] != null) || (queue.firstOrNull { it.loanId == loanId } != null) )
    }


    override fun createNewLoan(loanName: String, ownerId: String, bookIds: List<String>): String{
        val newLoanId = loanIdRunner.toString()
        val newLoan = LoanInfo(newLoanId, loanReq = LoanRequestInformation(loanName, bookIds, ownerId, LoanStatus.QUEUED), CompletableFuture<ObtainedLoan>())
        queue.addLast(newLoan)
        loanIdRunner += 1
        return newLoanId
    }

    override fun getLoanInfo(loanId: String): LoanRequestInformation ?{
        val loanInfo = dequeuedLoans.getOrDefault(loanId, null)
        if (loanInfo != null){
            return loanInfo
        }
        // The following syntax means that if it's null return null, and if not get it's loanReq and return that
        return queue.firstOrNull() { it.loanId == loanId }?.loanReq
    }

    override fun cancelLoan(loanId: String): Unit {
        val elem = queue.firstOrNull() { it.loanId == loanId }
        if (elem != null){
            queue.remove(elem)
            val loanReq = elem.loanReq
            dequeuedLoans[elem.loanId] = LoanRequestInformation(loanReq.loanName, loanReq.requestedBooks, loanReq.ownerUserId, LoanStatus.CANCELED)
        }
    }

    fun bookReturnFunc(loanId: String){
        val loan = dequeuedLoans[loanId]
        if (loan != null) {
            dequeuedLoans[loanId] =
                LoanRequestInformation(loan.loanName, loan.requestedBooks, loan.ownerUserId, LoanStatus.RETURNED)

            for(bookId in loan.requestedBooks){
                returnBook(bookId)
            }
        }else{
            // should never get here
            assert(false)
        }

    }

    fun freeQueueWaiters() {
        while (!queue.isEmpty() && canCompleteLoan(queue.first.loanId)){
            // remove from Queue
            completeLoan(queue.first.loanId)

            val elem = queue.first()
            queue.removeFirst()
//            queue.remove(elem)
            val loanReq = elem.loanReq
            // keep loanReq state as QUEUED so we know if has been waited on already or not, only waiter can change to OBTAINED
            dequeuedLoans[elem.loanId] = LoanRequestInformation(loanReq.loanName, loanReq.requestedBooks, loanReq.ownerUserId, LoanStatus.QUEUED)
        }
    }

    // in queue or in dequeued under status = QUEUED (which means it was given the books but was not waited for yet)
    private fun isLoanStatusQueued(loanId: String): Boolean {
        return ((queue.firstOrNull{it.loanId == loanId} != null) || (dequeuedLoans[loanId] != null && dequeuedLoans[loanId]!!.loanStatus == LoanStatus.QUEUED))
    }

    override fun waitForLoan(loanId: String): CompletableFuture<ObtainedLoan> {
        // if the loan status is LoanStatus.CANCELED or Obtained or Returned return a DoNothingObtainedLoan
        if (!isLoanStatusQueued(loanId)){
            return CompletableFuture.completedFuture(DefaultObtainedLoan(loanId, null))
        }
        if (queue.firstOrNull { it.loanId == loanId } != null){
            val elem = queue.first { it.loanId == loanId }
            freeQueueWaiters()
            if (dequeuedLoans[loanId] == null) {
                return elem.future
            }
        }

        // maybe it was freed from queue by a book returner but was not "waited on" yet
        assert(dequeuedLoans[loanId] != null)
        // we know loan is queued, assert this fact
        assert(dequeuedLoans[loanId]!!.loanStatus == LoanStatus.QUEUED)
        // if it's in dequeued, that means it was freed from queue and also waited (now) so we can change status
        val loanReq = dequeuedLoans[loanId]!!
        dequeuedLoans[loanId] = LoanRequestInformation(loanReq.loanName, loanReq.requestedBooks, loanReq.ownerUserId, LoanStatus.OBTAINED)
//        println("loan "+loanId+" obtained")
        freeQueueWaiters()
        return CompletableFuture.completedFuture(DefaultObtainedLoan(loanId, this))


//        // else (if it's Queued) return it's future (that goes with it)
//        assert(queue.firstOrNull { it.loanId == loanId } != null)
//        val elem = queue.first { it.loanId == loanId }
//        freeQueueWaiters()
//        return elem.future
    }
}