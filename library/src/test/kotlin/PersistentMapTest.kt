package library

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import io.mockk.every
import io.mockk.mockk
import library.impl.DefaultPersistentMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

//class PersistentMapTestModule : KotlinModule() {
//    override fun configure() {
//        bind<SecureStorage>().to<SecureStorageFake>()
//    }
//}

class MapValue(val v1: String, val v2: Boolean, val v3: Int)

class PersistentMapTest {

//    private val injector = Guice.createInjector(PersistentMapTestModule())
//    private val persistentMap = injector.getInstance<DefaultPersistentMap<MapValue>>()
    private var secureStorageFake = SecureStorageFake()
    private var persistentMap = DefaultPersistentMap<String>(secureStorageFake)

    @BeforeEach
    fun init() {
        secureStorageFake = SecureStorageFake()
        persistentMap = DefaultPersistentMap(secureStorageFake)
    }

    @Test
    fun `get on a non-existing key returns null`() {
        // Arrange

        // Act
        val res = persistentMap.get("")

        // Assert
        assertEquals(res, null)
    }

    @Test
    fun `get on a existing key returns it`() {
        // Arrange
//        val secureStorageMock = mockk<SecureStorage>();
//        val persistentMap = DefaultPersistentMap<String>(secureStorageMock);

        // Act
//        var res = persistentMap.get("");

        // Assert
//        assertEquals(res, );
    }

    @Test
    fun `get after big put return the full value`() {
        // Arrange
//        val secureStorageMock = mockk<SecureStorage>();
//        val persistentMap = DefaultPersistentMap<String>(secureStorageMock);

        // Act
//        var res = persistentMap.get("");

        // Assert
//        assertEquals(res, );
    }

    @Test
    fun `write an empty key works (before get return null after return the value`() {
        // Arrange
//        val secureStorageMock = mockk<SecureStorage>();
//        val persistentMap = DefaultPersistentMap<String>(secureStorageMock);

        // Act
//        var res = persistentMap.get("");

        // Assert
//        assertEquals(res, );
    }

    @Test
    fun `write an existing key overwrites it`() {
        // Arrange
//        val secureStorageMock = mockk<SecureStorage>();
//        val persistentMap = DefaultPersistentMap<String>(secureStorageMock);

        // Act
//        var res = persistentMap.get("");

        // Assert
//        assertEquals(res, );
    }

    @Test
    fun `exists on an existing key`() {
        // Arrange
//        val secureStorageMock = mockk<SecureStorage>();
//        val persistentMap = DefaultPersistentMap<String>(secureStorageMock);

        // Act
//        var res = persistentMap.get("");

        // Assert
//        assertEquals(res, );
    }

    @Test
    fun `exists on a non-existing key`() {
        // Arrange
//        val secureStorageMock = mockk<SecureStorage>();
//        val persistentMap = DefaultPersistentMap<String>(secureStorageMock);

        // Act
//        var res = persistentMap.get("");

        // Assert
//        assertEquals(res, );
    }

    @Test
    fun `get-All`() {
        // Arrange
//        val secureStorageMock = mockk<SecureStorage>();
//        val persistentMap = DefaultPersistentMap<String>(secureStorageMock);

        // Act
//        var res = persistentMap.get("");

        // Assert
//        assertEquals(res, );
    }
}