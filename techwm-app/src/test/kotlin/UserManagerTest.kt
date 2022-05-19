package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.softwaredesign.impl.DefaultTokenManager
import il.ac.technion.cs.softwaredesign.impl.DefaultUserManager
import il.ac.technion.cs.softwaredesign.impl.UserInfo
import library.PersistentMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class PersistentMapUserMockModule : KotlinModule() {
    override fun configure() {
        bind<PersistentMap>().to<PersistentMapFake>()
        bind<TokenManager>().to<DefaultTokenManager>()
    }
}

class UserManagerTest {

    private val injector = Guice.createInjector(PersistentMapUserMockModule())
    private var manager = injector.getInstance<DefaultUserManager>()

    @BeforeEach
    fun init(){
        manager = injector.getInstance<DefaultUserManager>()
    }

    @Test
    fun `is User name Exists`() {
        assert(!manager.isUsernameExists("eilon"))
        manager.register("eilon", "123", true, 26)
        assert(manager.isUsernameExists("eilon"))
    }

    @Test
    fun `is User name And Pass Match`() {
        manager.register("yaniv", "123", true, 18)
        assert(manager.isUsernameAndPassMatch("yaniv", "123"))
        assertFalse(manager.isUsernameAndPassMatch("yaniv", "456"))
    }

    @Test
    fun `is Valid Token`() {
        manager.register("omer", "xyz", true, 27)
        val firstToken = manager.generateUserTokenAndInvalidateOld("omer")
        assert(manager.isValidToken(firstToken))
        val secondToken = manager.generateUserTokenAndInvalidateOld("omer")
        assert(manager.isValidToken(secondToken))
        assertFalse(manager.isValidToken(firstToken))
    }

    @Test
    fun `get User Information`() {
        manager.register("ofir", "jjj", false, 56)

        val ofir = manager.getUserInformation("ofir")

        assertFalse(ofir.isFromCS)
        assertEquals(ofir.age, 56)
        assertEquals(ofir.username, "ofir")
    }
}