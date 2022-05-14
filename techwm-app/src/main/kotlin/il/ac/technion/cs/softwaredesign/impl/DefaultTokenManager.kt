package il.ac.technion.cs.softwaredesign

import library.PersistentMap
import library.PersistentMapFactroy
import javax.inject.Inject

class TokenManager @Inject constructor(private val pmf: PersistentMapFactroy<Boolean>){


    private val persistentMap: PersistentMap<Boolean> = pmf.createPersistentMap()

    //TokenManager is shared between clients and must be a stateful singleton
//    fun getInstance(): TokenManager{
//        // TODO - make sure all request get the same instance
//    }

    // return true if and only if invalidation accrued successfully
    fun invalidate(oldToken: String): Boolean{
        return persistentMap.put(oldToken, false) == true
    }

    // return true if and only if invalidation accrued successfully
    fun insert(newToken: String): Boolean{
        return persistentMap.put(newToken, true) == true
    }

    fun isValid(token: String): Boolean{
        return persistentMap.get(token)
    }

}