package Fakes

import il.ac.technion.cs.softwaredesign.loan.LoanService
import java.util.concurrent.CompletableFuture

class LoanServiceFake : LoanService {

    override fun loanBook(id: String): CompletableFuture<Unit>{
        println(id.plus(" loaned"))
        return CompletableFuture.completedFuture(Unit)
    }

    override fun returnBook(id: String): CompletableFuture<Unit> {
        println(id.plus(" returned"))
        return CompletableFuture.completedFuture(Unit)
    }
}