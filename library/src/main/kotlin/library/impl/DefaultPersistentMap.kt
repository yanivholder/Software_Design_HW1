package library.impl

import javax.inject.Inject

import library.PersistentMap
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import kotlinx.serialization.decodeFromString
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.system.exitProcess

class DefaultPersistentMap @Inject constructor(private val secureStorage: SecureStorage) : PersistentMap {

    private val secureStorageMaxSize = 100
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
        val currentMasterKeyList = deserializeStringList(currentMasterKeyListEncoded) as MutableList<String>
        currentMasterKeyList.add(key)
        // TODO fix
        putMainLogic(masterKeyName, ObjectSerializer.serialize(currentMasterKeyList), true)
//        putMainLogic(masterKeyName, Json.encodeToString(currentMasterKeyList).toByteArray(), true)
    }

    private fun putMainLogic(key: String, value: Any, isMasterKey: Boolean) {
        var currentKey = key
        val encodedWrapValueSize = encodedWrapValue.size
        var currentIteration = 0

        while(currentIteration * (secureStorageMaxSize - 1) < encodedWrapValueSize) {
            val relevantPart = ByteArray(secureStorageMaxSize)
            System.arraycopy(
                encodedWrapValue, currentIteration * secureStorageMaxSize,
                relevantPart, 0,
                if(encodedWrapValueSize >= secureStorageMaxSize) secureStorageMaxSize - 1 else encodedWrapValueSize
            )

            if((currentIteration + 1) * (secureStorageMaxSize - 1) < encodedWrapValueSize) {
                relevantPart[relevantPart.size - 1] = 1 // Symbols that this is not the last part
            } else {
                relevantPart[relevantPart.size - 1] = 0 // Symbols that this is the last part
            }

            if(isMasterKey){
                secureStorage.write(
                    key = (currentKey).toByteArray(),
                    value = relevantPart
                )
                currentKey = increaseLex(currentKey)
            } else {
                secureStorage.write(
                    key = (key + currentIteration.toString()).toByteArray(),
                    value = relevantPart
                )
            }
            currentIteration++
        }
    }

    override fun put(key: String, value: T?): Boolean {
//        val wrapValue = DataWrapper(value)
        // TODO remove comment
        val serializedValue = ObjectSerializer.serialize(value)
//        val encodedWrapValue = Json.encodeToString(wrapValue).toByteArray()
        putMainLogic(key, serializedValue, isMasterKey = false)
        addToMasterKey(key)
        // TODO erase that
        return true
    }

    private fun getMainLogic(key: String, isMasterKey: Boolean): ByteArray {
        val listOfParts = mutableListOf<ByteArray>()
        var currentIteration = 0

        var currentKey: ByteArray
        if(isMasterKey) {
            currentKey = key.toByteArray()
        } else {
            currentKey = (key + currentIteration.toString()).toByteArray()
        }

        var currentPart = secureStorage.read(currentKey)

        while(currentPart != null) {
            listOfParts.add(currentPart)
            currentIteration++

            if(isMasterKey) {
                currentKey = increaseLex(currentKey.toString()).toByteArray()
            } else {
                currentKey = (key + currentIteration.toString()).toByteArray()
            }

            currentPart = secureStorage.read(currentKey)
        }
        if(currentIteration == 0) {
            // TODO bug
            exitProcess(1)
        }

        val serializedValue = ByteArray(listOfParts.size * (secureStorageMaxSize - 1))
        for(i in listOfParts.indices) {
            System.arraycopy(
                listOfParts[i], 0,
                serializedValue, i * secureStorageMaxSize,
                secureStorageMaxSize - 1
            )
        }
        return serializedValue
    }

    override fun get(key: String): T? {
        if(!exists(key)) {
            return null
        }
        val value = getMainLogic(key, false)
        // TODO erase comment
        return ObjectSerializer.deserialize(value) as T?
//        return Json.decodeFromString<DataWrapper<T>>(value.toString()).data
    }

    override fun exists(key: String): Boolean {
        return secureStorage.read((key + "0").toByteArray()) != null
    }

    override fun getAllMap(): Map<String, T?> {
        val keyListByteArray = getMainLogic(masterKeyName, true)
        val keyList: MutableList<String> = Json.decodeFromString(keyListByteArray.toString())
        return keyList.associateWith { get(it) }
    }
}