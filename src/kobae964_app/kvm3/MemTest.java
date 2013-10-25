package kobae964_app.kvm3;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

public class MemTest {

	@Test
	public void testAllocate() {
		Mem mem=new Mem(0x1000);
		int obj=mem.allocate(0x101);
		int obj2=mem.allocate(1);
		assertEquals(0x101,mem.memView().get(obj).intValue());
		assertEquals(0x104,obj2);
	}

	@Test
	public void testFree() {
		Mem mem=new Mem(0x1000);
		int obj=mem.allocate(0x100);
		mem.free(obj);
		assertEquals(0, mem.memView().size());
	}

	@Test
	public void testGetLength() {
		Mem mem=new Mem(0x1000);
		int len=0x101;
		int obj=mem.allocate(len);
		assertEquals(len,mem.getLength(obj));
	}

}
