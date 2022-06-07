package library

import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import impl.DefaultPersistentMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ValueClass(var v1: String = "", var v2: Boolean = true, var v3: List<Int> = listOf(3)) : java.io.Serializable {

    constructor(byteArray: ByteArray?): this() {
        if(byteArray == null)
            return
        val bais = ByteArrayInputStream(byteArray)
        val ois = ObjectInputStream(bais)
        val obj = ois.readObject() as ValueClass

        this.v1 = obj.v1
        this.v2 = obj.v2
        this.v3 = obj.v3
    }
    fun serialize(): ByteArray {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(this)
        return baos.toByteArray()
    }

    override fun equals(other: Any?): Boolean {
        return this === other || other is ValueClass && (this.v1 == other.v1 && this.v2 == other.v2 && (this.v3.size == other.v3.size && this.v3.toTypedArray() contentEquals other.v3.toTypedArray()))
    }

    override fun hashCode(): Int {
        var result = v1.hashCode()
        result = 31 * result + v2.hashCode()
        result = 31 * result + v3.hashCode()
        return result
    }
}

class PersistentMapTest {

    private val injector = Guice.createInjector(LibraryTestModule())
    private var persistentMap = injector.getInstance<DefaultPersistentMap>()

    @BeforeEach
    fun init() {
        persistentMap = injector.getInstance()
    }

    @Test
    fun `get on a non-existing key returns null`() {
        // Arrange

        // Act
        val res = persistentMap.get("someKey").get()

        // Assert
        assertEquals(res, null)
    }

    @Test
    fun `get on a existing key returns it`() {
        // Arrange
        val key = "someKey"
        val value = ValueClass("someValue")

        // Act
        persistentMap.put(key, value.serialize()).get()
        val res = persistentMap.get(key).get()

        // Assert
        assertEquals(ValueClass(res), value)
    }

    @Test
    fun `get after big put return the full value`() {
        // Arrange
        val key = "someKey"
        val value = ValueClass(v3 = (1..10000).toList())

        // Act
        persistentMap.put(key, value.serialize()).get()
        val res = persistentMap.get(key).get()

        // Assert
        assertEquals(ValueClass(res), value)
    }

    @Test
    fun `write a non-existing key works (at start get(key) return null then it will return the value`() {
        // Arrange
        val key = "someKey"
        val value = ValueClass("someValue")

        // Act
        val resBeforePut = persistentMap.get(key).get()
        persistentMap.put(key, value.serialize()).get()
        val resAfterPut = persistentMap.get(key).get()

        // Assert
        assertEquals(resBeforePut, null)
        assertEquals(ValueClass(resAfterPut), value)
    }

    @Test
    fun `write an existing key and overwrite it`() {
        // Arrange
        val key = "someKey"
        val value1 = ValueClass("first someValue")
        val value2 = ValueClass("second someValue")

        // Act
        persistentMap.put(key, value1.serialize()).get()
        val resBeforePut = persistentMap.get(key).get()
        persistentMap.put(key, value2.serialize()).get()
        val resAfterPut = persistentMap.get(key).get()

        // Assert
        assertEquals(ValueClass(resBeforePut), value1)
        assertEquals(ValueClass(resAfterPut), value2)
    }

    @Test
    fun `exists on an existing key`() {
        // Arrange
        val key = "someKey"
        val value = ValueClass("someValue")

        // Act
        persistentMap.put(key, value.serialize()).get()
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
        // Arrange
        val map: Map<String, ValueClass> = (1..1000).associate { it.toString() to ValueClass("", v3 = listOf(it)) }.toMap()

        // Act
        map.forEach { entry -> persistentMap.put(entry.key, entry.value.serialize()).get() }
        val res = persistentMap.getAllMap().get()

        // Assert
        for ((key, value) in map) {
            assertEquals(value, ValueClass(res[key]))
        }
    }

    @Test
    fun `serializer works`() {
        // Arrange

        // Act
        val value1 = ValueClass(v3 = listOf(1, 2, 3))
        val value2 = ValueClass(value1.serialize())
        assertEquals(value1, value2)
    }
}