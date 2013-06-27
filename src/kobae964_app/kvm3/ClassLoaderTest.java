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
		boolean ok=false;
		try{
			cdat.getVMCodeAddress("randomNameThatIsNotDefined");
		}catch(RuntimeException ex){
			ok=true;//ok
		}finally{
			assertTrue(ok);
		}
		assertTrue(cdat.fieldTable.isEmpty());
		System.out.println(cdat.methodTable);
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
				RET,1,0,0,//RET :return 2
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
	/**
	 * Test of {@link ClassLoader#registerClassWithBinary(String, BinaryClassData, Mem)}.
	 * This code runs CPU and checks if the method returns correct value.
	 * public int test(int val){return 2;}
	 */
	@Test
	public void testRegisterClassWithBinary2() {
		final String name="TestClass2";
		final String methodName="cpTest1.";
		Mem mem=new Mem(0x10000);
		CPU cpu=new CPU(mem);
		ClassLoader.setMem(mem);
		//registers TestClass with ClassLoader
		registerClassSub1(name);
		byte[] code={
			LDV,1,0,0,//var1:"cpTest1."
			LDV,0,0,0,//var0:"TestClass2"
			CALLst,0,0,0,//CALL.st st0 st1 st2, ar0=0

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
		//stack:[cpTest1()=sqrt(2)+sqrt(5)]
		assertEquals(1, cpu.stack.size());
		assertEquals(Double.doubleToLongBits(sqrt2+sqrt5),cpu.stack.getAt(0).value);
	}
	static final double sqrt2=1.41421356;
	static final double sqrt5=2.2360679;
	static final double pi=3.141592654;
	BinaryClassData registerClassSub1(String name){
		int id;
		BinaryClassData dat=new BinaryClassData();
		dat.code=new byte[]{
				//cpTest1() computes sqrt(2)+sqrt(5)
				LDCcpcur,0,0,0,//LDC.cp.cur 0 (sqrt(2))
				LDCcpcur,1,0,0,//LDC.cp.cur 1 (sqrt(5))
				ADD,1,0,0,//ADD st0 st1(real)
				RET,1,0,0,//RET nonvoid:return
				//cpTest2() computes pi+sqrt(2) (offset=16)
				LDCcpcur,0,0,0,//LDC.cp.cur 0 (sqrt(2))
				LDCcpcur,2,0,0,//LDC.cp.cur 2 (pi)
				ADD,1,0,0,//ADD st0 st1(real)
				RET,1,0,0,//RET(nonvoid)
		};
		dat.constPool=new Object[]{
			sqrt2,
			sqrt5,
			pi,
			"sqrt2",
			"sqrt5",
			"pi",
		};
		dat.fieldNames=new String[0];
		dat.fieldOffsets=new int[0];
		dat.fieldSigns=new String[0];
		dat.methodNames=new String[]{"cpTest1","cpTest2"};
		dat.methodOffsets=new int[]{0,16};
		dat.methodSigns=new String[]{"",""};//cpTest1(),cpTest2()
		id=ClassLoader.registerClassWithBinary(name, dat);
		System.out.println("id("+name+")="+id);
		return dat;
	}
}
