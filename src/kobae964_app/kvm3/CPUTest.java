package kobae964_app.kvm3;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


import kobae964_app.kvm3.inline.KString;
import kobae964_app.kvm3.inline.Pair;

import org.hamcrest.Matchers;
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
				0,(byte)snd,(byte)(snd>>8),0,//LDC.im snd.addr ("snd")
				0,(byte)pair,(byte)(pair>>8),0,//LDC.im pair.addr
				2,0,0,0,//GETFIELD st0,st1

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0x0000);
		CPU cpu=new CPU(mem);
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
	 * Test of STV(3) instruction
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
}
