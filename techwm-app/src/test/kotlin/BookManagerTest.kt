package il.ac.technion.cs.softwaredesign

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.softwaredesign.impl.DefaultBookManager
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class BookManagerTest {

    private val injector = Guice.createInjector(AppTestModule())
    private var manager = injector.getInstance<DefaultBookManager>()

    @BeforeEach
    fun init(){
        manager = injector.getInstance()
    }

    @Test
    fun `is Id Exists`() {
        assertFalse(manager.isIdExists("harry potter3"))
        manager.addBook("harry potter3", "it's about magic", 3).get()
        assert(manager.isIdExists("harry potter3"))
    }


    @Test
    fun `get Book Description`() {
        manager.addBook("SpongeBob2", "it's about sponges", 1).get()

        val spongeDesc = manager.getBookDescription("SpongeBob2").get()

        assertEquals(spongeDesc, "it's about sponges")
    }

    @Test
    fun `get First Books By Add Time`() {
        manager.addBook("x", "hello", 5).get()
        manager.addBook("b", "world", 9).get()
        manager.addBook("j", "I\'m", 3).get()
        manager.addBook("q", "ready", 7).get()
        manager.addBook("b", "to", 2).get()
        manager.addBook("f", "rumble", 22).get()

        assertEquals(manager.getFirstBooksByAddTime(3).get(), listOf("x", "b", "j"))
        assertNotEquals(manager.getFirstBooksByAddTime(2).get(), listOf("x", "f"))
    }

    @Test
    fun `get First Books By Add Time Extended`() {
        manager.addBook("g", "hello", 5).get()
        manager.addBook("23", "world", 9).get()
        manager.addBook("xxx", "Imm", 3).get()
        manager.addBook("rtg", "Innn", 3).get()
        manager.addBook("erg", "love", 3).get()
        manager.addBook("tt", "tonight", 3).get()
        manager.addBook("jk", "baby", 3).get()
        manager.addBook("loooooo", "ooh", 3).get()
        manager.addBook("yp", "ywah", 3).get()
        manager.addBook("fr", "grr", 3).get()
        manager.addBook("fw", "pss pss", 3).get()
        manager.addBook("ws", "kraahh krahh", 3).get()
        manager.addBook("aq", "ready", 7).get()
        manager.addBook("qm", "to", 2).get()
        manager.addBook("987", "rumble", 22).get()

        assertEquals(manager.getFirstBooksByAddTime(3).get(), listOf("g", "23", "xxx"))
        assertEquals(manager.getFirstBooksByAddTime(1).get(), listOf("g"))
        assertEquals(manager.getFirstBooksByAddTime(2).get(), listOf("g", "23"))
        assertEquals(manager.getFirstBooksByAddTime(7).get(), listOf("g", "23", "xxx", "rtg", "erg", "tt", "jk"))
        assertEquals(manager.getFirstBooksByAddTime(100).get(), listOf("g", "23", "xxx", "rtg", "erg", "tt", "jk", "loooooo", "yp", "fr", "fw", "ws", "aq", "qm", "987"))
        assertEquals(manager.getFirstBooksByAddTime(14).get(), listOf("g", "23", "xxx", "rtg", "erg", "tt", "jk", "loooooo", "yp", "fr", "fw", "ws", "aq", "qm"))
        assertEquals(manager.getFirstBooksByAddTime(15).get(), listOf("g", "23", "xxx", "rtg", "erg", "tt", "jk", "loooooo", "yp", "fr", "fw", "ws", "aq", "qm", "987"))


    }


}