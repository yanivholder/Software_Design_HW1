package il.ac.technion.cs.softwaredesign

import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.impl.BookInfo
import il.ac.technion.cs.softwaredesign.impl.DefaultBookManager
import il.ac.technion.cs.softwaredesign.impl.DefaultTokenManager
import il.ac.technion.cs.softwaredesign.impl.UserInfo
import library.PersistentMap
import library.impl.DefaultPersistentMap

class UserManagerModule: KotlinModule() {
    override fun configure() {
        bind<PersistentMap<UserInfo>>().to<DefaultPersistentMap<UserInfo>>()
        bind<TokenManager>().to<DefaultTokenManager>()
    }
}