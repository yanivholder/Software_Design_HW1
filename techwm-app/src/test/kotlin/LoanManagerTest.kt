package il.ac.technion.cs.softwaredesign;

import com.google.inject.Guice;
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.softwaredesign.impl.BookInfo
import il.ac.technion.cs.softwaredesign.impl.DefaultLoanManager
import il.ac.technion.cs.softwaredesign.loan.LoanService
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


public class LoanManagerTest {
//    private val injector = Guice.createInjector(AppTestModule())
//    private var manager = injector.getInstance<DefaultLoanManager>()

    private var loanServiceMock: LoanService = mockk(relaxed = true)
    private var manager = DefaultLoanManager(loanServiceMock)

    @BeforeEach
    fun init(){
//        manager = injector.getInstance()
        loanServiceMock = mockk(relaxed = true)
        manager = DefaultLoanManager(loanServiceMock)
    }

    @Test
    fun `create and cancel loan requests`() {
        val loanId_1 = manager.createNewLoan("loanName1", "owner1", listOf("b1", "b2"))
        val loanId_2 = manager.createNewLoan("loanName2", "owner2", listOf("b2", "b3"))
        val loanId_3 = manager.createNewLoan("loanName3", "owner1", listOf("b1", "b4"))

        assertEquals(LoanRequestInformation("loanName1", listOf("b1", "b2"), "owner1", LoanStatus.QUEUED), manager.getLoanInfo(loanId_1))
        assertEquals(LoanRequestInformation("loanName2", listOf("b2", "b3"), "owner2", LoanStatus.QUEUED), manager.getLoanInfo(loanId_2))
        assertEquals(LoanRequestInformation("loanName3", listOf("b1", "b4"), "owner1", LoanStatus.QUEUED), manager.getLoanInfo(loanId_3))
        assertEquals(null, manager.getLoanInfo("non existing loan id"))

        manager.cancelLoan(loanId_1)
        assertEquals(LoanStatus.CANCELED, manager.getLoanInfo(loanId_1)!!.loanStatus)
        assertEquals(LoanStatus.QUEUED, manager.getLoanInfo(loanId_2)!!.loanStatus)
        assertEquals(LoanStatus.QUEUED, manager.getLoanInfo(loanId_3)!!.loanStatus)

        manager.cancelLoan(loanId_2)
        val loanId_5 = manager.createNewLoan("loanName5", "owner5", listOf("b2", "b5"))
        assertEquals(LoanStatus.CANCELED, manager.getLoanInfo(loanId_1)!!.loanStatus)
        assertEquals(LoanStatus.CANCELED, manager.getLoanInfo(loanId_2)!!.loanStatus)
        assertEquals(LoanStatus.QUEUED, manager.getLoanInfo(loanId_3)!!.loanStatus)
        assertEquals(LoanRequestInformation("loanName5", listOf("b2", "b5"), "owner5", LoanStatus.QUEUED), manager.getLoanInfo(loanId_5))

    }

    @Test
    fun waitForLoan(){
        // Prepare
        manager.addBook("b1", 5)
        manager.addBook("b2", 3)
        val loanId_1 = manager.createNewLoan("loanName1", "owner1", listOf("b1", "b2"))

        // Act 1
        val obtainedLoan = manager.waitForLoan(loanId_1).join()
        // Assert 1
        verify (exactly = 2) { loanServiceMock.loanBook(any()) }
        assertEquals(4, manager.getBookAvailableAmount("b1"))
        assertEquals(2, manager.getBookAvailableAmount("b2"))

        // Act 2
        obtainedLoan.returnBooks().join()
        // Assert 2
        verify (exactly = 2) { loanServiceMock.returnBook(any()) }
        assertEquals(5, manager.getBookAvailableAmount("b1"))
        assertEquals(3, manager.getBookAvailableAmount("b2"))
    }

    @Test
    fun `waitForLoan on canceled loan`(){
        // Prepare
        manager.addBook("b1", 5)
        manager.addBook("b2", 3)
        val loanId_1 = manager.createNewLoan("loanName1", "owner1", listOf("b1", "b2"))
        manager.cancelLoan(loanId_1)

        // Act 1
        val obtainedLoan = manager.waitForLoan(loanId_1).join()
        // Assert 1
        verify (exactly = 0) { loanServiceMock.loanBook(any()) }

        // Act 2
        obtainedLoan.returnBooks().join()
        // Assert 2
        verify (exactly = 0) { loanServiceMock.returnBook(any()) }
    }

    @Test
    fun `waitForLoan on obtained loan`(){
        // Prepare
        manager.addBook("b1", 5)
        manager.addBook("b2", 3)
        val loanId_1 = manager.createNewLoan("loanName1", "owner1", listOf("b1", "b2"))
        manager.cancelLoan(loanId_1)

        val obtainedLoan1 = manager.waitForLoan(loanId_1).join()
        obtainedLoan1.returnBooks().join()

        // Act 1
        val obtainedLoan2 = manager.waitForLoan(loanId_1).join()
        // Assert 1
        verify (exactly = 0) { loanServiceMock.loanBook(any()) }

        // Act 2
        obtainedLoan2.returnBooks().join()
        // Assert 2
        verify (exactly = 0) { loanServiceMock.returnBook(any()) }
    }
}









