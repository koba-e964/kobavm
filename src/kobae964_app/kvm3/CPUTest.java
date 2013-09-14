package kobae964_app.kvm3;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static kobae964_app.kvm3.DataType.*;

import java.util.Random;


import kobae964_app.kvm3.inline.Init;
import kobae964_app.kvm3.inline.KString;
import kobae964_app.kvm3.inline.Pair;

import org.junit.Test;
import static kobae964_app.kvm3.CPU.*;

public class CPUTest {

	/**
	 * Test of
	 * LDC.im(0),
	 * LDV(1),
	 * ADD(7),
	 * SUB(8)
	 */
	@Test
	public void test0() {
		Mem mem=new Mem(0x10000);
		byte[] code={
				LDCim,100,0,0,//LDC.im 100
				LDV,3,0,0,//LDV var[3](=9)
				ADD,0,0,0,//ADD st0,st1
				LDCim,1,23,0,//LDC.im 23*256+1
				SUB,0,0,0,//SUB st0,st1

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0x0000);
		CPU cpu=new CPU(mem);
		cpu.vtable.store(3, new VarEntry(DataType.INT.ordinal(),9));//var[3]=9
		cpu.run();
		long expected=23*256+1-(100+9);
		long actual=cpu.stack.getAt(0).value;
		assertEquals(expected, actual);
		assertEquals(1,cpu.stack.size());
	}
	/**
	 * Test of {@link Pair#getField(String)} and GETFIELD instruction.
	 */
	@Test
	public void test1() {
		Mem mem=new Mem(0x10000);
		long snd=new KString("snd").getAddress();
		long pair=new Pair(11351,234523).getAddress();
		byte[] code={
				LDV,0,0,0,//LDV 0 ("snd")
				LDV,1,0,0,//LDV 0 (pair)
				GETFIELD,0,0,0,//GETFIELD st0,st1

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0x0000);
		CPU cpu=new CPU(mem);
		//sets variables
		cpu.vtable.store(0, new VarEntry(OBJECT.ordinal(),snd));
		cpu.vtable.store(1, new VarEntry(OBJECT.ordinal(),pair));
		
		cpu.run();
		assertEquals(1,cpu.stack.size());
		long expected=234523;
		long actual=cpu.stack.getAt(0).value;
		assertEquals(expected, actual);
	}
	/**
	 * Test of LDC.cp(load from constant pool)
	 */
	@Test
	public void test2() {
		Mem mem=new Mem(0x10000);
		long pair=new KString("Pair").getAddress();
		byte[] code={
				LDCim,(byte)pair,(byte)(pair>>8),0,//LDC.im pair.addr
				LDCcp,0,0,0,//LDC.cp st0,ar0=0

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0x0000);
		CPU cpu=new CPU(mem);
		cpu.run();
		assertEquals(1,cpu.stack.size());
		String actual=KString.getContent(cpu.stack.getAt(0).value);
		assertEquals("fst",actual);
	}
	/**
	 * Test of
	 * STV(3) instruction
	 */
	@Test
	public void testSTV0(){
		Mem mem=new Mem(0x10000);
		byte[] code={
				LDCim,13,0,0,//LDC.im 13
				LDCim,(byte)0x9e,(byte)0xc7,(byte)0xde,//LDC.im 0xffdec79e-(1L<<32)
				STV,4,0,0,//STV st0 ar0=4, in CPU.CPU(Mem), it is ensured that variable table has space for 10 variable

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0);
		CPU cpu=new CPU(mem);
		assertThat(cpu.vtable.size(),greaterThanOrEqualTo(10));
		//assertTrue(cpu.vtable.size()>=10);
		cpu.run();
		//stack: 13
		//var: [4](long)(0xffdec79e-(1L<<32))
		assertEquals(1,cpu.stack.size());
		assertEquals(13,cpu.stack.getAt(0).value);
		assertEquals((long)(int)0xffdec79eL,cpu.vtable.load(4).value);
	}
	/**
	 * Test of
	 * SETFIELD(4) instruction
	 */
	@Test
	public void testSETFIELD(){
		Mem mem=new Mem(0x10000);
		//pair.snd=0x1010b7eb
		long pair=new Pair(23,-1234567890).getAddress();
		long snd=new KString("snd").getAddress();
		final long magic=0x1010b7eb;
		byte[] code={
				LDV,2,0,0,//LDV 2->st2(magic)
				LDV,1,0,0,//LDV 1->st1(snd)
				LDV,0,0,0,//LDV 0->st0(pair)
				SETFIELD,0,0,0,//SETFIELD st0 st1 st2

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0);
		CPU cpu=new CPU(mem);
		
		//variables
		cpu.vtable.store(0,new VarEntry(OBJECT, pair));
		cpu.vtable.store(1,new VarEntry(OBJECT, snd));
		cpu.vtable.store(2,new VarEntry(INT, magic));
		
		//execute
		cpu.run();
		
		//stack:[]
		assertEquals(0,cpu.stack.size());
		assertEquals(magic,Pair.createInstanceFromAddress(pair).getField("snd").value);
	}
	/**
	 * Test of
	 * LDC.cp.cur
	 * Init.getConstant(int) always returns (int)12345.
	 * @see Init
	 */
	@Test
	public void testLDCcpcur(){
		Mem mem=new Mem(0x10000);
		byte[] code={
				LDCcpcur,1,0,0,//LDC.cp.cur 1

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0);
		CPU cpu=new CPU(mem);
		
		//execute
		cpu.run();
		
		//stack:[(s)]
		assertEquals(1,cpu.stack.size());
		assertEquals(new VarEntry(INT, 12345),cpu.stack.getAt(0));
	}
	/**
	 * Test of
	 * CALL(13) instruction
	 * vtSize
	 */
	@Test
	public void testCall(){
		Mem mem=new Mem(0x10000);
		CPU cpu=new CPU(mem);
		byte[] code={
			CALL,4,16,0,//call ar0=4, ar1(vtSize)=1
			-1,0,0,0,
			LDCim,114,0,0,//LDC.im 114
			STV,1,0,0,//vtable[1]=114;//out of range
			LDV,1,0,0,
			RET,1,0,0,//RET void
		};
		mem.load(code, 0);
		try{
			cpu.run();
		}catch(RuntimeException ex){
			//ok
			return;
		}
		fail();
	}
	/**
	 * Test of
	 * JC(31) instruction
	 */
	@Test
	public void testJC(){
		Random rand=new Random();
		for(int i=0;i<10;i++){
			long v0=rand.nextLong();
			long v1=rand.nextLong();
			Mem mem=new Mem(0x10000);
			CPU cpu=new CPU(mem);
			byte[] code={
				LDV,1,0,0,//LDV 1
				LDV,0,0,0,//LDV 0
				CMPlt,0,0,0,//CMP.lt
				JC,9,0,0,
				LDV,0,0,0,//LDV 0
				-1,0,0,0,//EXIT
				127,//padding
				//label
				LDV,1,0,0,//LDV 1
				-1,0,0,0,//EXIT
			};
			cpu.vtable.store(0,new VarEntry(INT,v0));
			cpu.vtable.store(1,new VarEntry(INT,v1));
			mem.load(code,0);
			cpu.run();
			//stack:[max(v0,v1)]
			assertEquals(1,cpu.stack.size());
			assertEquals(Math.max(v0, v1),cpu.stack.getAt(0).value);
		}
	}
	/**
	 * Test of
	 * ADD/SUB/MUL/DIV for real numbers.
	 */
	@Test
	public void testRealOperations(){
		final double r0=1.41421356;
		final double r1=2.2360679;
		final double r2=1.7320508;
		Mem mem=new Mem(0x10000);
		byte[] code={
				LDV,0,0,0,//LDV 0(r0)
				LDV,1,0,0,//LDV 1(r1)
				ADD,1,0,0,//ADD st0 st1(real)
				LDV,1,0,0,//LDV 1(r1)
				LDV,2,0,0,//LDV 2(r2)
				SUB,1,0,0,//SUB st0 st1(real)
				LDV,1,0,0,//LDV 1(r1)
				LDV,2,0,0,//LDV 2(r2)
				MUL,1,0,0,//MUL st0 st1(real)
				LDV,3,0,0,//LDV 3(0.0)
				LDV,1,0,0,//LDV 1(r1)
				DIV,1,0,0,//DIV st0 st1(real)

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0);
		CPU cpu=new CPU(mem);
		
		//variables
		cpu.vtable.store(0,VarEntry.valueOf(r0));
		cpu.vtable.store(1,VarEntry.valueOf(r1));
		cpu.vtable.store(2,VarEntry.valueOf(r2));
		cpu.vtable.store(3,VarEntry.valueOf(0.0));
	
		//execute
		cpu.run();
		
		//stack:bottom<-[r1+r0,r2-r1,r2*r1,r1/0.0]->top
		assertEquals(4,cpu.stack.size());
		assertEquals(Double.doubleToLongBits(r1+r0), cpu.stack.getAt(3).value);
		assertEquals(Double.doubleToLongBits(r2-r1), cpu.stack.getAt(2).value);
		assertEquals(Double.doubleToLongBits(r2*r1), cpu.stack.getAt(1).value);
		assertEquals(Double.doubleToLongBits(Double.POSITIVE_INFINITY), cpu.stack.getAt(0).value);//r1/0.0=NaN
	}
}
