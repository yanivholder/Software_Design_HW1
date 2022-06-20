package il.ac.technion.cs.softwaredesign

import Fakes.LoanServiceFake
import Fakes.SecureStorageFake
import dev.misfitlabs.kotlinguice4.KotlinModule
import LibraryProdModule
import il.ac.technion.cs.softwaredesign.impl.*
import il.ac.technion.cs.softwaredesign.loan.LoanService
import il.ac.technion.cs.softwaredesign.loan.LoanServiceModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import il.ac.technion.cs.softwaredesign.storage.SecureStorageModule
import impl.DefaultPersistentMap


class SifriTaubModule: KotlinModule() {

    override fun configure() {
        install(LoanServiceModule())
//        bind<LoanService>().to<LoanServiceFake>()
        bind<LoanManager>().to<DefaultLoanManager>()

        install(LibraryProdModule())
        bind<UserManager>().to<DefaultUserManager>()
        bind<BookManager>().to<DefaultBookManager>()
        bind<TokenStore>().to<DefaultTokenStore>()
    }
}