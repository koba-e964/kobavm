package kobae964_app.kvm3.binclz;

import java.io.File;
import java.io.IOException;

import kobae964_app.kvm3.CPU;
import kobae964_app.kvm3.ClassData;
import kobae964_app.kvm3.ClassLoader;
import kobae964_app.kvm3.Mem;

public class MainRunner {
	public static final boolean DEBUG=true;

	public MainRunner(String program,String className, String entryPoint) throws IOException {
		Loader loader=new Loader(new File(program));
		Mem mem=new Mem(0x10000);
		CPU cpu=new CPU(mem);
		//load
		ClassLoader.setMem(mem);
		ClassLoader.registerClassWithBinary(className, loader.bdat);
		
		ClassData classData = ClassLoader.getClassData(className);
		int[] addrAndVT=classData.getVMCodeAddressSizeofVT(entryPoint);
		int addr=addrAndVT[0];
		int sizeOfVT=addrAndVT[1];
		int bcLoad=mem.allocate(8);
		int t=((addr-4-bcLoad)&0xfff)|(sizeOfVT<<12);
		byte[] bootCode={
			CPU.CALL,(byte)t,(byte)(t>>8),(byte)(t>>16), //CALL addr sizeOfVT
			0x3f,0,0,0, //EXIT
		};
		mem.load(bootCode, bcLoad);
		if(DEBUG){
			System.out.println("bcLoad="+bcLoad+", length="+bootCode.length);
		}
		cpu.setPC(bcLoad);//pc=bootCode.start
		cpu.run();
		if(DEBUG){
			System.out.println("memory:"+mem.memView());
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String programFile=args.length>=1?args[0]:"test.bin";
		String entryPoint=args.length>=2?args[1]:"main";
		new MainRunner(programFile, "Main", entryPoint);
	}

}
