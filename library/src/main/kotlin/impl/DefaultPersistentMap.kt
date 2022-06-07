package impl

import javax.inject.Inject

import PersistentMap
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.system.exitProcess

class DefaultPersistentMap @Inject constructor(private val secureStorage: SecureStorage) : PersistentMap {

    private val secureStorageMaxSize = 100
    private val metedataSize = 2
    private val masterKeyName = "keys-a"
    private val executors = Executors.newCachedThreadPool()

    init {
        this.initMasterKey()
    }

    private fun serialize(value: Any): ByteArray {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(value)
        return baos.toByteArray()
    }

    private fun deserializeStringList(byteArray: ByteArray): MutableList<String> {
        val bais = ByteArrayInputStream(byteArray)
        val ois = ObjectInputStream(bais)
        return ois.readObject() as MutableList<String>
    }

    private fun deserializeString(byteArray: ByteArray): String {
        val bais = ByteArrayInputStream(byteArray)
        val ois = ObjectInputStream(bais)
        return ois.readObject() as String
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

    private fun initMasterKey() {
        putMainLogic(masterKeyName, serialize(mutableListOf<String>()), true)
    }

    private fun addToMasterKey(key: String): CompletableFuture<Unit> {
        val currentMasterKeyListSerialized = getMainLogic(masterKeyName, isMasterKey = true).get()
        if(currentMasterKeyListSerialized == null) {
            // BUG
            exitProcess(1)
        }
        val currentMasterKeyList = deserializeStringList(currentMasterKeyListSerialized)
        currentMasterKeyList.add(key)
        return putMainLogic(masterKeyName, serialize(currentMasterKeyList), true)
    }

    private fun putMainLogic(key: String, serializedValue: ByteArray, isMasterKey: Boolean): CompletableFuture<Unit> {
        val future: CompletableFuture<Unit> = CompletableFuture()

        executors.submit {
            val future_set: Set<Future<Unit>> = HashSet()
            var currentKey = key
            var currentValueSize = serializedValue.size
            var currentIteration = 0

            while (currentValueSize > 0) {
                val relevantPart = ByteArray(secureStorageMaxSize)
                System.arraycopy(
                    serializedValue, currentIteration * (secureStorageMaxSize - metedataSize),
                    relevantPart, 0,
                    if (currentValueSize >= (secureStorageMaxSize - metedataSize)) (secureStorageMaxSize - metedataSize) else currentValueSize
                )

                if (currentValueSize > secureStorageMaxSize - metedataSize) {
                    relevantPart[relevantPart.size - 1] = 1 // Symbols that this is not the last part
                    relevantPart[relevantPart.size - 2] = 98 // Read 98 bytes from this part
                } else {
                    relevantPart[relevantPart.size - 1] = 0 // Symbols that this is the last part
                    relevantPart[relevantPart.size - 2] = currentValueSize.toByte()
                }

                currentValueSize -= (secureStorageMaxSize - metedataSize)

                if (isMasterKey) {
                    val writeFuture = secureStorage.write(
                        key = serialize(currentKey),
                        value = relevantPart
                    )
                    future_set.plus(writeFuture)
                    currentKey = increaseLex(currentKey)
                } else {
                    val writeFuture = secureStorage.write(
                        key = serialize(key + currentIteration.toString()),
                        value = relevantPart
                    )
                    future_set.plus(writeFuture)
                }
                currentIteration++
            }
            future_set.forEach { it.get()}
            future.complete(Unit)
        }
        return future
    }

    override fun put(key: String, value: ByteArray): CompletableFuture<Unit> {
        val future: CompletableFuture<Unit> = CompletableFuture()

        executors.submit {
            // TODO: maybe try to do it parallel
            val putMainFuture = putMainLogic(key, value, isMasterKey = false).get()
            val addToMasterKeyFuture = addToMasterKey(key).get()
//            putMainFuture.get()
//            addToMasterKeyFuture.get()
            future.complete(Unit)
        }
        return future
    }

    private fun getMainLogic(key: String, isMasterKey: Boolean): CompletableFuture<ByteArray?> {
        // TODO: turn this code to parallel (change the reads get)
        // TODO: add the check of the write future to see that the read will succeed
        val future: CompletableFuture<ByteArray?> = CompletableFuture()

        executors.submit {
            val listOfParts = mutableListOf<ByteArray>()
            var currentIteration = 0

            var currentKey: ByteArray
            if (isMasterKey) {
                currentKey = serialize(key)
            } else {
                currentKey = serialize(key + currentIteration.toString())
            }

            var currentPart = secureStorage.read(currentKey).get()

            while (currentPart != null) {
                listOfParts.add(currentPart)
                currentIteration++

                if (isMasterKey) {
                    currentKey = serialize(increaseLex(deserializeString(currentKey)))
                } else {
                    currentKey = serialize(key + currentIteration.toString())
                }

                currentPart = secureStorage.read(currentKey).get()

            }
            if (currentIteration == 0) {
                // TODO bug
            }

            val serializedValue =
                ByteArray(((listOfParts.size - 1) * (secureStorageMaxSize - metedataSize)) + listOfParts.last()[98])
            for (i in listOfParts.indices) {
                System.arraycopy(
                    listOfParts[i], 0,
                    serializedValue, (i * (secureStorageMaxSize - metedataSize)),
                    listOfParts[i][secureStorageMaxSize - 2].toInt()
                )
            }
            future.complete(serializedValue)
        }
        return future
    }

    override fun get(key: String): CompletableFuture<ByteArray?> {
        // The check stage is not parallel
        if (!exists(key)) {
            return CompletableFuture.completedFuture(null)
        }
        return getMainLogic(key, false)
    }
    
    override fun exists(key: String): Boolean {
        return secureStorage.read(serialize(key + "0")).get() != null
    }

    override fun getAllMap(): CompletableFuture<Map<String, ByteArray?>> {
        val future: CompletableFuture<Map<String, ByteArray?>> = CompletableFuture()

        executors.submit {
            val keyListByteArray: ByteArray? = getMainLogic(masterKeyName, true).get()
            if(keyListByteArray == null) {
                // BUG
                exitProcess(1)
            }
            val keyList = deserializeStringList(keyListByteArray)
            future.complete(keyList.associateWith { this.get(it).get() })
        }
        return future
    }
}