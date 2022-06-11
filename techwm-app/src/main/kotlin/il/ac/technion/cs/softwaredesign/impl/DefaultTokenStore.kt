package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.TokenStore
import PersistentMap
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class DefaultTokenStore @Inject constructor(private val persistentMap: PersistentMap) : TokenStore{

    // return true if and only if invalidation occurred successfully
    override fun invalidate(oldToken: String): CompletableFuture<Unit> {
        return persistentMap.put(oldToken, TokenInfo(false).serialize())
    }

    // return true if and only if insert occurred successfully
    override fun insert(newToken: String): CompletableFuture<Unit>{
        return persistentMap.put(newToken, TokenInfo(true).serialize())
    }

    override fun isValid(token: String): CompletableFuture<Boolean>{
        return persistentMap.get(token).thenCompose { serializedToken ->
            CompletableFuture.completedFuture((serializedToken != null) && TokenInfo(serializedToken).getValidity())
        }
//        val ret = persistentMap.get(token).get()
//        return (ret != null) && TokenInfo(ret).getValidity()
    }

}