package il.ac.technion.cs.softwaredesign

import com.google.common.collect.MutableClassToInstanceMap
import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.softwaredesign.impl.BookInfo
import il.ac.technion.cs.softwaredesign.impl.DefaultBookManager
import library.PersistentMap
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject




class PersistenMapBookMockModule : KotlinModule() {
    override fun configure() {
        bind<PersistentMap<BookInfo>>().to<PersistentMapMock<BookInfo>>()
    }
}


class BookManagerTest {//@Inject constructor(private val manager: BookManager){

    // TODO - create mock for PersistentMap and send it to BookManager so we can test BookManager only

    // also, by the current gradle configuration, the mockK library is available only under techwm-test so I'm not sure about our current files structure and unit-tests location


    private val injector = Guice.createInjector(PersistenMapBookMockModule())
    private val manager = injector.getInstance<DefaultBookManager>()



//    val manager = DefaultBookManager()

    @Test
    fun `is Id Exists`() {
        assert(manager.isIdExists("harry potter3") == false)
        manager.addBook("harry potter3", "it's about magic", 3)
        assert(manager.isIdExists("harry potter3"))
    }


    @Test
    fun `get Book Description`() {
        manager.addBook("SpongeBob2", "it's about sponges", 1)
        val spongeDesc = manager.getBookDescription("SpongeBob2")
        assert(spongeDesc == "it's about sponges")
    }

    @Test
    fun `get First Books By Add Time`() {
        manager.addBook("x", "hello", 5)
        manager.addBook("b", "world", 9)
        manager.addBook("j", "I\'m", 3)
        manager.addBook("q", "ready", 7)
        manager.addBook("b", "to", 2)
        manager.addBook("f", "rumble", 22)

        assert(manager.getFirstBooksByAddTime(3) == listOf("x", "b", "j"))
    }

}