package il.ac.technion.cs.softwaredesign

interface TokenStore {

    // return true if and only if invalidation occurred successfully
    fun invalidate(oldToken: String): Boolean;

    // return true if and only if insert occurred successfully
    fun insert(newToken: String): Boolean;

    fun isValid(token: String): Boolean;
}