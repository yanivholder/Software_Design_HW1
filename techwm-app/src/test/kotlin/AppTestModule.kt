package il.ac.technion.cs.softwaredesign

import PersistentMap
import com.google.inject.Provides
import dev.misfitlabs.kotlinguice4.KotlinModule
import il.ac.technion.cs.softwaredesign.Fakes.PersistentMapFake
import il.ac.technion.cs.softwaredesign.impl.DefaultTokenStore
import io.mockk.mockk

class AppTestModule: KotlinModule() {

    override fun configure() {
        bind<PersistentMap>().to<PersistentMapFake>()
        bind<TokenStore>().to<DefaultTokenStore>()
    }

    @Provides
    fun provideBookManager(): BookManager {
        return mockk(relaxed = true)
    }

    @Provides
    fun provideUserManager(): UserManager {
        return mockk(relaxed = true)
    }
}