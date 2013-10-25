package kobae964_app.kvm3.inline;

import static org.junit.Assert.*;

import kobae964_app.kvm3.ClassLoader;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.Heap;
import kobae964_app.kvm3.KVMObject;
import kobae964_app.kvm3.ObjManager;
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
	@Test
	public void testKArrayCtor(){
		Object[] array=new Object[]{"test0",145,22.0,true};
		KArray inst=new KArray(array);
		KVMObject obj=Heap.retrieve(inst.getAddress());
		byte[] correct=new byte[4+4*12];
		correct[0]=4;//length=4;
		correct[4]=(byte) DataType.OBJECT.ordinal();
		System.arraycopy(obj.getContent(), 8, correct, 8, 8);//copies the address of "test0"
		correct[16]=(byte) DataType.INT.ordinal();
		correct[20]=(byte) 145;
		correct[28]=(byte)DataType.REAL.ordinal();
		correct[38]=54;
		correct[39]=64;
		correct[40]=(byte)DataType.BOOL.ordinal();
		correct[44]=1;
		assertArrayEquals(correct,obj.getContent());
		long addr=obj.getInt(8,8);
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
		Object[] array=new Object[]{"test0",145,22.0,true,0x8012345678abcdeL};
		KArray inst=new KArray(array);
		long addr=inst.getAddress();
		KArray inst2=KArray.createInstanceFromAddress(addr);
		assertEquals(addr,inst2.getAddress());
	}
	@Test
	public void testArrayGC(){
		KString obj=new KString("eliminated");//obj.refcount=1
		VarEntry vobj=VarEntry.valueOf(obj);
		vobj.refer();
		KArray ary=new KArray(2);
		VarEntry.valueOf(ary).refer();
		ary.call("set", VarEntry.valueOf(0),vobj);
		ObjManager.dumpAll();
		vobj.unrefer();//it is probable that a reference to object is only held by an array.
		VarEntry result=ary.call("get",VarEntry.valueOf(0));
		assertEquals(vobj,result);
		assertEquals("eliminated",KString.getContent(result.value));//if gc'ed, this will cause an error.
		ObjManager.dumpAll();
	}

}
