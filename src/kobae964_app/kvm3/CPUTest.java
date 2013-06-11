package kobae964_app.kvm3;

import static org.junit.Assert.*;

import org.junit.Test;

public class CPUTest {

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
	@Test
	public void test1() {
		Mem mem=new Mem(0x10000);
		byte[] code={
				0,1,2,0,//LDC.im 513 ("snd")
				0,23,1,0,//LDC.im 279 (Pair[23,529])
				2,0,0,0,//GETFIELD st0,st1

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0x0000);
		CPU cpu=new CPU(mem);
		cpu.run();
		long expected=279*279;
		long actual=cpu.stack.getAt(0).value;
		assertEquals(expected, actual);
		assertEquals(1,cpu.stack.size());
	}

}
