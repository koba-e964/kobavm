package kobae964_app.kvm3.inline;

import static org.junit.Assert.*;
import kobae964_app.kvm3.CPU;
import kobae964_app.kvm3.CallStack;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.Mem;
import kobae964_app.kvm3.VarEntry;

import org.junit.Test;

public class IOTest {
	@Test
	public void testInstantiate(){
		try{
			IO.createInstanceFromAddress(2);
		}catch(IllegalArgumentException ex){
			//ok
			return;
		}
		fail();
	}
	@SuppressWarnings("deprecation")
	@Test
	public void testCall() {
		Mem mem=new Mem(0x10000);
		CPU cpu=new CPU(mem);
		/*
		 * open("-",0);
		 */
		mem.load(new byte[]{
			CPU.LDCim,0,0,0,
			CPU.LDV,1,0,0,
			CPU.LDV,2,0,0,
			CPU.LDV,3,0,0,
			CPU.CALLst,2,0,0,
			CPU.STV,4,0,0,//store [4]<- fd
			CPU.LDCim,'@',0,0,//LDC.im '@'
			CPU.LDV,4,0,0,
			CPU.LDV,5,0,0,
			CPU.LDV,3,0,0,
			CPU.CALLst,2,0,0,
			-1,0,0,0,
		},0);
		long addr0=new KString("-").getAddress();
		long met=new KString("open").getAddress();
		long cn=new KString("IO").getAddress();
		long pc=new KString("putchar").getAddress();
		cpu.getVTable().store(1, new VarEntry(DataType.OBJECT,addr0));
		cpu.getVTable().store(2, new VarEntry(DataType.OBJECT,met));
		cpu.getVTable().store(3, new VarEntry(DataType.OBJECT,cn));
		cpu.getVTable().store(5, new VarEntry(DataType.OBJECT,pc));
		cpu.run();
		CallStack st=cpu.getCallStack();
		assertEquals(1,st.size());
		assertEquals(DataType.INT.ordinal(),st.getAt(0).type);
		assertEquals(0,st.getAt(0).value);
	}

}
