package koba_app.kvm;

public class CPU
{
	public static int REG_SIZE=16;
	public static int CARRY=1,
			OVER=2,
			SIG=4;//signum
	public static String[] reg_names=
	{"ra5","rb5","rc5","rd5", "rp5","rq5","rr5","rs5",/*stack ptr*/
	 "e85","e95","ea5","eb5", "ec5","ed5","ee5","ef5"};
	public CPU(Mem mem){
		for(int i=0;i<REG_SIZE;i++)
		{
			reg[i]=0;
		}
		pc=0;
		this.mem=mem;
	}
	private void set_dword(int addr,int value)
	{
		if(addr>=0)
			mem.set_dword(addr,value);
		else if(addr>=-REG_SIZE)//move to reg
			reg[addr+REG_SIZE]=value;
	}
	private int get_dword(int addr)
	{
		if(addr>=0)
			return mem.get_dword(addr);
		else if(addr>= -REG_SIZE)
			return reg[addr+REG_SIZE];
		throw new RuntimeException("CPU::get_dword: addr="+String.format("%08x",addr));
	}
	private byte prog_byte()
	{
		byte v=mem.get_byte(pc);
		pc++;
		return v;
	}
	private int prog_dword()
	{
		int v=mem.get_dword(pc);
		pc+=4;
		return v;
	}
	private int resolveAddr(int addrmod,int regID)
	{
		switch(addrmod)
		{
		case 0:
			return regID-REG_SIZE;
		case 1:
		{
			int dif=prog_byte();
			return reg[regID]+dif;
		}
		case 2:
		{
			int dif=prog_dword();
			return reg[regID]+dif;
		}
		case 3://value,not address
		{
			int b=prog_byte();
			return b;
		}
		case 4:
			return prog_dword();
		case 5:
			return prog_dword();
		case 6:
		{
			int dif=prog_dword();
			return pc+dif;// pc := imm32( dif ) ‚ÌÝ‚Á‚½ˆÊ’u+4
		}
		default:
			throw new RuntimeException("Invalid addrmode");
		}
	}
	private int[] addrModeHelper(int argc)
	{
		switch(argc)
		{
		case 2:
		{
			int addrmod=prog_byte()&0xff;
			int destmod=addrmod&0xf,srcmod=addrmod>>4;
			boolean useRegField=false;
			switch(destmod)
			{
			case 0:
			case 1:
			case 2:
				useRegField=true;
			case 5:
			case 6:
				break;
			default:
				throw new RuntimeException("Bad Addressing Mode dest:"+destmod);
			}
			if(srcmod >= 7)
				throw new RuntimeException("Bad Addressing Mode src :"+srcmod);
			if(srcmod>=0 && srcmod <= 2)
				useRegField=true;
			int regField=0;
			if(useRegField)
			{
				regField=prog_byte()&0xff;
			}
			int dest=resolveAddr(destmod,regField&0xf);
			boolean bSrcValue=
				(srcmod==3 ||srcmod==4);
			int src =resolveAddr(srcmod,regField>>4);
			return new int[]{dest,src,bSrcValue?1:0};
		}
		case 1:
		{
			int addrmod=prog_byte()&0xff;
			int destmod=addrmod&0xf;//Only one address/value
			boolean useRegField=false;
			switch(destmod)
			{
			case 0:
			case 1:
			case 2:
				useRegField=true;
			case 5:
			case 6:
				break;
			default:
				throw new RuntimeException("Bad Addressing Mode dest:"+destmod);
			}
			int regField=0;
			if(useRegField)
			{
				regField=prog_byte()&0x0f;
			}
			int dest=resolveAddr(destmod,regField);
			boolean bValue=(destmod==3) || (destmod==4);
			return new int[]{dest,bValue?1:0};
		}
		default:
		}
		throw new RuntimeException("argc == 1 or 2\r\n");
	}
	public int decode()
	{
		System.out.printf("pc:%08x\n",pc);
		byte code=mem.get_byte(pc);pc++;
		int icode=code&0xff;
		if((icode&0xf0)==0x50)//push reg
		{
			this.push(reg[icode&0x0f]);
			return 1;
		}
		if((icode&0xf0 )==0x60)//pop reg
		{
			reg[icode&0x0f]=pop();
			return 1;
		}
		switch(icode)
		{
		/*
		case 0x01://tentative,reg[0]+=imm32
			int oprd=mem.get_dword(pc);pc+=4;
			reg[0]+=oprd;
			break;
		*/
		case 0x10://mov
		{
			int[] addr=biop_help();
			set_dword(addr[0],addr[1]);
			//System.out.printf("%08x:=%08x\r\n",addr[0],addr[1]);
			break;
		}
		case 0x11://add
		{
			int[] vals=biop_help();
			int orig=get_dword(vals[0]);
			int value=vals[1];
			int tmp=add_flag(orig&MASK32,value & MASK32,0);
			set_dword(vals[0],(int)tmp);
			break;
		}
		case 0x12://adc
		{
			int[] vals=biop_help();
			int orig=get_dword(vals[0]);
			int value=vals[1];
			int tmp=add_flag(orig & MASK32,value & MASK32,flag&CARRY);
			set_dword(vals[0],(int)tmp);
			break;
		}
		case 0x13://sub
		{
			int[] vals=biop_help();
			int orig=get_dword(vals[0]);
			int value=vals[1];
			int tmp=add_flag(orig & MASK32,(1L<<32)-(value &MASK32),0);
			set_dword(vals[0],(int)tmp);
			break;
		}
		case 0x14://sbb
		{
			int[] vals=biop_help();
			int orig=get_dword(vals[0]);
			int value=vals[1];
			int tmp=add_flag(orig& MASK32,(1L<<32)-(value & MASK32),(flag&CARRY)-1);
			set_dword(vals[0],(int)tmp);
			break;
		}
		case 0x15://and
		{
			int[] vals=biop_help();
			int orig=get_dword(vals[0]);
			int value=vals[1];
			orig&=value;
			onoff((orig&0x80000000)!=0,SIG);
			set_dword(vals[0],orig);
			break;
		}
		case 0x16://or
		{
			int[] vals=biop_help();
			int orig=get_dword(vals[0]);
			int value=vals[1];
			orig|=value;
			onoff((orig&0x80000000)!=0,SIG);
			set_dword(vals[0],orig);
			break;
		}
		case 0x17://xor
		{
			int[] vals=biop_help();
			int orig=get_dword(vals[0]);
			int value=vals[1];
			orig^=value;
			onoff((orig&0x80000000)!=0,SIG);
			set_dword(vals[0],orig);
			break;
		}
		case 0x18://mul
		{
			int[] vals=biop_help();
			int orig=get_dword(vals[0]);
			int value=vals[1];
			orig*=value;
			onoff((orig&0x80000000)!=0,SIG);
			set_dword(vals[0],orig);
			break;
		}
		case 0x19://cmp
		{
			int[] vals=biop_help();
			int orig=get_dword(vals[0]);
			int value=vals[1];
			add_flag(orig & MASK32,(1L<<32)-(value &MASK32),0);
		
		}
		case 0x20://div ra5 rd5
		{
			int q=reg[0]/reg[3];
			int r=reg[0]%reg[3];
			reg[0]=q;
			reg[3]=r;
			break;
			//onoff(?,?);///----------------------------------------
		}
		case 0x21://push ...
		{
			int[] addr=addrModeHelper(1);
			int val=addrVal(addr[0],addr[1]!=0);
			push(val);
			break;
		}
		case 0xfe://SYSCALL
		{
			System.out.printf("syscall[id=%x](%08x,%08x,%08x)\n",reg[0],reg[1],reg[2],reg[3]);
			break;
		}
		case 0xff:// EXIT
		{
			return -1;
		}
		default:
			throw new RuntimeException("undefined instruction code"+String.format(":%02x\r\npc=%08x",code,pc));
		}
		return 1;// >0 : doesn't exit
	}
	public String toString()
	{
		String out="";
		for(int i=0; i < REG_SIZE;i++)
		{
			out+=reg_names[i]+"="+String.format("%08x\r\n",reg[i]);
		}
		out+=String.format("pc =%08x\r\n",pc);
		out+=String.format("flg=%08x\r\n",flag);
		return out;
	}
	public String dump(int start,int end)
	{
		String out="";
		for(int i=start;i<end;i+=16)
		{
			out+=String.format("%08x: ",i);
			for(int j=0;j<16;j+=4)
				out+=String.format("%08x ",get_dword(i+j));
			out+="\r\n";
		}
		return out;
	}
	private int[] reg=new int[REG_SIZE];
	private int pc;
	private int flag=0;
	private Mem mem;
	private final long MASK32=0xffffffffL;
	private void onoff(boolean s,int fl)
	{
		if(s)
			flag|=fl;
		else
			flag&=~fl;
	}
	private int[] biop_help()
	{
		int[] addrs=addrModeHelper(2);
		int orig=addrs[0];//ADDRESS of original
		int value;
		if(addrs[2]==0)
			value=get_dword(addrs[1]);
		else
			value=addrs[1];
		return new int[]{orig,value};
	}
	private int addrVal(int what,boolean isValue)
	{
		if(isValue)
			return what;
		return get_dword(what);
	}
	private int add_flag(long orig,long value,int c)
	{
		long utmp=orig+value+c;
		long tmp=(long)((int)orig)+ (long)((int)value)+(long)c;
		onoff((tmp>=0x80000000L )||(tmp<=-0x80000001L),OVER);
		onoff(utmp>=0x100000000L,CARRY);
		onoff(((int)tmp&0x80000000)!=0,SIG);
		return (int)tmp;	
	}
	private void push(int val)
	{
		reg[7]-=4;
		set_dword(reg[7],val);
	}
	private int pop()
	{
		int tmp=get_dword(reg[7]);
		reg[7]+=4;
		return tmp;
	}
}
