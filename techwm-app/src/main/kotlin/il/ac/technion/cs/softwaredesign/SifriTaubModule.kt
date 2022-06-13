package il.ac.technion.cs.softwaredesign

import dev.misfitlabs.kotlinguice4.KotlinModule
import LibraryProdModule
import il.ac.technion.cs.softwaredesign.impl.*
import il.ac.technion.cs.softwaredesign.loan.LoanServiceModule
import il.ac.technion.cs.softwaredesign.storage.SecureStorageModule
import impl.DefaultPersistentMap


class SifriTaubModule: KotlinModule() {

    override fun configure() {
        install(LoanServiceModule())
        bind<LoanManager>().to<DefaultLoanManager>()
        bind<ObtainedLoan>().to<DefaultObtainedLoan>()

        install(LibraryProdModule())
        bind<UserManager>().to<DefaultUserManager>()
        bind<BookManager>().to<DefaultBookManager>()
        bind<TokenStore>().to<DefaultTokenStore>()
    }
}