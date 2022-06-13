package il.ac.technion.cs.softwaredesign.impl

import Fakes.LoanServiceFake
import il.ac.technion.cs.softwaredesign.LoanStatus
import il.ac.technion.cs.softwaredesign.ObtainedLoan
import il.ac.technion.cs.softwaredesign.SifriTaub
import il.ac.technion.cs.softwaredesign.loan.LoanService
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

//class DefaultObtainedLoan(private val loanId: String, private val bookReturnFunction: (loanId: String) -> Unit ) : ObtainedLoan {
class DefaultObtainedLoan @Inject constructor(private val loanId: String, private val defaultLoanManager: DefaultLoanManager) : ObtainedLoan {

    /**
     * Marks the loan request as [LoanStatus.RETURNED], returning all book to the library, marking them available for other loan
     * requests. This should also call LoanService.returnBook(id) for each book that was loaned.
     */
    override fun returnBooks(): CompletableFuture<Unit>{
        return CompletableFuture.supplyAsync {
            defaultLoanManager.bookReturnFun(loanId)
//            bookReturnFunction(loanId)
        }
    }
}
