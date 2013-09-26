package kobae964_app.kvm3.inline;

import static org.junit.Assert.*;

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
		for(int i=0;i<50;i++){
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
		VarEntry[] argV=new VarEntry[args.length];
		for(int i=0,s=args.length;i<s;i++){
			VarEntry ve;
			Object a=args[i];
			if(a instanceof Integer){
				ve=new VarEntry(DataType.INT,(Integer)a);
			}else if(a instanceof Boolean){
				ve=new VarEntry(DataType.BOOL,((Boolean)a)?1:0);
			}else if(a instanceof String){
				ve=new VarEntry(DataType.OBJECT,new KString((String)a).getAddress());
			}else{
				throw new IllegalArgumentException("arg:"+a.toString());
			}
			argV[i]=ve;
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
		byte[] code=new byte[4*args.length+preCode.length];
		//link
		for(int i=0,s=args.length;i<s;i++){
			vt.store(i+2,argV[i]);
			code[4*i+0]=LDV;
			code[4*i+1]=(byte)(i+2);
			code[4*i+2]=(byte)((i+2)>>>8);
			code[4*i+3]=0;
		}
		System.arraycopy(preCode, 0, code, 4*args.length, preCode.length);
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
