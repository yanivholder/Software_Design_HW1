package il.ac.technion.cs.softwaredesign

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

interface ByteSerializable : Serializable {
    fun serialize(): ByteArray {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(this)
        return baos.toByteArray()
    }

    fun deserialize(bytes: ByteArray) : Any? {
        if(bytes.contentEquals(ByteArray(0))) {
            return null
        }
        val bais = ByteArrayInputStream(bytes)
        val ois = ObjectInputStream(bais)
        return ois.readObject()
    }
}