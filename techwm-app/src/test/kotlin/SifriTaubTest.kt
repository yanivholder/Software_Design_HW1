package il.ac.technion.cs.softwaredesign

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class SifriTaubTest {

    // TODO: change to use Guice
//    private val injector = Guice.createInjector(AppTestModule())
//    private var sifriTaub = injector.getInstance<SifriTaub>()
//
//    @BeforeEach
//    fun init(){
//        sifriTaub = injector.getInstance()
//    }

    private var userManagerMock: UserManager = mockk(relaxed = true)
    private var bookManagerMock: BookManager = mockk(relaxed = true)
    private var sifriTaub = SifriTaub(userManagerMock, bookManagerMock)

    @BeforeEach
    fun init(){
        userManagerMock = mockk(relaxed = true)
        bookManagerMock = mockk(relaxed = true)
        sifriTaub = SifriTaub(userManagerMock, bookManagerMock)
    }

    @Test
    fun `authenticate Fail`() {
        every { userManagerMock.isUsernameAndPassMatch(any(), any()) } answers { false }

        assertThrows<IllegalArgumentException> { sifriTaub.authenticate("eilon", "xyz") }
    }

    @Test
    fun `authenticate Success`() {
        every { userManagerMock.isUsernameAndPassMatch(any(), any()) } answers { true }
        every { userManagerMock.generateUserTokenAndInvalidateOld(any()) } answers { "ohhh lovely token" }

        val result = sifriTaub.authenticate("eilon", "xyz")

        assertEquals(result,"ohhh lovely token")
    }

    @Test
    fun `register Fail`() {
        every { userManagerMock.isUsernameExists(any()) } answers { true }

        assertThrows<IllegalArgumentException> { sifriTaub.register("eilon", "xyz", true, 26) }
    }

    @Test
    fun `register Success`() {
        every { userManagerMock.isUsernameExists(any()) } answers { false }

        sifriTaub.register("eilon", "xyz", true, 26)

        verify (exactly = 1) { userManagerMock.register("eilon", "xyz", true, 26) }
    }

    @Test
    fun `user Information Fail Invalid Token`() {
        every { userManagerMock.isValidToken(any()) } answers { false }

        assertThrows<PermissionException> { sifriTaub.userInformation("Jiji Hadid", "Bamba") }
    }

    @Test
    fun `user Information Fail Non Existing User`() {
        every { userManagerMock.isValidToken(any()) } answers { true }
        every { userManagerMock.isUsernameExists(any()) } answers { false }

        val result =  sifriTaub.userInformation("Bella Hadid", "Bisli")

        assertEquals(result, null)
    }

    @Test
    fun `user Information Success`() {
        every { userManagerMock.isValidToken(any()) } answers { true }
        every { userManagerMock.isUsernameExists(any()) } answers { true }
        every { userManagerMock.getUserInformation(any()) } answers { User("Shahak", false, 27) }

        val user = sifriTaub.userInformation("any", "anyyy")

        assertEquals(user, User("Shahak", false, 27))
    }

    @Test
    fun `add Book To Catalog Fail Token not valid`() {
        every { userManagerMock.isValidToken(any()) } answers { false }

        assertThrows<PermissionException> {
            sifriTaub.addBookToCatalog("tiki token", "harry potter2", "magic", 17)
        }
    }

    @Test
    fun `add Book To Catalog Fail id already exists`() {
        every { userManagerMock.isValidToken(any()) } answers { true }
        every { bookManagerMock.isIdExists(any()) } answers { true }

        assertThrows<IllegalArgumentException> {
            sifriTaub.addBookToCatalog("riki token", "harry potter3", "magic and drugs", 2)
        }
    }

    @Test
    fun `add Book To Catalog Fail id already Success`() {
        every { userManagerMock.isValidToken(any()) } answers { true }
        every { bookManagerMock.isIdExists(any()) } answers { false }

        sifriTaub.addBookToCatalog("shliki token", "garry potter12", "only drugs", 5)

        verify (exactly = 1) { bookManagerMock.addBook("garry potter12", "only drugs", 5) }
    }

    @Test
    fun `get Book Description Fail Token not valid`() {
        every { userManagerMock.isValidToken(any()) } answers { false }

        assertThrows<PermissionException> { sifriTaub.getBookDescription("tiki tiki", "gary") }
    }

    @Test
    fun `get Book Description Fail id doesnt exists`() {
        every { userManagerMock.isValidToken(any()) } answers { true }
        every { bookManagerMock.isIdExists(any()) } answers { false }

        assertThrows<IllegalArgumentException> { sifriTaub.getBookDescription("tiki tiki", "gary") }
    }

    @Test
    fun `get Book Description`() {
        every { userManagerMock.isValidToken(any()) } answers { true }
        every { bookManagerMock.isIdExists(any()) } answers { true }
        every { bookManagerMock.getBookDescription(any()) } answers { "vey interesting, yes yes, very very" }

        val  result = sifriTaub.getBookDescription("tokin shmoken", "my_book")

        assertEquals(result, "vey interesting, yes yes, very very")
    }

    @Test
    fun `list Book Ids Fail invalid token`() {
        every { userManagerMock.isValidToken(any()) } answers { false }

        assertThrows<PermissionException> { sifriTaub.listBookIds("tokeNNN!", 5) }
    }

    @Test
    fun `list Book Ids Success`() {
        every { userManagerMock.isValidToken(any()) } answers { true }
        every { bookManagerMock.getFirstBooksByAddTime(any()) } answers { listOf("d1", "d2", "d3") }

        val result = sifriTaub.listBookIds("tokeNNN!", 5)

        assertEquals(result, listOf("d1", "d2", "d3"))
        verify (exactly = 1) { bookManagerMock.getFirstBooksByAddTime(5) }
    }

}