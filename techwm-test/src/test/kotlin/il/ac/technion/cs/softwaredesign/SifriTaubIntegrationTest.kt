package il.ac.technion.cs.softwaredesign

import Fakes.LoanServiceFake
import com.google.inject.Guice
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.isA
import dev.misfitlabs.kotlinguice4.getInstance
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.concurrent.CompletionException
import kotlin.concurrent.thread


class SifriTaubIntegrationTest {

    private val injector = Guice.createInjector(IntegrationalTestModule())
    private var sifri = injector.getInstance<SifriTaub>()

    @BeforeEach
    fun init(){
        sifri = injector.getInstance()
    }

    @Test
    fun `many  tokens`() {
        sifri.register("omer", "secret", true, 27).get()
        sifri.register("yaniv", "top_secret", true, 19).get()
        val omer_token1 = sifri.authenticate("omer", "secret").get()
        val yaniv_token1 = sifri.authenticate("omer", "secret").get()
        val omer_token2 = sifri.authenticate("omer", "secret").get()
        val yaniv_token2 = sifri.authenticate("omer", "secret").get()
        sifri.register("ofir", "oolala", false, 300).get()
        val omer_token3 = sifri.authenticate("omer", "secret").get()
        val ofir_token1 = sifri.authenticate("ofir", "oolala").get()

//        assertThrows<IllegalArgumentException> { sifri.authenticate("doron", "x") }
//        assertThrows<IllegalArgumentException> { sifri.authenticate("yaniv", "bad_pwd") }

        var throwable = assertThrows<CompletionException> {
            sifri.authenticate("doron", "x").join()
        }
        assertThat(throwable.cause!!, isA<IllegalArgumentException>())

        throwable = assertThrows<CompletionException> {
            sifri.authenticate("yaniv", "bad_pwd").join()
        }
        assertThat(throwable.cause!!, isA<IllegalArgumentException>())

        assertEquals(sifri.userInformation(omer_token3, "shahak").get(), null)
        assertDoesNotThrow { sifri.userInformation(omer_token3, "yaniv").get() }

//        assertThrows<PermissionException> { sifri.userInformation(omer_token2, "yaniv") }
//        assertThrows<PermissionException> { sifri.userInformation(omer_token1, "omer") }
//        assertThrows<PermissionException> { sifri.userInformation(yaniv_token1, "ofir") }

        throwable = assertThrows {
            sifri.userInformation(omer_token2, "yaniv").join()
        }
        assertThat(throwable.cause!!, isA<PermissionException>())

        throwable = assertThrows {
            sifri.userInformation(omer_token1, "omer").join()
        }
        assertThat(throwable.cause!!, isA<PermissionException>())

        throwable = assertThrows {
            sifri.userInformation(yaniv_token1, "ofir").join()
        }
        assertThat(throwable.cause!!, isA<PermissionException>())
    }

    @Test
    fun `many books`() {
        sifri.register("eilon", "secret", true, 26).get()
        val eilon_token = sifri.authenticate("eilon", "secret").get()

        sifri.addBookToCatalog(eilon_token, "b1", "good", 2).get()
        sifri.addBookToCatalog(eilon_token, "g2", "bad", 7).get()
        sifri.addBookToCatalog(eilon_token, "s3", "just ok", 1).get()
        sifri.addBookToCatalog(eilon_token, "k4", "meh", 1).get()

//        assertThrows<PermissionException> { sifri.addBookToCatalog("bad token", "n5", "grr", 1).get() }

        var throwable = assertThrows<CompletionException> {
            sifri.addBookToCatalog("bad token", "n5", "grr", 1).join()
        }
        assertThat(throwable.cause!!, isA<PermissionException>())

//        assertThrows<IllegalArgumentException> { sifri.addBookToCatalog(eilon_token, "s3", "grr", 1).get() }

        throwable = assertThrows<CompletionException> {
            sifri.addBookToCatalog(eilon_token, "s3", "grr", 1).join()
        }
        assertThat(throwable.cause!!, isA<IllegalArgumentException>())

        sifri.addBookToCatalog(eilon_token, "t6", "meh", 1).get()
        sifri.addBookToCatalog(eilon_token, "a7", "oogah boogah", 8).get()

        assertEquals("bad", sifri.getBookDescription(eilon_token, "g2").get())
        val eilon_token2 = sifri.authenticate("eilon", "secret").get()

//        assertThrows<PermissionException> { sifri.getBookDescription(eilon_token, "g2").get() }
        throwable = assertThrows<CompletionException> {
            sifri.getBookDescription(eilon_token, "g2").join()
        }
        assertThat(throwable.cause!!, isA<PermissionException>())

        assertEquals("bad", sifri.getBookDescription(eilon_token2, "g2").get())
        assertEquals("oogah boogah", sifri.getBookDescription(eilon_token2, "a7").get())

//        assertThrows<IllegalArgumentException> { sifri.addBookToCatalog(eilon_token2, "a7", "oogah boogah", 8).get() }

        throwable = assertThrows<CompletionException> {
            sifri.addBookToCatalog(eilon_token2, "a7", "oogah boogah", 8).join()
        }
        assertThat(throwable.cause!!, isA<IllegalArgumentException>())

        assertEquals(sifri.listBookIds(eilon_token2, 4).get(), listOf("b1", "g2", "s3", "k4"))
    }

    @Test
    fun `many users`() {
        sifri.register("omer", "secret", false, 27).get()
        sifri.register("yaniv1", "top_secret1", true, 1).get()
        sifri.register("yaniv2", "top_secret2", true, 2).get()
        sifri.register("yaniv3", "top_secret3", true, 3).get()
        sifri.register("yaniv4", "top_secret4", true, 4).get()
        sifri.register("yaniv5", "top_secret5", true, 5).get()
        sifri.register("yaniv6", "top_secret6", true, 6).get()
        sifri.register("yaniv7", "top_secret7", true, 7).get()
        sifri.register("yaniv8", "top_secret8", true, 8).get()
        val token = sifri.authenticate("yaniv4", "top_secret4").get()

        assertEquals(User("yaniv7", true, 7) , sifri.userInformation(token, "yaniv7").get())
        assertEquals(User("omer", false, 27) , sifri.userInformation(token, "omer").get())
    }

    @Test
    fun `submitLoanRequest cases`() {
        var throwable = assertThrows<CompletionException> {
            sifri.submitLoanRequest("bad token", "omer_loan", listOf("booky")).join()
        }
        assertThat(throwable.cause!!, isA<PermissionException>())

        sifri.register("omer", "secret", false, 27).join()
        val token = sifri.authenticate("omer", "secret").join()


        throwable = assertThrows {
            sifri.submitLoanRequest(token, "omer_loan", listOf("booky")).join()
        }
        assertThat(throwable.cause!!, isA<IllegalArgumentException>())

        sifri.addBookToCatalog(token, "booky", "goody", 2).join()

        val loan1 = sifri.submitLoanRequest(token, "omer_loan", listOf("booky")).join()

        assertEquals(LoanRequestInformation("omer_loan", listOf("booky"), "omer", LoanStatus.QUEUED), sifri.loanRequestInformation(token, loan1).join())

        val obtainedLoan = sifri.waitForBooks(token, loan1).join()

        assertEquals(LoanStatus.OBTAINED, sifri.loanRequestInformation(token, loan1).join().loanStatus)

        obtainedLoan.returnBooks().join()

        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token, loan1).join().loanStatus)

    }

    @Test
    fun `loan requests easy`(){
        sifri.register("u1", "p1", false, 27).join()
        val token1 = sifri.authenticate("u1", "p1").join()

        sifri.register("u2", "p2", false, 27).join()
        val token2 = sifri.authenticate("u2", "p2").join()

        sifri.register("u3", "p3", false, 27).join()
        val token3 = sifri.authenticate("u3", "p3").join()

        sifri.addBookToCatalog(token1, "b1", "d1", 1).join()
        sifri.addBookToCatalog(token1, "b2", "d2", 1).join()
        sifri.addBookToCatalog(token2, "b3", "d3", 1).join()
        sifri.addBookToCatalog(token2, "b4", "d4", 1).join()
        sifri.addBookToCatalog(token3, "b5", "d5", 1).join()
        sifri.addBookToCatalog(token3, "b6", "d6", 1).join()

        val loanId1 = sifri.submitLoanRequest(token1, "l1", listOf("b5", "b6")).join()
        val loanId2 = sifri.submitLoanRequest(token2, "l2", listOf("b1", "b2")).join()
        val loanId3 = sifri.submitLoanRequest(token3, "l3", listOf("b3", "b4")).join()

        val throwable = assertThrows<CompletionException> {
            sifri.waitForBooks(token1, loanId2).join()
        }
        assertThat(throwable.cause!!, isA<IllegalArgumentException>())


        val obtainedLoan1 = sifri.waitForBooks(token1, loanId1).join()
        val obtainedLoan2 = sifri.waitForBooks(token2, loanId2).join()
        val obtainedLoan3 = sifri.waitForBooks(token3, loanId3).join()

        obtainedLoan1.returnBooks().join()
        obtainedLoan2.returnBooks().join()
        obtainedLoan3.returnBooks().join()

        /**
         * check print of LoanServiceFake to see that:
         *
         * b5 loaned
         * b6 loaned
         * b1 loaned
         * b2 loaned
         * b3 loaned
         * b4 loaned
         * b5 returned
         * b6 returned
         * b1 returned
         * b2 returned
         * b3 returned
         * b4 returned
         * was printed
         */
    }

    @Test
    fun `loan requests 3 waits for 2 waits for 1`(){
        sifri.register("u1", "p1", false, 27).join()
        val token1 = sifri.authenticate("u1", "p1").join()

        sifri.register("u2", "p2", false, 27).join()
        val token2 = sifri.authenticate("u2", "p2").join()

        sifri.register("u3", "p3", false, 27).join()
        val token3 = sifri.authenticate("u3", "p3").join()

        sifri.addBookToCatalog(token1, "b1", "d1", 1).join()
        sifri.addBookToCatalog(token1, "b2", "d2", 1).join()
        sifri.addBookToCatalog(token2, "b3", "d3", 1).join()
        sifri.addBookToCatalog(token2, "b4", "d4", 1).join()
        sifri.addBookToCatalog(token3, "b5", "d5", 1).join()
        sifri.addBookToCatalog(token3, "b6", "d6", 1).join()

        val loanId1 = sifri.submitLoanRequest(token1, "l1", listOf("b1", "b2")).join()
        val loanId2 = sifri.submitLoanRequest(token2, "l2", listOf("b2", "b3")).join()
        val loanId3 = sifri.submitLoanRequest(token3, "l3", listOf("b3", "b4")).join()

        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId2).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)

        var obtainedLoan1 = sifri.waitForBooks(token1, loanId1).join()
        val obtainedLoan2 = sifri.waitForBooks(token2, loanId2)
        val obtainedLoan3 = sifri.waitForBooks(token3, loanId3)

        assertEquals(LoanStatus.OBTAINED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId2).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)

        obtainedLoan1.returnBooks().join()
        obtainedLoan1 = sifri.waitForBooks(token1, loanId1).join()

        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId2).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)

        obtainedLoan2.join().returnBooks().join()

        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)
        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token1, loanId2).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)

        obtainedLoan3.join().returnBooks().join()

        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)
        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token1, loanId2).join().loanStatus)
        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)

        obtainedLoan1.returnBooks().join()

        /**
         * check print of LoanServiceFake to see that:
         *
         * b1 loaned
         * b2 loaned
         * b1 returned
         * b2 returned
         * b2 loaned
         * b3 loaned
         * b2 returned
         * b3 returned
         * b3 loaned
         * b4 loaned
         * b3 returned
         * b4 returned
         * was printed
         */

    }

    @Test
    fun `loan requests 3 and 4 both wait for 2 and 1`(){
        sifri.register("u1", "p1", false, 27).join()
        val token1 = sifri.authenticate("u1", "p1").join()

        sifri.register("u2", "p2", false, 27).join()
        val token2 = sifri.authenticate("u2", "p2").join()

        sifri.register("u3", "p3", false, 27).join()
        val token3 = sifri.authenticate("u3", "p3").join()

        sifri.addBookToCatalog(token1, "b1", "d1", 1).join()
        sifri.addBookToCatalog(token1, "b2", "d2", 1).join()
        sifri.addBookToCatalog(token2, "b3", "d3", 1).join()
        sifri.addBookToCatalog(token2, "b4", "d4", 1).join()
        sifri.addBookToCatalog(token3, "b5", "d5", 1).join()
        sifri.addBookToCatalog(token3, "b6", "d6", 1).join()

        val loanId1 = sifri.submitLoanRequest(token1, "l1", listOf("b1", "b2")).join()
        val loanId2 = sifri.submitLoanRequest(token2, "l2", listOf("b3", "b4")).join()
        val loanId3 = sifri.submitLoanRequest(token3, "l3", listOf("b1", "b3")).join()

        var obtainedLoan1 = sifri.waitForBooks(token1, loanId1).join()

        assertEquals(LoanStatus.OBTAINED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId2).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)

        val obtainedLoan2 = sifri.waitForBooks(token2, loanId2).join()

        assertEquals(LoanStatus.OBTAINED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)
        assertEquals(LoanStatus.OBTAINED, sifri.loanRequestInformation(token1, loanId2).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)

        val obtainedLoan3 = sifri.waitForBooks(token3, loanId3)

        assertEquals(LoanStatus.OBTAINED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)
        assertEquals(LoanStatus.OBTAINED, sifri.loanRequestInformation(token1, loanId2).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)

        val loanId4 = sifri.submitLoanRequest(token3, "l4", listOf("b2", "b4")).join()

        assertEquals(LoanStatus.OBTAINED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)
        assertEquals(LoanStatus.OBTAINED, sifri.loanRequestInformation(token1, loanId2).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId4).join().loanStatus)


        obtainedLoan1.returnBooks().join()

        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)
        assertEquals(LoanStatus.OBTAINED, sifri.loanRequestInformation(token1, loanId2).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId4).join().loanStatus)

        obtainedLoan1 = sifri.waitForBooks(token1, loanId1).join()



        val obtainedLoan4 = sifri.waitForBooks(token3, loanId4)

        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)
        assertEquals(LoanStatus.OBTAINED, sifri.loanRequestInformation(token1, loanId2).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId4).join().loanStatus)


        obtainedLoan2.returnBooks().join()

        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId4).join().loanStatus)


        obtainedLoan3.join().returnBooks().join()

        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token1, loanId3).join().loanStatus)
        assertEquals(LoanStatus.QUEUED, sifri.loanRequestInformation(token1, loanId4).join().loanStatus)

        obtainedLoan1.returnBooks().join()

        obtainedLoan4.join().returnBooks().join()

        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token1, loanId4).join().loanStatus)

        assertEquals(LoanStatus.RETURNED, sifri.loanRequestInformation(token1, loanId1).join().loanStatus)


    }

}