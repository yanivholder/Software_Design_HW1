package library.impl

import javax.inject.Inject

import library.PersistentMap
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import kotlinx.serialization.decodeFromString
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class DefaultPersistentMap @Inject constructor(private val secureStorage: SecureStorage) : PersistentMap {

    private val secureStorageMaxSize = 100
    private val metedataSize = 2
    private val masterKeyName = "keys-a"

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

    private fun addToMasterKey(key: String) {
        val currentMasterKeyListSerialized = getMainLogic(masterKeyName, isMasterKey = true)
        val currentMasterKeyList = deserializeStringList(currentMasterKeyListSerialized)
        currentMasterKeyList.add(key)
        putMainLogic(masterKeyName, serialize(currentMasterKeyList), true)
    }

    private fun putMainLogic(key: String, serializedValue: ByteArray, isMasterKey: Boolean) {
        var currentKey = key
        var currentValueSize = serializedValue.size
        var currentIteration = 0

        while(currentValueSize > 0) {
            val relevantPart = ByteArray(secureStorageMaxSize)
            System.arraycopy(
                serializedValue, currentIteration * (secureStorageMaxSize - metedataSize) ,
                relevantPart, 0,
                if(currentValueSize >= (secureStorageMaxSize - metedataSize)) (secureStorageMaxSize - metedataSize) else currentValueSize
            )

            if(currentValueSize > secureStorageMaxSize - metedataSize) {
                relevantPart[relevantPart.size - 1] = 1 // Symbols that this is not the last part
                relevantPart[relevantPart.size - 2] = 98 // Read 98 bytes from this part
            } else {
                relevantPart[relevantPart.size - 1] = 0 // Symbols that this is the last part
                relevantPart[relevantPart.size - 2] = currentValueSize.toByte()
            }

            currentValueSize -= (secureStorageMaxSize - metedataSize)

            if(isMasterKey){
                secureStorage.write(
                    key = serialize(currentKey),
                    value = relevantPart
                )
                currentKey = increaseLex(currentKey)
            } else {
                secureStorage.write(
                    key = serialize(key + currentIteration.toString()),
                    value = relevantPart
                )
            }
            currentIteration++
        }
    }

    override fun put(key: String, value: ByteArray): Boolean {
        putMainLogic(key, value, isMasterKey = false)
        addToMasterKey(key)
        return true
    }

    private fun getMainLogic(key: String, isMasterKey: Boolean): ByteArray {
        val listOfParts = mutableListOf<ByteArray>()
        var currentIteration = 0

        var currentKey: ByteArray
        if(isMasterKey) {
            currentKey = serialize(key)
        } else {
            currentKey = serialize(key + currentIteration.toString())
        }

        var currentPart = secureStorage.read(currentKey)

        while(currentPart != null) {
            listOfParts.add(currentPart)
            currentIteration++

            if(isMasterKey) {
                currentKey = serialize(increaseLex(deserializeString(currentKey)))
            } else {
                currentKey = serialize(key + currentIteration.toString())
            }

            currentPart = secureStorage.read(currentKey)
        }
        if(currentIteration == 0) {
            // TODO bug
        }

        val serializedValue = ByteArray(listOfParts.size - 1 * (secureStorageMaxSize - metedataSize) + listOfParts.last()[98])
        for(i in listOfParts.indices) {
            System.arraycopy(
                listOfParts[i], 0,
                serializedValue, i * secureStorageMaxSize,
                listOfParts[i][secureStorageMaxSize - 2].toInt()
            )
        }
        return serializedValue
    }

    override fun get(key: String): ByteArray? {
        if (!exists(key)) {
            return null
        }
        return getMainLogic(key, false)
    }

    override fun exists(key: String): Boolean {
        return secureStorage.read(serialize(key + "0")) != null
    }

    override fun getAllMap(): Map<String, ByteArray?> {
        val keyListByteArray = getMainLogic(masterKeyName, true)
        val keyList = deserializeStringList(keyListByteArray)
        return keyList.associateWith { get(it) }
    }
}