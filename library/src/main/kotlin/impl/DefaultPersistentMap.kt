package impl

import PersistentMap
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import kotlin.system.exitProcess

class DefaultPersistentMap @Inject constructor(private val secureStorage: SecureStorage) : PersistentMap {

    private val secureStorageMaxSize = 100
    private val metedataSize = 1
    private val masterKeyName = "keys-a"

    init {
        // TODO: check what to do instead of .get()
        this.initMasterKey().get()
    }

    private fun serialize(value: Any): ByteArray {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(value)
        return baos.toByteArray()
    }

    private fun deserialize(byteArray: ByteArray): Any {
        val bais = ByteArrayInputStream(byteArray)
        val ois = ObjectInputStream(bais)
        return ois.readObject()
    }

    private fun increaseLex(str: String): String {
        // if string is empty - not happening in our case
        if(str == "") {
            return "a"
        }

        // Find first character from
        // right which is not z.
        var i = str.length - 1
        while(i >= 0 && str[i] == 'z') {
            i--
        }

        // If all characters are 'z',
        // append an 'a' at the end.
        if(i == -1 || str[i] == '-') {
            return str + 'a'
        } else {
            return str.substring(0, i) + (str[i] + 1) + str.substring(i + 1)
        }
    }

    private fun initMasterKey(): CompletableFuture<Void> {
        return putMainLogic(masterKeyName, serialize(mutableListOf<String>()), true)
    }

    private fun addToMasterKey(key: String): CompletableFuture<Void> {
        // TODO: change the master key to be in a different SecureStorage

        return getMainLogic(masterKeyName, isMasterKey = true).thenCompose { currentMasterKeyListSerialized ->
            if (currentMasterKeyListSerialized == null) {
                // BUG
                exitProcess(1)
            }
            val currentMasterKeyList = deserialize(currentMasterKeyListSerialized) as MutableList<String>
            currentMasterKeyList.add(key)
            putMainLogic(masterKeyName, serialize(currentMasterKeyList), true)
        }
    }

    private fun putMainLogic(key: String, serializedValue: ByteArray, isMasterKey: Boolean): CompletableFuture<Void> {
        val futuresList: MutableList<CompletableFuture<Unit>> = mutableListOf()
        var currentKey = key
        var currentValueSize = serializedValue.size
        var numberOfParts = serializedValue.size / (secureStorageMaxSize - metedataSize)
        if (serializedValue.size % (secureStorageMaxSize - metedataSize) > 0) {
            numberOfParts++
        }
        val serializedNumberOfParts = serialize(numberOfParts)
        val sizeHeader = ByteArray(secureStorageMaxSize)
        if (serializedNumberOfParts.size > secureStorageMaxSize) exitProcess(1) // BUG
        System.arraycopy(
            serializedNumberOfParts, 0,
            sizeHeader, 0,
            serializedNumberOfParts.size
        )
        var writeFuture = secureStorage.write(
            key = serialize(key),
            value = sizeHeader
        )
        futuresList.add(writeFuture)

        var currentIteration = 0

        while (currentValueSize > 0) {
            val relevantPart = ByteArray(secureStorageMaxSize)
            System.arraycopy(
                serializedValue, currentIteration * (secureStorageMaxSize - metedataSize),
                relevantPart, 0,
                if (currentValueSize >= (secureStorageMaxSize - metedataSize)) (secureStorageMaxSize - metedataSize) else currentValueSize
            )

            if (currentValueSize > secureStorageMaxSize - metedataSize) {
                relevantPart[relevantPart.size - 1] = 99 // Read 99 bytes from this part
            } else {
                relevantPart[relevantPart.size - 1] = currentValueSize.toByte()
            }

            currentValueSize -= (secureStorageMaxSize - metedataSize)

            if (isMasterKey) {
                currentKey = increaseLex(currentKey)
                writeFuture = secureStorage.write(
                    key = serialize(currentKey),
                    value = relevantPart
                )
                futuresList.add(writeFuture)
            } else {
                writeFuture = secureStorage.write(
                    key = serialize(key + currentIteration.toString()),
                    value = relevantPart
                )
                futuresList.add(writeFuture)
            }
            currentIteration++
        }
        return CompletableFuture.allOf(*futuresList.toTypedArray())
    }

    override fun put(key: String, value: ByteArray): CompletableFuture<Unit> {
        // TODO: maybe try to do it parallel
        return putMainLogic(key, value, isMasterKey = false).thenCompose {
            addToMasterKey(key)
        }.thenCompose { CompletableFuture.completedFuture(Unit) }
    }

    private fun listOfFuturesToFutureOfList(list: MutableList<CompletableFuture<ByteArray?>>): CompletableFuture<MutableList<ByteArray?>> {
        if (list.size == 0) return CompletableFuture.completedFuture(mutableListOf())
        else {
            return list[0].thenCompose { headValue ->
                list.removeAt(0)
                listOfFuturesToFutureOfList(list).thenCompose { returnedList ->
                    returnedList.add(headValue)
                    CompletableFuture.completedFuture(returnedList)
                }
            }
        }
    }

    private fun getMainLogic(key: String, isMasterKey: Boolean): CompletableFuture<ByteArray?> {
        return secureStorage.read(serialize(key)).thenCompose { sizeHeader ->
            val listOfFutureParts: MutableList<CompletableFuture<ByteArray?>> = mutableListOf()
            if (sizeHeader == null) return@thenCompose CompletableFuture.completedFuture(null)
            val numberOfParts = deserialize(sizeHeader) as Int

            var currentKey = serialize(key)

            for (currentIteration in 0 until numberOfParts) {
                currentKey = if (isMasterKey) {
                    serialize(increaseLex(deserialize(currentKey) as String))
                } else {
                    serialize(key + currentIteration.toString())
                }

                listOfFutureParts.add(secureStorage.read(currentKey))
            }
            listOfFuturesToFutureOfList(listOfFutureParts)
        }.thenCompose { listOfParts ->
            listOfParts.reverse()
            val serializedValue =
                ByteArray(((listOfParts.size - 1) * (secureStorageMaxSize - metedataSize)) + listOfParts.last()!![99])
            for (i in listOfParts.indices) {
                System.arraycopy(
                    listOfParts[i]!!, 0,
                    serializedValue, (i * (secureStorageMaxSize - metedataSize)),
                    listOfParts[i]!![secureStorageMaxSize - 1].toInt()
                )
            }
            CompletableFuture.completedFuture(serializedValue)
        }
    }

    override fun get(key: String): CompletableFuture<ByteArray?> {
        return exists(key).thenCompose { res ->
            if (!res) CompletableFuture.completedFuture(null)
            else getMainLogic(key, false)
        }
    }
    
    override fun exists(key: String): CompletableFuture<Boolean> {
        return secureStorage.read(serialize(key + "0")).thenCompose {
            res -> CompletableFuture.completedFuture(res != null)
        }
    }

    override fun getAllMap(): CompletableFuture<Map<String, ByteArray?>> {
        return getMainLogic(masterKeyName, true).thenCompose { keyListByteArray ->
            if(keyListByteArray == null) {
                // BUG
                exitProcess(1)
            }
            val keyList = deserialize(keyListByteArray) as MutableList<String>
            val futureList: MutableList<CompletableFuture<ByteArray?>> = mutableListOf()
//            keyList.forEach { entry -> futureList.add(this.get(entry)) }
            for (key in keyList) {
                futureList.add(this.get(key))
            }
            this.listOfFuturesToFutureOfList(futureList).thenApply { listOfValue ->
                listOfValue.reverse()
                keyList.zip(listOfValue).toMap() }
        }
    }
}