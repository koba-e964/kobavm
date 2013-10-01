package kobae964_app.kvm3.inline;

import static org.junit.Assert.*;

import java.util.Arrays;

import kobae964_app.kvm3.ClassLoader;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.Heap;
import kobae964_app.kvm3.KVMObject;
import kobae964_app.kvm3.VarEntry;

import org.junit.Test;

public class KArrayTest {

	@Test
	public void testGetAddress() {
		KArray inst=new KArray(4);
		long addr=inst.getAddress();
		KVMObject obj=Heap.retrieve(addr);
		assertEquals(ClassLoader.getClassID(KArray.CLASS_NAME),obj.getClassID());
	}

	@Test
	public void testGetField() {
		Object[] array=new Object[]{"test0",145,22.0,true};
		KArray inst=new KArray(array);
		assertEquals(VarEntry.valueOf(array.length),inst.getField("length"));
	}

	@Test
	public void testSetField() {
		try{
			Object[] array=new Object[]{"test0",145,22.0,true};
			KArray inst=new KArray(array);
			inst.setField("dummyField", VarEntry.valueOf(0xdeadc0del));
		}catch(UnsupportedOperationException ex){
			//ok
			return;
		}
		fail();
	}

	@Test
	public void testCall() {
		Object[] array=new Object[]{"test0",145,22.0,true};
		KArray inst=new KArray(array);
		VarEntry ret = inst.call("get", VarEntry.valueOf(0));
		ret.checkDataType(DataType.OBJECT);
		assertEquals("test0",KString.getContent(ret.value));
		assertNull(inst.call("set", VarEntry.valueOf(2),VarEntry.valueOf(0x81925050af342516L)));
		assertEquals(VarEntry.valueOf(0x81925050af342516L),inst.call("get", VarEntry.valueOf(2)));
	}
	private static long toInt(byte[] array,int start,int len){
		long v=0;
		for(int i=0;i<len&&i<8;i++){
			v|=(array[start+i]&0xffL)<<(8*i);
		}
		return v;
	}

	@Test
	public void testKArrayCtor(){
		Object[] array=new Object[]{"test0",145,22.0,true};
		KArray inst=new KArray(array);
		KVMObject obj=Heap.retrieve(inst.getAddress());
		byte[] correct=new byte[4+4*12];
		correct[0]=4;//length=4;
		correct[4]=(byte) DataType.OBJECT.ordinal();
		correct[8]=obj.data[8];//not checked
		System.arraycopy(obj.data, 8, correct, 8, 8);//copies the address of "test0"
		correct[16]=(byte) DataType.INT.ordinal();
		correct[20]=(byte) 145;
		correct[28]=(byte)DataType.REAL.ordinal();
		correct[38]=54;
		correct[39]=64;
		correct[40]=(byte)DataType.BOOL.ordinal();
		correct[44]=1;
		assertArrayEquals(correct,obj.data);
		long addr=toInt(obj.data,8,8);
		String cont=KString.getContent(addr);
		assertEquals("test0",cont);
	}
	@Test
	public void testKArrayCtor2(){
		int len=10;
		KArray inst=new KArray(len);
		for(int i=0;i<len;i++){
			VarEntry ve=inst.call("get", VarEntry.valueOf(i));
			assertEquals(VarEntry.valueOf(null),ve);
		}
	}
	@Test
	public void testCreateInstanceFromAddress() {
		Object[] array=new Object[]{"test0",145,22.0,true};
		KArray inst=new KArray(array);
		long addr=inst.getAddress();
		KArray inst2=KArray.createInstanceFromAddress(addr);
		assertEquals(addr,inst2.getAddress());
	}

}
