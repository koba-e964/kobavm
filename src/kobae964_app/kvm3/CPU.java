package kobae964_app.kvm3;

import static kobae964_app.kvm3.Flags.*;
import kobae964_app.kvm3.inline.KString;

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
		this.classID=-1;//invalid
		/*
		 * for tests
		 */
		final int N=10;
		this.vtable.allocate(N);
		for(int i=0;i<N;i++)
		{
			this.vtable.store(i, new VarEntry(DataType.INT.ordinal(), i*i));
		}
		
	}
	void run()
	{
		while(decode()>=0){}
	}
	int decode()
	{
		int code=mem.getDword(pc);
		pc+=4;
		//TODO build machine code
		switch(code%32)
		{
		case 0://LDC.im
		{
			int ar0=code>>8;//signed int
			System.out.println("LDC.im "+ar0);
			stack.pushInt(ar0);
			break;
		}
		case 1://LDV
		{
			int ar0=code>>>8;//unsigned
			System.out.println("LDV "+ar0);
			VarEntry cont=vtable.load(ar0);
			stack.push(cont);
			break;
		}
		case 2://GETFIELD
		{
			VarEntry st0=stack.pop();
			VarEntry st1=stack.pop();
			int type=st1.type&TYPE_MASK;
			System.out.println("type="+type);
			String name=KString.getContent(st1.value);
			System.out.printf("GETFIELD st0=%d st1=%s\n",st0.value,name);
			//checking if st0.type-=OBJECT.ordinal() is necessary but now omitted for convenience
			VarEntry res=ClassLoader.getField(st0.value,name);
			stack.push(res);
			break;
		}
		case 3://STV
		{
			VarEntry st0=stack.pop();
			int ar0=code>>>8;//unsigned
			System.out.println("STV st0="+st0.value+" ar0="+ar0);
			vtable.store(ar0,st0);
			break;
		}
		case 4://SETFIELD
		{
			VarEntry st0=stack.pop();
			VarEntry st1=stack.pop();
			VarEntry st2=stack.pop();
			int type=st1.type&TYPE_MASK;
			String name=KString.getContent(st1.value);
			System.out.printf("SETFIELD st0=%d st1=%s st2=%d\n",st0.value,name,st2.value);
			if(type!=DataType.OBJECT.ordinal())
			{
				//throw new IllegalStateException();
				//omitted for convenience
			}
			ClassLoader.setField(st0.value, name, st2);
			break;
		}
		case 5://DUP
		{
			int ar0=code>>>8;
			VarEntry ve=stack.getAt(ar0);
			stack.push(ve.clone());
			break;
		}
		case 6://SWAP
		{
			int ar0=(code>>>8)&0xfff;//unsigned
			int ar1=(code>>>20)&0xfff;
			VarEntry ve1=stack.getAt(ar0);
			VarEntry ve2=stack.getAt(ar1);
			stack.setAt(ar0,ve2);
			stack.setAt(ar1,ve1);
			break;
		}
		case 7://ADD
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1+val2);
			break;
		}
		case 8://SUB
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1-val2);
			break;
		}
		case 9://MUL
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1*val2);
			break;
		}
		case 10://DIV
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1/val2);
			stack.pushInt(val1%val2);
			break;
		}
		case 11://CALL ar0
		{
			int ar0=code>>8;//signed
			int dest=pc+ar0;
			call(dest);
			break;
		}
		case 13://JMP
		{
			int ar0=code>>8;//signed
			pc+=ar0;
			break;
		}
		case 14://ret
		{
			ret();
			break;
		}
		case 16://LDC.cp st0 ar0
		{
			VarEntry st0=stack.pop();
			String clzName=KString.getContent(st0.value);
			int ar0=code>>>8;//unsigned
			VarEntry result=ClassLoader.getConstant(ClassLoader.getClassID(clzName), ar0);
			stack.push(result);
			break;
		}
		case 0x1f://EXIT
			return -1;
		default:
			throw new RuntimeException("Invalid Code");
		}
		System.out.println("callstack="+stack);
		return 0;
	}
	public void call(int addr)
	{
		stack.pushInt(pc);
		vtable.allocate(100);//actual value should be recalled from classData
		pc=addr;
	}
	public void ret()
	{
		long a=stack.popInt();
		vtable.deallocate();
		pc=(int)a;
	}
}
