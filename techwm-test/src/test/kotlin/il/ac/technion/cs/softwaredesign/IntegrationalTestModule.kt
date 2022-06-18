package il.ac.technion.cs.softwaredesign

import Fakes.LoanServiceFake
import Fakes.SecureStorageFake
import LibraryProdModule
import PersistentMap
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.impl.*
import il.ac.technion.cs.softwaredesign.loan.LoanService
import il.ac.technion.cs.softwaredesign.storage.SecureStorage
import impl.DefaultPersistentMap



class IntegrationalTestModule: KotlinModule() {

    override fun configure() {
        bind<LoanManager>().to<DefaultLoanManager>()
//        bind<ObtainedLoan>().to<DefaultObtainedLoan>()
        bind<LoanService>().to<LoanServiceFake>()


        // TODO maybe use this in the future instead of binding SecureStorageFake
//        install(LibraryTestModule())
        bind<UserManager>().to<DefaultUserManager>()
        bind<BookManager>().to<DefaultBookManager>()
        bind<TokenStore>().to<DefaultTokenStore>()
        bind<PersistentMap>().to<DefaultPersistentMap>()
        bind<SecureStorage>().to<SecureStorageFake>()

    }
}