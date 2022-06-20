package il.ac.technion.cs.softwaredesign

import Fakes.LoanServiceFake
import PersistentMap
import com.google.inject.Provides
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.Fakes.PersistentMapFake
import il.ac.technion.cs.softwaredesign.impl.DefaultTokenStore
import il.ac.technion.cs.softwaredesign.impl.DefaultBookManager
import il.ac.technion.cs.softwaredesign.impl.DefaultUserManager
import il.ac.technion.cs.softwaredesign.loan.LoanService
import io.mockk.mockk

class AppTestModule: KotlinModule() {

    override fun configure() {
        bind<PersistentMap>().to<PersistentMapFake>()
//        bind<UserManager>().to<DefaultUserManager>()
//        bind<BookManager>().to<DefaultBookManager>()
        bind<TokenStore>().to<DefaultTokenStore>()


        bind<LoanService>().to<LoanServiceFake>()
    }

    @Provides
    fun provideBookManager(): BookManager {
        return mockk(relaxed = true)
    }

    @Provides
    fun provideUserManager(): UserManager {
        return mockk(relaxed = true)
    }

//    @Provides
//    fun provideLoanService(): LoanService {
//        return mockk(relaxed = true)
//    }

//    @Provides
//    fun providePersistentMap(): PersistentMap {
//        return PersistentMapFake()
//    }
}