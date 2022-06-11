package il.ac.technion.cs.softwaredesign.impl
//package il.ac.technion.cs.softwaredesign
import il.ac.technion.cs.softwaredesign.LoanManager
import il.ac.technion.cs.softwaredesign.LoanStatus
import il.ac.technion.cs.softwaredesign.LoanRequestInformation
import il.ac.technion.cs.softwaredesign.ObtainedLoan
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.Comparator

data class LoanInfo(var loanId: String = "N.A", var loanReq: LoanRequestInformation = LoanRequestInformation("", listOf(""), "", LoanStatus.QUEUED)) {
    override fun equals(other: Any?): Boolean {
        return loanId == (other as LoanInfo).loanId
    }
}

class LoanComparator: Comparator<LoanInfo>{
    override fun compare(o1: LoanInfo?, o2: LoanInfo?): Int {
        if(o1 == null || o2 == null){
            return 0;
        }
        return o1.loanId.toInt() - o2.loanId.toInt()
    }
}

class DefaultLoanManager : LoanManager{

    private var loanIdRunner: Int = 0
    private var queue: LinkedList<LoanInfo> = LinkedList(listOf())

    override fun createNewLoan(loanName: String, ownerId: String, bookIds: List<String>): String{
        val newLoanId = loanIdRunner.toString()
        val newLoan = LoanInfo(newLoanId, loanReq = LoanRequestInformation(loanName, bookIds, ownerId, LoanStatus.QUEUED))
        queue.addLast(newLoan)
        loanIdRunner += 1
        return newLoanId
    }

    override fun loanExists(loanId: String): Boolean{
        val defaultLoanReq = LoanRequestInformation("", listOf(""),"",LoanStatus.QUEUED)
        return queue.contains(LoanInfo(loanId, defaultLoanReq))
    }

    override fun getLoanInfo(loanId: String): LoanRequestInformation{
        return queue.first { it.loanId == loanId }.loanReq
    }

    override fun cancelLoan(loanId: String): Unit {
        // TODO - need to change the status somehow
        val elem = queue.first { it.loanId == loanId }

        // TODO - return all books under this loan
    }

}