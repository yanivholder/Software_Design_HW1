package library

import library.impl.DefaultPersistentMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ValueClass(val v1: String, val v2: Boolean = true, val v3: Int = 3) {
    
}

class PersistentMapTest {

    private var secureStorageFake = SecureStorageFake()
    private var persistentMap = DefaultPersistentMap<ValueClass>(secureStorageFake)

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
        val value = ValueClass("someValue")

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
        val value = ValueClass("someValue")

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
        val value1 = ValueClass("first someValue")
        val value2 = ValueClass("second someValue")

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
        val value = ValueClass("someValue")

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
        val map = (1..1000).associate { it.toString() to ValueClass("", v3 = it) }.toMap()

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
        assertEquals(ValueClass("").v2, ((ObjectSerializer.deserialize(ObjectSerializer.serialize(ValueClass("")))) as ValueClass).v2)
//        assertEquals(ObjectSerializer.serialize(ValueClass("")), ObjectSerializer.serialize(ValueClass("")))
//        assertEquals("".toByteArray(), "".toByteArray())


//        val map = mutableMapOf<ByteArray, ByteArray>()
//
//        val t10 = ObjectSerializer.serialize("key")
//        map[t10] = ObjectSerializer.serialize("value")
//        val t11 = ObjectSerializer.serialize("key")
//        val t2 = map.get(ObjectSerializer.serialize("key"))
//
//        assertEquals(t10, t11)
//        assertEquals(map.get(ObjectSerializer.serialize("key")), ObjectSerializer.serialize("value"))
    }
}