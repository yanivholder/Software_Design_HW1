package il.ac.technion.cs.softwaredesign

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.isA
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Exception
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException


class SifriTaubTest {

    private var userManagerMock: UserManager = mockk(relaxed = true)
    private var bookManagerMock: BookManager = mockk(relaxed = true)
    private var loanManagerMock: LoanManager = mockk(relaxed = true)
    private var sifriTaub = SifriTaub(userManagerMock, bookManagerMock, loanManagerMock)

    @BeforeEach
    fun init(){
        userManagerMock = mockk(relaxed = true)
        bookManagerMock = mockk(relaxed = true)
        loanManagerMock = mockk(relaxed = true)

        sifriTaub = SifriTaub(userManagerMock, bookManagerMock, loanManagerMock)
    }

    @Test
    fun `authenticate Fail`() {
        every { userManagerMock.isUsernameAndPassMatch(any(), any()) } answers { CompletableFuture.completedFuture(false) }

        val throwable = assertThrows<CompletionException> {
            sifriTaub.authenticate("eilon", "xyz").join()
        }
        assertThat(throwable.cause!!, isA<IllegalArgumentException>())
    }

    @Test
    fun `authenticate Success`() {
        every { userManagerMock.isUsernameAndPassMatch(any(), any()) } answers { CompletableFuture.completedFuture(true) }
        every { userManagerMock.generateUserTokenAndInvalidateOld(any()) } answers { CompletableFuture.completedFuture("ohhh lovely token") }

        val result = sifriTaub.authenticate("eilon", "xyz").get()

        assertEquals(result,"ohhh lovely token")
    }

    @Test
    fun `register Fail`() {
        every { userManagerMock.isUsernameExists(any()) } answers { CompletableFuture.completedFuture(true) }

        val throwable = assertThrows<CompletionException> {
            sifriTaub.register("eilon", "xyz", true, 26).join()
        }
        assertThat(throwable.cause!!, isA<IllegalArgumentException>())
    }

    @Test
    fun `register Success`() {
        every { userManagerMock.isUsernameExists(any()) } answers { CompletableFuture.completedFuture(false) }

        sifriTaub.register("eilon", "xyz", true, 26)

        verify (exactly = 1) { userManagerMock.register("eilon", "xyz", true, 26) }
    }

    @Test
    fun `user Information Fail Invalid Token`() {
        every { userManagerMock.isValidToken(any()) } answers { CompletableFuture.completedFuture(false) }

        val throwable = assertThrows<CompletionException> {
            sifriTaub.userInformation("Jiji Hadid", "Bamba").join()
        }
        assertThat(throwable.cause!!, isA<PermissionException>())
    }

    @Test
    fun `user Information Fail Non Existing User`() {
        every { userManagerMock.isValidToken(any()) } answers { CompletableFuture.completedFuture(true) }
        every { userManagerMock.isUsernameExists(any()) } answers { CompletableFuture.completedFuture(false) }

        val result =  sifriTaub.userInformation("Bella Hadid", "Bisli").get()

        assertEquals(result, null)
    }

    @Test
    fun `user Information Success`() {
        every { userManagerMock.isValidToken(any()) } answers { CompletableFuture.completedFuture(true) }
        every { userManagerMock.isUsernameExists(any()) } answers { CompletableFuture.completedFuture(true) }
        every { userManagerMock.getUserInformation(any()) } answers { CompletableFuture.completedFuture(User("Shahak", false, 27)) }

        val user = sifriTaub.userInformation("any", "anyyy").get()

        assertEquals(user, User("Shahak", false, 27))
    }

    @Test
    fun `add Book To Catalog Fail Token not valid`() {
        every { userManagerMock.isValidToken(any()) } answers { CompletableFuture.completedFuture(false) }

        val throwable = assertThrows<CompletionException> {
            sifriTaub.addBookToCatalog("tiki token", "harry potter2", "magic", 17).join()
        }
        assertThat(throwable.cause!!, isA<PermissionException>())
    }

    @Test
    fun `add Book To Catalog Fail id already exists`() {
        every { userManagerMock.isValidToken(any()) } answers { CompletableFuture.completedFuture(true) }
        every { bookManagerMock.isIdExists(any()) } answers { CompletableFuture.completedFuture(true) }

        val throwable = assertThrows<CompletionException> {
            sifriTaub.addBookToCatalog("riki token", "harry potter3", "magic and drugs", 2).join()
        }
        assertThat(throwable.cause!!, isA<IllegalArgumentException>())

        verify (exactly = 0) { bookManagerMock.addBook(any(), any(), any()) }

    }

    @Test
    fun `add Book To Catalog Fail id already Success`() {
        every { userManagerMock.isValidToken(any()) } answers { CompletableFuture.completedFuture(true) }
        every { bookManagerMock.isIdExists(any()) } answers { CompletableFuture.completedFuture(false) }

        sifriTaub.addBookToCatalog("shliki token", "garry potter12", "only drugs", 5)

        verify (exactly = 1) { bookManagerMock.addBook("garry potter12", "only drugs", 5) }
    }


    @Test
    fun `get Book Description Fail Token not valid`() {
        every { userManagerMock.isValidToken(any()) } answers { CompletableFuture.completedFuture(false) }

        val throwable = assertThrows<CompletionException> {
            sifriTaub.getBookDescription("tiki tiki", "gary").join()
        }
        assertThat(throwable.cause!!, isA<PermissionException>())
    }

    @Test
    fun `get Book Description Fail id doesnt exists`() {
        every { userManagerMock.isValidToken(any()) } answers { CompletableFuture.completedFuture(true) }
        every { bookManagerMock.isIdExists(any()) } answers { CompletableFuture.completedFuture(false) }

        val throwable = assertThrows<CompletionException> {
            sifriTaub.getBookDescription("tiki tiki", "gary").join()
        }
        assertThat(throwable.cause!!, isA<IllegalArgumentException>())
    }

    @Test
    fun `get Book Description`() {
        every { userManagerMock.isValidToken(any()) } answers { CompletableFuture.completedFuture(true) }
        every { bookManagerMock.isIdExists(any()) } answers { CompletableFuture.completedFuture(true) }
        every { bookManagerMock.getBookDescription(any()) } answers { CompletableFuture.completedFuture("vey interesting, yes yes, very very") }

        val  result = sifriTaub.getBookDescription("tokin shmoken", "my_book").get()

        assertEquals(result, "vey interesting, yes yes, very very")
    }

    @Test
    fun `list Book Ids Fail invalid token`() {
        every { userManagerMock.isValidToken(any()) } answers { CompletableFuture.completedFuture(false) }

        val throwable = assertThrows<CompletionException> {
            sifriTaub.listBookIds("tokeNNN!", 5).join()
        }
        assertThat(throwable.cause!!, isA<PermissionException>())
    }

    @Test
    fun `list Book Ids Success`() {
        every { userManagerMock.isValidToken(any()) } answers { CompletableFuture.completedFuture(true) }
        every { bookManagerMock.getFirstBooksByAddTime(any()) } answers { CompletableFuture.completedFuture(listOf("d1", "d2", "d3")) }

        val result = sifriTaub.listBookIds("tokeNNN!", 5).get()

        assertEquals(result, listOf("d1", "d2", "d3"))
        verify (exactly = 1) { bookManagerMock.getFirstBooksByAddTime(5) }
    }

    @Test
    fun `need to implement loaning tests`() {
        assert(false)
    }

}