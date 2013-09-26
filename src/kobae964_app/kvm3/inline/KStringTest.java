package kobae964_app.kvm3.inline;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kobae964_app.kvm3.CPU;
import kobae964_app.kvm3.CallStack;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.Mem;
import kobae964_app.kvm3.VarEntry;
import kobae964_app.kvm3.VariableTable;
import static kobae964_app.kvm3.CPU.*;

import org.junit.Test;

public class KStringTest {

	@Test
	public void testCallLength() {
		String str="Kaehler";
		VarEntry result=callInstance(new KString(str), "length");
		assertEquals(new VarEntry(DataType.INT,str.length()),result);
	}
	@Test
	public void testCallCharAt(){
		Random rand=new Random();
		String str="which is infinite\nwhich is yes";
		for(int i=0;i<1000;i++){
			int index=rand.nextInt(str.length());
			VarEntry result=callInstance(new KString(str),"charAt",index);
			assertEquals(new VarEntry(DataType.INT,str.charAt(index)),result);
		}
	}
	@SuppressWarnings("deprecation")
	VarEntry callInstance(KString instance,String method,Object... args){
		if(args.length>=64){
			throw new RuntimeException("too many arguments("+args.length+")");
		}
		VarEntry methodV=new VarEntry(DataType.OBJECT,new KString(method).getAddress());
		VarEntry strV=new VarEntry(DataType.OBJECT,instance.getAddress());
		List<VarEntry> argList=new ArrayList<VarEntry>();
		for(Object a:args){
			VarEntry ve;
			if(a instanceof Integer){
				ve=new VarEntry(DataType.INT,(Integer)a);
			}else if(a instanceof Boolean){
				ve=new VarEntry(DataType.BOOL,((Boolean)a)?1:0);
			}else if(a instanceof String){
				ve=new VarEntry(DataType.OBJECT,new KString((String)a).getAddress());
			}else{
				throw new IllegalArgumentException("arg:"+a.toString());
			}
			argList.add(ve);
		}
		Mem mem=new Mem(0x10000);
		CPU cpu=new CPU(mem);
		VariableTable vt=cpu.getVTable();
		vt.store(0, methodV);
		vt.store(1, strV);
		byte[] preCode={
			LDV,0,0,0,//[0]="length"
			LDV,1,0,0,//[1]="Kaehler"
			CALLin,(byte)args.length,0,0,//call
			-1,0,0,0,//EXIT
		};
		byte[] code=new byte[4*argList.size()+preCode.length];
		//link
		for(int i=0,s=argList.size();i<s;i++){
			vt.store(i+2,argList.get(i));
			byte[] snip=new byte[]{LDV,(byte)(i+2),(byte)((i+2)>>>8),0};//[i+2]
			System.arraycopy(snip, 0, code, 4*i, 4);
		}
		System.arraycopy(preCode, 0, code, 4*argList.size(), preCode.length);
		mem.load(code,0);
		cpu.run();
		CallStack cs=cpu.getCallStack();
		if(cs.size()==0){
			return null;
		}
		//cs.size()>=1
		assertEquals(1,cs.size());
		return cs.getAt(0);
		
	}
}
