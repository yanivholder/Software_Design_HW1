package il.ac.technion.cs.softwaredesign;

import com.google.inject.Guice;
import dev.misfitlabs.kotlinguice4.getInstance
import il.ac.technion.cs.softwaredesign.impl.DefaultLoanManager
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


public class LoanManagerTest {
    private val injector = Guice.createInjector(AppTestModule())
    private var manager = injector.getInstance<DefaultLoanManager>()

    @BeforeEach
    fun init(){
        manager = injector.getInstance()
    }

    @Test
    fun `need to implemnt tests`() {
        assert(false)
    }
}
