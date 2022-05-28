package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.TokenStore
import PersistentMap
import javax.inject.Inject

class DefaultTokenStore @Inject constructor(private val persistentMap: PersistentMap) : TokenStore{


    // return true if and only if invalidation occurred successfully
    override fun invalidate(oldToken: String): Boolean{
        return persistentMap.put(oldToken, TokenInfo(false).serialize())
    }

    // return true if and only if insert occurred successfully
    override fun insert(newToken: String): Boolean{
        return persistentMap.put(newToken, TokenInfo(true).serialize())
    }

    override fun isValid(token: String): Boolean{
        val ret = persistentMap.get(token)
        return (ret != null) && TokenInfo(ret).getValidity()
    }

}