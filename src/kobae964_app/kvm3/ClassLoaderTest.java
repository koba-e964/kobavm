package kobae964_app.kvm3;

import static org.junit.Assert.*;
import static kobae964_app.kvm3.CPU.*;
import static kobae964_app.kvm3.DataType.*;
import kobae964_app.kvm3.inline.KString;

import org.junit.Test;

public class ClassLoaderTest {

	/**
	 * Test of {@link ClassLoader#registerClassWithBinary(String, BinaryClassData, Mem)}.
	*/
	@Test
	public void testRegisterClassWithBinary0(){
		final String name="TestClass";
		Mem mem=new Mem(0x10000);
		ClassLoader.setMem(mem);
		//registers TestClass with ClassLoader
		BinaryClassData dat=registerClassSub0(name);
		ClassData cdat=ClassLoader.getClassData(name);
		assertTrue(cdat.hasVMCode("test.I"));
		assertEquals(cdat.getCodePlace()+dat.methodOffsets[0],cdat.getVMCodeAddress("test.I"));
	}
	/**
	 * Test of {@link ClassLoader#registerClassWithBinary(String, BinaryClassData, Mem)}.
	 * This code runs CPU and checks if the method returns correct value.
	 * public int test(int val){return 2;}
	 */
	@Test
	public void testRegisterClassWithBinary1() {
		final String name="TestClass";
		final String methodName="test.I";
		Mem mem=new Mem(0x10000);
		CPU cpu=new CPU(mem);
		ClassLoader.setMem(mem);
		//registers TestClass with ClassLoader
		registerClassSub0(name);
		byte[] code={
			LDCim,0,0,0,//LDC.im 0
			LDV,1,0,0,//var1:"test.I"
			LDV,0,0,0,//var0:"TestClass"
			0xe,1,0,0,//CALL.st st0 st1 st2, ar0=1

			-1,0,0,0,//exit
		};
		int mainCode=ClassLoader.loadCode(code);
		System.out.println("mainCode was loaded at "+mainCode+"~"+(mainCode+code.length));

		//setting variables
		{
			cpu.vtable.store(0, new VarEntry(OBJECT,new KString(name).getAddress()));
			cpu.vtable.store(1, new VarEntry(OBJECT,new KString(methodName).getAddress()));
		}

		cpu.pc=mainCode;
		cpu.run();
		//stack:[int:2]
		assertEquals(1,cpu.stack.size());
		assertEquals(2L,cpu.stack.getAt(0).value);
	}
	BinaryClassData registerClassSub0(String name){
		int id;
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
		id=ClassLoader.registerClassWithBinary(name, dat);
		System.out.println("id("+name+")="+id);
		return dat;
	}
}
