package kobae964_app.kvm3;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static kobae964_app.kvm3.DataType.*;


import kobae964_app.kvm3.inline.KString;
import kobae964_app.kvm3.inline.Pair;

import org.junit.Test;

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
				0,100,0,0,//LDC.im 100
				1,3,0,0,//LDV var[3](=9)
				7,0,0,0,//ADD st0,st1
				0,1,23,0,//LDC.im 23*256+1
				8,0,0,0,//SUB st0,st1

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
				1,0,0,0,//LDV 0 ("snd")
				1,1,0,0,//LDV 0 (pair)
				2,0,0,0,//GETFIELD st0,st1

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
				0,(byte)pair,(byte)(pair>>8),0,//LDC.im pair.addr
				16,0,0,0,//LDC.cp st0,ar0=0

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
				0,13,0,0,//LDC.im 13
				0,(byte)0x9e,(byte)0xc7,(byte)0xde,//LDC.im 0xffdec79e-(1L<<32)
				3,4,0,0,//STV st0 ar0=4, in CPU.CPU(Mem), it is ensured that variable table has space for 10 variable

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
				1,2,0,0,//LDV 2->st2(magic)
				1,1,0,0,//LDV 1->st1(snd)
				1,0,0,0,//LDV 0->st0(pair)
				4,0,0,0,//SETFIELD st0 st1 st2

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0);
		CPU cpu=new CPU(mem);
		
		//variables
		cpu.vtable.store(0,new VarEntry(OBJECT.ordinal(), pair));
		cpu.vtable.store(1,new VarEntry(OBJECT.ordinal(), snd));
		cpu.vtable.store(2,new VarEntry(INT.ordinal(), magic));
		
		//execute
		cpu.run();
		
		//stack:[]
		assertEquals(0,cpu.stack.size());
		assertEquals(magic,Pair.createInstanceFromAddress(pair).getField("snd").value);
	}
	/**
	 * Test of
	 * LDC.im.cur
	 */
	@Test
	public void testLDCimcur(){
		Mem mem=new Mem(0x10000);
		byte[] code={
				32,1,0,0,//LDC.im.cur 1

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0);
		CPU cpu=new CPU(mem);
		
		//execute
		cpu.run();
		
		//stack:[(s)]
		assertEquals(1,cpu.stack.size());
		System.out.println(cpu.stack);
	}
}
