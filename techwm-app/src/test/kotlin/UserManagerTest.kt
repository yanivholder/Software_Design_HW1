package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import org.junit.jupiter.api.Test

class UserManagerTest {

    // TODO - create mock for secureStorage and send it to UserManager
    private val injector = Guice.createInjector(SifriTaubModule())
    private val manager = injector.getInstance<UserManager>()



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
        assert(manager.isUsernameAndPassMatch("yaniv", "456") == false)
    }

    @Test
    fun `is Valid Token`() {
        manager.register("omer", "xyz", true, 27)
        val firstToken = manager.generateUserToken("omer")
        assert(manager.isValidToken(firstToken))
        val secondToken = manager.generateUserToken("omer")
        assert(manager.isValidToken(secondToken))
        assert(manager.isValidToken(firstToken) == false)
    }

    @Test
    fun `get User Information`() {
        manager.register("ofir", "jjj", false, 56)
        val ofir = manager.getUserInformation("ofir")
        assert(ofir.isFromCS == false)
        assert(ofir.age == 56)
        assert(ofir.username == "ofir")
    }
}