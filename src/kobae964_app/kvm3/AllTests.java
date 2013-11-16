package kobae964_app.kvm3;

import kobae964_app.kvm3.binclz.LoaderTest;
import kobae964_app.kvm3.binclz.MainRunnerTest;
import kobae964_app.kvm3.inline.IOTest;
import kobae964_app.kvm3.inline.KArrayTest;
import kobae964_app.kvm3.inline.KStringTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CallStackTest.class, ClassLoaderTest.class, CPUTest.class,ObjManagerTest.class,
		VarEntryTest.class,KArrayTest.class, KStringTest.class ,IOTest.class,
		LoaderTest.class, MainRunnerTest.class})
public class AllTests {

}
