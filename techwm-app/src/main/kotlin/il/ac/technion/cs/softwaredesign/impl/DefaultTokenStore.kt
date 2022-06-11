package il.ac.technion.cs.softwaredesign.impl

import il.ac.technion.cs.softwaredesign.TokenStore
import PersistentMap
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class DefaultTokenStore @Inject constructor(private val persistentMap: PersistentMap) : TokenStore{

    // return true if and only if invalidation occurred successfully
    override fun invalidate(oldToken: String, ownerId: String): CompletableFuture<Unit> {
        return persistentMap.put(oldToken, TokenInfo(false, ownerId).serialize())
    }

    // return true if and only if insert occurred successfully
    override fun insert(newToken: String, ownerId: String): CompletableFuture<Unit> {
        return persistentMap.put(newToken, TokenInfo(true, ownerId).serialize())
    }

    override fun isValid(token: String): CompletableFuture<Boolean> {
        return persistentMap.get(token).thenCompose { serializedToken ->
            CompletableFuture.completedFuture((serializedToken != null) && TokenInfo(serializedToken).getValidity())
        }
    }

    override fun getTokenOwner(token: String): CompletableFuture<String> {
        return persistentMap.get(token).thenApply { serializedToken ->
            TokenInfo(serializedToken!!).getOwner()
        }
    }


}