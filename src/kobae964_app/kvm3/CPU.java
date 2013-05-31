package kobae964_app.kvm3;

import static kobae964_app.kvm3.DataType.*;
import static kobae964_app.kvm3.Flags.*;

public class CPU {
	Mem mem;
	int pc;
	CallStack stack;
	Heap heap;
	VariableTable vtable;
	ClassLoader loader;
	public CPU(Mem mem)
	{
		this.mem=mem;
		this.pc=0;
		this.stack=new CallStack();
		this.heap=new Heap();
		this.vtable=new VariableTable();
		this.loader=new ClassLoader();
		/*
		 * for tests
		 */
		final int N=10;
		this.vtable.allocate(N);
		for(int i=0;i<N;i++)
		{
			this.vtable.store(i, new VarEntry(DataType.INT.ordinal(), i*i));
		}
		new KString("bytecode_test",heap);//creates an instance of KString.
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
			String name=new KString(heap.retrieve(st1.value)).getContent();
			System.out.printf("GETFIELD st0=%d st1=%s\n",st0.value,name);
			//TODO 
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
			String name=new KString(heap.retrieve(st1.value)).getContent();
			System.out.printf("SETFIELD st0=%d st1=%s st2=%d\n",st0.value,name,st2.value);
			//TODO 
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
		case 0x1f://EXIT
			return -1;
		}
		System.out.println("callstack="+stack);
		return 0;
	}
}
