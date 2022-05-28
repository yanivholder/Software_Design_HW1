package il.ac.technion.cs.softwaredesign

import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.impl.DefaultBookManager
import il.ac.technion.cs.softwaredesign.impl.DefaultTokenStore
import il.ac.technion.cs.softwaredesign.impl.DefaultUserManager
import LibraryProdModule


class SifriTaubModule: KotlinModule() {

    override fun configure() {
        install(LibraryProdModule())
        bind<UserManager>().to<DefaultUserManager>()
        bind<BookManager>().to<DefaultBookManager>()
        bind<TokenStore>().to<DefaultTokenStore>()
    }
}