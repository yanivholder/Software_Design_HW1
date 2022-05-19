package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.softwaredesign.impl.BookInfo
import il.ac.technion.cs.softwaredesign.impl.DefaultBookManager
import il.ac.technion.cs.softwaredesign.impl.DefaultUserManager
import library.PersistentMap
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach


class PersistentMapBookMockModule : KotlinModule() {
    override fun configure() {
        bind<PersistentMap>().to<PersistentMapFake>()
    }
}


class BookManagerTest {//@Inject constructor(private val manager: BookManager){

    // TODO - create mock for PersistentMap and send it to BookManager so we can test BookManager only

    private val injector = Guice.createInjector(PersistentMapBookMockModule())
    private var manager = injector.getInstance<DefaultBookManager>()

    @BeforeEach
    fun init(){
        manager = injector.getInstance<DefaultBookManager>()
    }

    @Test
    fun `is Id Exists`() {
        assertFalse(manager.isIdExists("harry potter3"))
        manager.addBook("harry potter3", "it's about magic", 3)
        assert(manager.isIdExists("harry potter3"))
    }


    @Test
    fun `get Book Description`() {
        manager.addBook("SpongeBob2", "it's about sponges", 1)

        val spongeDesc = manager.getBookDescription("SpongeBob2")

        assertEquals(spongeDesc, "it's about sponges")
    }

    @Test
    fun `get First Books By Add Time`() {
        manager.addBook("x", "hello", 5)
        manager.addBook("b", "world", 9)
        manager.addBook("j", "I\'m", 3)
        manager.addBook("q", "ready", 7)
        manager.addBook("b", "to", 2)
        manager.addBook("f", "rumble", 22)

        assertEquals(manager.getFirstBooksByAddTime(3), listOf("x", "b", "j"))
        assertNotEquals(manager.getFirstBooksByAddTime(2), listOf("x", "f"))
    }

    @Test
    fun `get First Books By Add Time Extended`() {
        manager.addBook("g", "hello", 5)
        manager.addBook("23", "world", 9)
        manager.addBook("xxx", "Imm", 3)
        manager.addBook("rtg", "Innn", 3)
        manager.addBook("erg", "love", 3)
        manager.addBook("tt", "tonight", 3)
        manager.addBook("jk", "baby", 3)
        manager.addBook("loooooo", "ooh", 3)
        manager.addBook("yp", "ywah", 3)
        manager.addBook("fr", "grr", 3)
        manager.addBook("fw", "pss pss", 3)
        manager.addBook("ws", "kraahh krahh", 3)
        manager.addBook("aq", "ready", 7)
        manager.addBook("qm", "to", 2)
        manager.addBook("987", "rumble", 22)

        assertEquals(manager.getFirstBooksByAddTime(3), listOf("g", "23", "xxx"))
        assertEquals(manager.getFirstBooksByAddTime(1), listOf("g"))
        assertEquals(manager.getFirstBooksByAddTime(2), listOf("g", "23"))
        assertEquals(manager.getFirstBooksByAddTime(7), listOf("g", "23", "xxx", "rtg", "erg", "tt", "jk"))
        assertEquals(manager.getFirstBooksByAddTime(100), listOf("g", "23", "xxx", "rtg", "erg", "tt", "jk", "loooooo", "yp", "fr", "fw", "ws", "aq", "qm", "987"))
        assertEquals(manager.getFirstBooksByAddTime(14), listOf("g", "23", "xxx", "rtg", "erg", "tt", "jk", "loooooo", "yp", "fr", "fw", "ws", "aq", "qm"))
        assertEquals(manager.getFirstBooksByAddTime(15), listOf("g", "23", "xxx", "rtg", "erg", "tt", "jk", "loooooo", "yp", "fr", "fw", "ws", "aq", "qm", "987"))


    }


}