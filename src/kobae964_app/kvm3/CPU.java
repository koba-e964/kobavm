package kobae964_app.kvm3;

import kobae964_app.kvm3.inline.Init;
import kobae964_app.kvm3.inline.KString;
import static kobae964_app.kvm3.DataType.*;

public class CPU {
	Mem mem;
	int pc;
	CallStack stack;
	VariableTable vtable;
	ClassLoader loader;
	/**
	 * The ID of class which this CPU runs.
	 */
	int classID;
	public CPU(Mem mem)
	{
		this.mem=mem;
		this.pc=0;
		this.stack=new CallStack();
		this.vtable=new VariableTable();
		this.loader=new ClassLoader();
		this.classID=ClassLoader.getClassID(Init.CLASS_NAME);
		/*
		 * for tests
		 */
		final int N=10;
		this.vtable.allocate(N);
	}
	void run()
	{
		while(decode()>=0){}
	}
	int decode(){
		int code=mem.getDword(pc);
		pc+=4;
		//TODO build machine code
		try{
			return decode_sub(code);
		}catch(RuntimeException ex){
			throw new RuntimeException(String.format("Exception at pc=0x%x, code=%08x",pc-4,code),ex);
		}
	}
	int decode_sub(int code)
	{
		switch(code%64)
		{
		case LDCim://LDC.im
		{
			int ar0=code>>8;//signed int
			stack.pushInt(ar0);
			break;
		}
		case LDCcp://LDC.cp st0 ar0
		{
			VarEntry st0=stack.pop();
			String clzName=KString.getContent(st0.value);
			int ar0=code>>>8;//unsigned
			VarEntry result=ClassLoader.getConstant(ClassLoader.getClassID(clzName), ar0);
			stack.push(result);
			break;
		}
		case LDCcpcur://LDC.cp.cur ar0
		{
			int ar0=code>>>8;//unsigned
			VarEntry result=ClassLoader.getConstant(classID, ar0);
			stack.push(result);
			break;
		}
		case 3://LDV
		{
			int ar0=code>>>8;//unsigned
			VarEntry cont=vtable.load(ar0);
			stack.push(cont);
			break;
		}
		case 4://GETFIELD
		{
			VarEntry st0=stack.pop();
			VarEntry st1=stack.pop();
			String name=KString.getContent(st1.value);
			st0.checkDataType(OBJECT);
			VarEntry res=ClassLoader.getField(st0.value,name);
			stack.push(res);
			break;
		}
		case 5://STV
		{
			VarEntry st0=stack.pop();
			int ar0=code>>>8;//unsigned
			vtable.store(ar0,st0);
			break;
		}
		case 6://SETFIELD
		{
			VarEntry st0=stack.pop();//object whose member is assigned to
			VarEntry st1=stack.pop();//name of member
			VarEntry st2=stack.pop();//value that is assigned to member
			st1.checkDataType(OBJECT);
			String name=KString.getContent(st1.value);
			st0.checkDataType(OBJECT);
			ClassLoader.setField(st0.value, name, st2);
			break;
		}
		case 7://DUP
		{
			int ar0=code>>>8;
			VarEntry ve=stack.getAt(ar0);
			stack.push(ve.clone());
			break;
		}
		case 8://SWAP
		{
			int ar0=(code>>>8)&0xfff;//unsigned
			int ar1=(code>>>20)&0xfff;
			VarEntry ve1=stack.getAt(ar0);
			VarEntry ve2=stack.getAt(ar1);
			stack.setAt(ar0,ve2);
			stack.setAt(ar1,ve1);
			break;
		}
		case 9://ADD
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1+val2);
			break;
		}
		case 10://SUB
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1-val2);
			break;
		}
		case 11://MUL
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1*val2);
			break;
		}
		case 12://DIV
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1/val2);
			stack.pushInt(val1%val2);
			break;
		}
		case 13://CALL ar0
		{
			int ar0=code>>8;//signed
			int dest=pc+ar0;
			call(dest);
			break;
		}
		case 14://CALL.st st0 st1
		{
			int ar0=code>>>8;//number of arguments
			String className=stack.popString();
			String methodName=stack.popString();
			VarEntry[] args=new VarEntry[ar0];
			for(int i=0;i<ar0;i++){
				args[i]=stack.pop();
			}
			ClassData dat=ClassLoader.getClassData(className);
			if(dat.hasVMCode(methodName))
			{
				int addr=dat.getVMCodeAddress(methodName);
				call(addr);
			}else{
				dat.call(Heap.NULL_ADDR, methodName,args);
			}
			break;
		}
		case 16://JMP
		{
			int ar0=code>>8;//signed
			pc+=ar0;
			break;
		}
		case RET://ret
		{
			boolean hasRetVal=(code>>>8)!=0;
			ret(hasRetVal);
			break;
		}
		case 0x3f://EXIT
			return -1;
		default:
			throw new RuntimeException("Invalid Code");
		}
		return 0;
	}
	public void call(int addr)
	{
		stack.pushInt(pc);
		vtable.allocate(100);//actual value should be recalled from classData
		pc=addr;
	}
	public void ret(boolean hasRetVal)
	{
		VarEntry retVal=null;
		if(hasRetVal){
			retVal=stack.pop();
		}
		long a=stack.popInt();
		if(hasRetVal){
			stack.push(retVal);
		}
		vtable.deallocate();
		pc=(int)a;
	}
	public static final int LDCim=0,
			LDCcp=1,
			LDCcpcur=2,
			LDV=3,
			RET=17;
}
