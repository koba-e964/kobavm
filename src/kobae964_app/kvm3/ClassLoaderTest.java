package kobae964_app.kvm3;

import static org.junit.Assert.*;
import static kobae964_app.kvm3.CPU.*;

import org.junit.Test;

public class ClassLoaderTest {

	/**
	 * Test of {@link ClassLoader#registerClassWithBinary(String, BinaryClassData, Mem)}.
	 * public int test(){return 2;}
	 */
	@Test
	public void testRegisterClassWithBinary0() {
		Mem mem=new Mem(0x10000);
		CPU cpu=new CPU(mem);
		BinaryClassData dat=new BinaryClassData();
		dat.code=new byte[]{
			LDCim,2,0,0,//LDC.im 2
			RET,0,0,0,//RET :return 2
		};
		dat.constPool=new Object[0];
		dat.fieldNames=new String[0];
		dat.fieldOffsets=new int[0];
		dat.fieldSigns=new String[0];
		dat.methodNames=new String[]{"test"};
		dat.methodOffsets=new int[]{0};
		dat.methodSigns=new String[]{"I"};//func(int)
		final String name="TestClass";
		int id=ClassLoader.registerClassWithBinary(name, dat, mem);
		System.out.println("id("+name+")="+id);
		byte[] code={
			LDV,1,0,0,//var1:"test.I"
			LDV,0,0,0,//var0:"TestClass"
			0xe,0,0,0,//CALL.st st0,st1

			-1,0,0,0,//exit
		};
		int mainCode=ClassLoader.loadCode(code);
		cpu.pc=mainCode;
		cpu.run();
		//stack:[int:2]
		assertEquals(1,cpu.stack.size());
		assertEquals(2L,cpu.stack.getAt(0).value);
	}

}
