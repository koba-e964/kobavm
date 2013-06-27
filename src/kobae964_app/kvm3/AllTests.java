package kobae964_app.kvm3;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CallStackTest.class, ClassLoaderTest.class, CPUTest.class,
		VarEntryTest.class })
public class AllTests {

}
