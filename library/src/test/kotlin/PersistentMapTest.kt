package library

import library.impl.DefaultPersistentMap
import library.impl.ObjectSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class valueClass(val v1: String, val v2: Boolean = true, val v3: Int = 3)

class PersistentMapTest {

    private var secureStorageFake = SecureStorageFake()
    private var persistentMap = DefaultPersistentMap<valueClass>(secureStorageFake)

    @BeforeEach
    fun init() {
        secureStorageFake = SecureStorageFake()
        persistentMap = DefaultPersistentMap(secureStorageFake)
    }

    @Test
    fun `get on a non-existing key returns null`() {
        // Arrange

        // Act
        val res = persistentMap.get("someKey")

        // Assert
        assertEquals(res, null)
    }

    @Test
    fun `get on a existing key returns it`() {
        // Arrange
        val key = "someKey"
        val value = valueClass("someValue")

        // Act
        persistentMap.put(key, value)
        val res = persistentMap.get(key)

        // Assert
        assertEquals(res, value)
    }

    @Test
    fun `get after big put return the full value`() {
        // Arrange
        val listPersistentMap = DefaultPersistentMap<List<Int>>(secureStorageFake)

        val key = "someKey"
        val value = (1..10000).toList()

        // Act
        listPersistentMap.put(key, value)
        val res = listPersistentMap.get(key)

        // Assert
        assertEquals(res, value)
    }

    @Test
    fun `write a non-existing key works (at start get(key) return null then it will return the value`() {
        // Arrange
        val key = "someKey"
        val value = valueClass("someValue")

        // Act
        val resBeforePut = persistentMap.get(key)
        persistentMap.put(key, value)
        val resAfterPut = persistentMap.get(key)

        // Assert
        assertEquals(resBeforePut, null)
        assertEquals(resAfterPut, value)
    }

    @Test
    fun `write an existing key and overwrite it`() {
        // Arrange
        val key = "someKey"
        val value1 = valueClass("first someValue")
        val value2 = valueClass("second someValue")

        // Act
        persistentMap.put(key, value1)
        val resBeforePut = persistentMap.get(key)
        persistentMap.put(key, value2)
        val resAfterPut = persistentMap.get(key)

        // Assert
        assertEquals(resBeforePut, value1)
        assertEquals(resAfterPut, value2)
    }

    @Test
    fun `exists on an existing key`() {
        // Arrange
        val key = "someKey"
        val value = valueClass("someValue")

        // Act
        persistentMap.put(key, value)
        val res = persistentMap.exists(key)

        // Assert
        assertEquals(res, true)
    }

    @Test
    fun `exists on a non-existing key`() {
        // Arrange
        val key = "someKey"

        // Act
        val res = persistentMap.exists(key)

        // Assert
        assertEquals(res, false)
    }

    @Test
    fun `get-All`() {
        val map = (1..1000).associate { it.toString() to valueClass("", v3 = it) }.toMap()

        // Act
        map.forEach { entry -> persistentMap.put(entry.key, entry.value) }
        val res = persistentMap.getAllMap()

        // Assert
        assertEquals(res, map)
    }

    @Test
    fun `serializer works`() {
        val listA = listOf(1, 2, 3, 4, 5)
        val ser = ObjectSerializer.serialize(listA)
        val listB = ObjectSerializer.deserialize(ser)
        assertEquals(listA, listB)
    }

    @Test
    fun `temp`() {
        val map = mutableMapOf<ByteArray, ByteArray>()

        map[ObjectSerializer.serialize("key")] = ObjectSerializer.serialize("value")

        assertEquals(map[ObjectSerializer.serialize("key")], ObjectSerializer.serialize("value"))
    }
}