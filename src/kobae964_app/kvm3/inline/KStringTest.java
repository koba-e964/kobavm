package kobae964_app.kvm3.inline;

import static org.junit.Assert.*;

import kobae964_app.kvm3.CPU;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.Mem;
import kobae964_app.kvm3.VarEntry;
import kobae964_app.kvm3.VariableTable;
import static kobae964_app.kvm3.CPU.*;

import org.junit.Test;

public class KStringTest {

	@Test
	public void testCallLength() {
		String method="length";
		String str="Kaehler";
		VarEntry methodV=new VarEntry(DataType.OBJECT,new KString(method).getAddress());
		VarEntry strV=new VarEntry(DataType.OBJECT,new KString(str).getAddress());
		Mem mem=new Mem(0x10000);
		CPU cpu=new CPU(mem);
		byte[] code={
			LDV,0,0,0,//[0]="length"
			LDV,1,0,0,//[1]="Kaehler"
			CALLin,0,0,0,//call
		};
		mem.load(code,0);
		VariableTable vt=cpu.getVTable();
		vt.store(0, methodV);
		vt.store(1, strV);
		cpu.run();
		assertEquals(new VarEntry(DataType.INT,str.length()),cpu.getCallStack().getAt(0));
	}

}
