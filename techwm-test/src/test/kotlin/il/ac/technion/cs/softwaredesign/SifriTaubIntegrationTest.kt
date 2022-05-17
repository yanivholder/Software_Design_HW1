package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals


class SifriTaubIntegrationTest {

    private val injector = Guice.createInjector(SifriTaubModule())
    private var sifri = injector.getInstance<SifriTaub>()

    @BeforeEach
    fun init(){
        sifri = injector.getInstance<SifriTaub>()
    }

    @Test
    fun `many  tokens`(){
        sifri.register("omer", "secret", true, 27)
        sifri.register("yaniv", "top_secret", true, 19)
        val omer_token1 = sifri.authenticate("omer", "secret")
        val yaniv_token1 = sifri.authenticate("omer", "secret")
        val omer_token2 = sifri.authenticate("omer", "secret")
        val yaniv_token2 = sifri.authenticate("omer", "secret")
        sifri.register("ofir", "oolala", false, 300)
        val omer_token3 = sifri.authenticate("omer", "secret")
        val ofir_token1 = sifri.authenticate("ofir", "oolala")

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
        sifri.register("eilon", "secret", true, 26)
        val eilon_token = sifri.authenticate("eilon", "secret")

        sifri.addBookToCatalog(eilon_token, "b1", "good", 2)
        sifri.addBookToCatalog(eilon_token, "g2", "bad", 7)
        sifri.addBookToCatalog(eilon_token, "s3", "just ok", 1)
        sifri.addBookToCatalog(eilon_token, "k4", "meh", 1)
        assertThrows<PermissionException> { sifri.addBookToCatalog("bad token", "n5", "grr", 1) }
        assertThrows<IllegalArgumentException> { sifri.addBookToCatalog(eilon_token, "s3", "grr", 1) }
        sifri.addBookToCatalog(eilon_token, "t6", "meh", 1)
        sifri.addBookToCatalog(eilon_token, "a7", "oogah boogah", 8)

        assertEquals("bad", sifri.getBookDescription(eilon_token, "g2"))
        val eilon_token2 = sifri.authenticate("eilon", "secret")
        assertThrows<PermissionException> { sifri.getBookDescription(eilon_token, "g2") }
        assertEquals("bad", sifri.getBookDescription(eilon_token2, "g2"))
        assertEquals("oogah boogah", sifri.getBookDescription(eilon_token2, "a7"))

        assertThrows<IllegalArgumentException> { sifri.addBookToCatalog(eilon_token2, "a7", "oogah boogah", 8) }

        assertEquals(sifri.listBookIds(eilon_token2), listOf("b1", "g2", "s3", "k4"))
    }

    @Test
    fun `many users`(){
        sifri.register("omer", "secret", false, 27)
        sifri.register("yaniv1", "top_secret1", true, 1)
        sifri.register("yaniv2", "top_secret2", true, 2)
        sifri.register("yaniv3", "top_secret3", true, 3)
        sifri.register("yaniv4", "top_secret4", true, 4)
        sifri.register("yaniv5", "top_secret5", true, 5)
        sifri.register("yaniv6", "top_secret6", true, 6)
        sifri.register("yaniv7", "top_secret7", true, 7)
        sifri.register("yaniv8", "top_secret8", true, 8)
        val token = sifri.authenticate("yaniv4", "top_secret4")

        assertEquals(User("yaniv7", true, 7) , sifri.userInformation(token, "yaniv7"))
        assertEquals(User("omer", false, 27) , sifri.userInformation(token, "omer"))
    }


}