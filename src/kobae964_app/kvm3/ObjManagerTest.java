package kobae964_app.kvm3;

import static org.junit.Assert.*;

import kobae964_app.kvm3.inline.KString;
import kobae964_app.kvm3.inline.KStringTest;
import static kobae964_app.kvm3.CPU.*;

import org.junit.Test;

public class ObjManagerTest {

	@Test
	public void testDumpObjects1(){
		new KStringTest().testCallConcat();
		ObjManager.dumpAll();
	}
	@Test
	public void testDumpObjects2(){
		new KStringTest().testCallCharAt();
		ObjManager.dumpAll();
	}
	@Test
	@SuppressWarnings("deprecation")
	public void testGCwork(){
		Mem mem=new Mem(0x10000);
		CPU cpu=new CPU(mem);
		KString obj1=new KString("eliminated");
		KString obj2=new KString("overwritten");
		VariableTable vt=new VariableTable();
		vt.store(0, VarEntry.valueOf(obj1));
		vt.store(1, VarEntry.valueOf(obj2));
		byte[] code={
			LDV,0,0,0,//LDV 0(obj1) [o1]
			LDV,1,0,0,//LDV 1(obj2) [o2,o1]
			DUP,0,0,0,//DUP 0 [o2,o2,o1]
			STV,0,0,0,//STV 0,[o2,o1], intentionally overwrites obj1 in VariableTable
			SWAP,0,16,0//SWAP 0,1, [o1,o2], o1 and o2 are once popped from the stack, so o1 may be regarded as a garbage.
			-1,0,0,0,
		};
		mem.load(code,0);
		cpu.run();
		CallStack cs=cpu.getCallStack();
		assertEquals(2, cs.size());
	}

}
