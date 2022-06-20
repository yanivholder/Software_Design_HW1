package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.LoanStatus
import il.ac.technion.cs.softwaredesign.ObtainedLoan
import java.util.concurrent.CompletableFuture

class DefaultObtainedLoan (private val loanId: String, private val savedLoanManager: DefaultLoanManager?) : ObtainedLoan {

    /**
     * Marks the loan request as [LoanStatus.RETURNED], returning all book to the library, marking them available for other loan
     * requests. This should also call LoanService.returnBook(id) for each book that was loaned.
     */
    override fun returnBooks(): CompletableFuture<Unit>{
        return CompletableFuture.supplyAsync {
            if (savedLoanManager != null){
                savedLoanManager.bookReturnFunc(loanId)
                savedLoanManager.freeQueueWaiters()
            }
        }
    }
}

