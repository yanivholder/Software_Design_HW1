package il.ac.technion.cs.softwaredesign

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Exception relating to permission errors when accessing the TechWM API.
 */
class PermissionException: RuntimeException()