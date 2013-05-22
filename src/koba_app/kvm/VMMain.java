package koba_app.kvm;
import java.io.*;


public class VMMain
{
	public static void main(String[] args)throws IOException
	{

		Mem mem=new Mem(0x10000);
		CPU cpu=new CPU(mem);
		String filename=args.length>=1?args[0]:"tcalc.bin";
		RandomAccessFile codefile=new RandomAccessFile(filename,"r");
		if(codefile.length() >= 0x80000000L)
			throw new RuntimeException();
		byte[] code=new byte[(int)codefile.length()];
		codefile.seek(0);
		codefile.readFully(code);
		mem.load(code,0);
		try
		{
			while(true)
			{
				int retval=cpu.decode();
				if(retval<= 0)
					break;
			}
		}
		catch(RuntimeException re)
		{
			re.printStackTrace();
		}
		System.out.println(cpu);
		System.out.print(cpu.dump(0x7b00,0x7c00));
		return;
	}
}
