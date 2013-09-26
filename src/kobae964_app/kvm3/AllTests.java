package kobae964_app.kvm3;

import kobae964_app.kvm3.inline.IOTest;
import kobae964_app.kvm3.inline.KStringTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CallStackTest.class, ClassLoaderTest.class, CPUTest.class,
		VarEntryTest.class,KStringTest.class ,IOTest.class})
public class AllTests {

}
