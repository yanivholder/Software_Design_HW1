package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.softwaredesign.impl.DefaultTokenStore
import il.ac.technion.cs.softwaredesign.impl.DefaultUserManager
import PersistentMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserManagerTest {

    private val injector = Guice.createInjector(AppTestModule())
    private var manager = injector.getInstance<DefaultUserManager>()

    @BeforeEach
    fun init(){
        manager = injector.getInstance()
    }

    @Test
    fun `is User name Exists`() {
        assert(!manager.isUsernameExists("eilon").get())
        manager.register("eilon", "123", true, 26).get()
        assert(manager.isUsernameExists("eilon").get())
    }

    @Test
    fun `is User name And Pass Match`() {
        manager.register("yaniv", "123", true, 18).get()
        assert(manager.isUsernameAndPassMatch("yaniv", "123").get())
        assertFalse(manager.isUsernameAndPassMatch("yaniv", "456").get())
    }

    @Test
    fun `is Valid Token`() {
        manager.register("omer", "xyz", true, 27)
        val firstToken = manager.generateUserTokenAndInvalidateOld("omer").get()
        assert(manager.isValidToken(firstToken).get())
        val secondToken = manager.generateUserTokenAndInvalidateOld("omer").get()
        assert(manager.isValidToken(secondToken).get())
        assertFalse(manager.isValidToken(firstToken).get())
    }

    @Test
    fun `get User Information`() {
        manager.register("ofir", "jjj", false, 56).get()

        val ofir = manager.getUserInformation("ofir").get()

        assertFalse(ofir!!.isFromCS)
        assertEquals(ofir.age, 56)
        assertEquals(ofir.username, "ofir")
    }
}