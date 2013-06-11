package kobae964_app.kvm3;

import static org.junit.Assert.*;
import kobae964_app.kvm3.inline.KString;
import kobae964_app.kvm3.inline.Pair;

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
	@Test
	public void test2() {
		Mem mem=new Mem(0x10000);
		long pair=new KString("Pair").getAddress();
		byte[] code={
				0,(byte)pair,(byte)(pair>>8),0,//LDC.im pair.addr
				16,0,0,0,//GETFIELD st0,ar0=0

				-1,0,0,0,//EXIT
		};
		mem.load(code, 0x0000);
		CPU cpu=new CPU(mem);
		cpu.run();
		assertEquals(1,cpu.stack.size());
		String actual=KString.getContent(cpu.stack.getAt(0).value);
		assertEquals("fst",actual);
	}

}
