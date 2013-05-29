package kobae964_app.kvm3;

public class CPU {
	Mem mem;
	int pc;
	public CPU(Mem mem)
	{
		this.mem=mem;
		this.pc=0;
	}
	void decode()
	{
		int code=mem.getDword(pc);
		pc+=4;
		//TODO build machine code
		switch(code%32)
		{
		case 0://LDC
			break;
		case 1://LDV
			break;
		case 2://GETFIELD
			break;
		}
	}
}
