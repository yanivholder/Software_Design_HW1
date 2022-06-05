package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals


class SifriTaubIntegrationTest {

    private val injector = Guice.createInjector(IntegrationalTestModule())
    private var sifri = injector.getInstance<SifriTaub>()

    @BeforeEach
    fun init(){
        sifri = injector.getInstance()
    }

    @Test
    fun `many  tokens`(){
        sifri.register("omer", "secret", true, 27).get()
        sifri.register("yaniv", "top_secret", true, 19).get()
        val omer_token1 = sifri.authenticate("omer", "secret").get()
        val yaniv_token1 = sifri.authenticate("omer", "secret").get()
        val omer_token2 = sifri.authenticate("omer", "secret").get()
        val yaniv_token2 = sifri.authenticate("omer", "secret").get()
        sifri.register("ofir", "oolala", false, 300).get()
        val omer_token3 = sifri.authenticate("omer", "secret").get()
        val ofir_token1 = sifri.authenticate("ofir", "oolala").get()

        assertThrows<IllegalArgumentException> { sifri.authenticate("doron", "x") }
        assertThrows<IllegalArgumentException> { sifri.authenticate("yaniv", "bad_pwd") }

        assertEquals(sifri.userInformation(omer_token3, "shahak"), null)
        assertDoesNotThrow { sifri.userInformation(omer_token3, "yaniv") }

        assertThrows<PermissionException> { sifri.userInformation(omer_token2, "yaniv") }
        assertThrows<PermissionException> { sifri.userInformation(omer_token1, "omer") }
        assertThrows<PermissionException> { sifri.userInformation(yaniv_token1, "ofir") }
    }

    @Test
    fun `many books`(){
        sifri.register("eilon", "secret", true, 26).get()
        val eilon_token = sifri.authenticate("eilon", "secret").get()

        sifri.addBookToCatalog(eilon_token, "b1", "good", 2).get()
        sifri.addBookToCatalog(eilon_token, "g2", "bad", 7).get()
        sifri.addBookToCatalog(eilon_token, "s3", "just ok", 1).get()
        sifri.addBookToCatalog(eilon_token, "k4", "meh", 1).get()
        assertThrows<PermissionException> { sifri.addBookToCatalog("bad token", "n5", "grr", 1).get() }
        assertThrows<IllegalArgumentException> { sifri.addBookToCatalog(eilon_token, "s3", "grr", 1).get() }
        sifri.addBookToCatalog(eilon_token, "t6", "meh", 1).get()
        sifri.addBookToCatalog(eilon_token, "a7", "oogah boogah", 8).get()

        assertEquals("bad", sifri.getBookDescription(eilon_token, "g2").get())
        val eilon_token2 = sifri.authenticate("eilon", "secret").get()
        assertThrows<PermissionException> { sifri.getBookDescription(eilon_token, "g2").get() }
        assertEquals("bad", sifri.getBookDescription(eilon_token2, "g2").get())
        assertEquals("oogah boogah", sifri.getBookDescription(eilon_token2, "a7").get())

        assertThrows<IllegalArgumentException> { sifri.addBookToCatalog(eilon_token2, "a7", "oogah boogah", 8).get() }

        assertEquals(sifri.listBookIds(eilon_token2, 4).get(), listOf("b1", "g2", "s3", "k4"))
    }

    @Test
    fun `many users`(){
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

}