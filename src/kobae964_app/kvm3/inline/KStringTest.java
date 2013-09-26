package kobae964_app.kvm3.inline;

import static org.junit.Assert.*;

import java.util.Random;

import kobae964_app.kvm3.CPU;
import kobae964_app.kvm3.CallStack;
import kobae964_app.kvm3.DataType;
import kobae964_app.kvm3.Heap;
import kobae964_app.kvm3.Mem;
import kobae964_app.kvm3.VarEntry;
import kobae964_app.kvm3.VariableTable;
import static kobae964_app.kvm3.CPU.*;

import org.junit.Ignore;
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
	@Test
	@Ignore
	public void testCallCreate(){
		KString dummy=KString.createInstanceFromAddress(Heap.NULL_ADDR);//static-call
		String str="qwertyuiop";
		VarEntry result=callInstance(dummy,"create",str.toCharArray());
		assertEquals(str,KString.getContent(result.value));
	}
	@Test
	public void testCallSubstring() {
		String str="Kaehler-manifold";
		Random rand=new Random();
		int a=rand.nextInt(str.length());
		int b=rand.nextInt(str.length());
		if(a>b){
			int t=a;
			a=b;
			b=t;
		}
		VarEntry result=callInstance(new KString(str),"substring",a,b-a);
		result.checkDataType(DataType.OBJECT);
		assertEquals(str.substring(a, b),KString.getContent(result.value));
	}
	@Test
	public void testCallConcat() {
		String str="Kaehler-manifold";
		String another="w15g2";
		VarEntry result=callInstance(new KString(str),"concat",another);
		result.checkDataType(DataType.OBJECT);
		assertEquals(str+another,KString.getContent(result.value));
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
			argV[i]=VarEntry.valueOf(args[i]);
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
			int pl=s-1-i;
			vt.store(i+2,argV[i]);
			code[4*pl+0]=LDV;
			code[4*pl+1]=(byte)(i+2);
			code[4*pl+2]=(byte)((i+2)>>>8);
			code[4*pl+3]=0;
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
