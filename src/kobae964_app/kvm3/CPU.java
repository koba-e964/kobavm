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
	public void run()
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
		case LDV://LDV(3)
		{
			int ar0=code>>>8;//unsigned
			VarEntry cont=vtable.load(ar0);
			stack.push(cont);
			break;
		}
		case GETFIELD://GETFIELD(4)
		{
			VarEntry st0=stack.pop();
			VarEntry st1=stack.pop();
			String name=KString.getContent(st1.value);
			st0.checkDataType(OBJECT);
			VarEntry res=ClassLoader.getField(st0.value,name);
			stack.push(res);
			break;
		}
		case STV://STV(5)
		{
			VarEntry st0=stack.pop();
			int ar0=code>>>8;//unsigned
			vtable.store(ar0,st0);
			break;
		}
		case SETFIELD://SETFIELD(6)
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
		case DUP://DUP(7)
		{
			int ar0=code>>>8;
			VarEntry ve=stack.getAt(ar0);
			stack.push(ve.clone());
			break;
		}
		case SWAP://SWAP(8)
		{
			int ar0=(code>>>8)&0xfff;//unsigned
			int ar1=(code>>>20)&0xfff;
			VarEntry ve1=stack.getAt(ar0);
			VarEntry ve2=stack.getAt(ar1);
			stack.setAt(ar0,ve2);
			stack.setAt(ar1,ve1);
			break;
		}
		case ADD://ADD(9)
		{
			if(code>>>8!=0){//real
				double val1=stack.popReal();
				double val2=stack.popReal();
				stack.pushReal(val1+val2);
				break;
			}
			//integer
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1+val2);
			break;
		}
		case SUB://SUB(10)
		{
			if(code>>>8!=0){//real
				double val1=stack.popReal();
				double val2=stack.popReal();
				stack.pushReal(val1-val2);
				break;
			}
			//integer
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1-val2);
			break;
		}
		case MUL://MUL(11)
		{
			if(code>>>8!=0){//real
				double val1=stack.popReal();
				double val2=stack.popReal();
				stack.pushReal(val1*val2);
				break;
			}
			//integer
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1*val2);
			break;
		}
		case DIV://DIV(12)
		{
			if(code>>>8!=0){//real
				double val1=stack.popReal();
				double val2=stack.popReal();
				stack.pushReal(val1/val2);//remainder is not pushed
				break;
			}
			//integer
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1/val2);
			stack.pushInt(val1%val2);
			break;
		}
		case CALL://CALL ar0(13)
		{
			int ar0=(code>>8)&0xfff;
			ar0|=(ar0&0x800)==0?0:~0xfff;//signed
			int ar1=code>>>20;//size of variable table
			int dest=pc+ar0;
			call(dest, this.classID,ar1);//classID is not changed
			break;
		}
		case CALLst://CALL.st st0 st1(14)
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
				int[] tmp=dat.getVMCodeAddressSizeofVT(methodName);
				int addr=tmp[0];
				int vtSize=tmp[1];
				call(addr,ClassLoader.getClassID(className),vtSize);
			}else{
				VarEntry res=dat.call(Heap.NULL_ADDR, methodName,args);
				if(res!=null){
					stack.push(res);
				}
			}
			break;
		}
		case JMP://JMP(16)
		{
			int ar0=code>>8;//signed
			pc+=ar0;
			break;
		}
		case RET://ret(17)
		{
			boolean hasRetVal=(code>>>8)!=0;
			ret(hasRetVal);
			break;
		}
		case CMPlt://CMP.lt(18)
		{
			long st0=stack.popInt();
			long st1=stack.popInt();
			stack.pushBool(st0<st1);
			break;
		}
		case CMPeq://CMP.eq(19)
		{
			VarEntry st0=stack.pop();
			VarEntry st1=stack.pop();
			stack.pushBool(st0.type==st1.type&&st0.value==st1.value);
			break;
		}
		case ANDi://AND.i(20)
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1&val2);
			break;
		}
		case ORi://OR.i(21)
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1|val2);
			break;
		}
		case XORi://XOR.i(22)
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1^val2);
			break;
		}
		case NOTi://NOT.i(23)
		{
			long val1=stack.popInt();
			stack.pushInt(~val1);
			break;
		}
		case ANDb://AND.b(24)
		{
			boolean val1=stack.popBool();
			boolean val2=stack.popBool();
			stack.pushBool(val1&val2);
			break;
		}
		case ORb://OR.b(25)
		{
			boolean val1=stack.popBool();
			boolean val2=stack.popBool();
			stack.pushBool(val1|val2);
			break;
		}
		case XORb://XOR.b(26)
		{
			boolean val1=stack.popBool();
			boolean val2=stack.popBool();
			stack.pushBool(val1^val2);
			break;
		}
		case NOTb://NOT.b(27)
		{
			boolean val1=stack.popBool();
			stack.pushBool(!val1);
			break;
		}
		case JC://JC(31)
		{
			int ar0=code>>8;
			boolean st0=stack.popBool();
			if(st0){
				pc+=ar0;
			}
			break;
		}
		case 0x3f://EXIT
			return -1;
		default:
			throw new RuntimeException("Invalid Code");
		}
		return 0;
	}
	public void call(int addr, int newClassID,int vtSize)
	{
		stack.pushInt(pc);
		vtable.allocate(vtSize);
		this.classID=newClassID;
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
	/**
	 * for test only
	 * @return
	 */
	@Deprecated
	public VariableTable getVTable(){
		return vtable;
	}
	/**
	 * for test only
	 */
	@Deprecated
	public CallStack getCallStack(){
		return stack;
	}
	public static final int LDCim=0,
			LDCcp=1,
			LDCcpcur=2,
			LDV=3,
			GETFIELD=4,
			STV=5,
			SETFIELD=6,
			DUP=7,
			SWAP=8,
			ADD=9,
			SUB=10,
			MUL=11,
			DIV=12,
			CALL=13,
			CALLst=14,
			CALLin=15,
			JMP=16,
			RET=17,
			CMPlt=18,
			CMPeq=19,
			ANDi=20,
			ORi=21,
			XORi=22,
			NOTi=23,
			ANDb=24,
			ORb=25,
			XORb=26,
			NOTb=27,
			JC=31;
}
