package il.ac.technion.cs.softwaredesign
import il.ac.technion.cs.softwaredesign.LoanStatus
import il.ac.technion.cs.softwaredesign.LoanRequestInformation
import il.ac.technion.cs.softwaredesign.ObtainedLoan
import java.util.*
import java.util.concurrent.CompletableFuture


interface LoanManager {

    fun createNewLoan(loanName: String, ownerId: String, bookIds: List<String>): String

    fun loanExists(loanId: String): Boolean

    fun getLoanInfo(loanId: String): LoanRequestInformation

    fun cancelLoan(loanId: String): Unit
}