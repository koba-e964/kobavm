package kobae964_app.kvm3;

public class KobaVM3 {
	public static void main(String[] args)
	{
		Mem mem=new Mem(0x8000);
		mem.load(new byte[]{
				0,100,0,0,//LDC.im 100
				1,3,0,0,//LDV var[3](=9)
				7,0,0,0,//ADD st1,st2
				//2,0,0,0,//GETFIELD 0,0
				-1,0,0,0,//EXIT
		}, 0);
		CPU cpu=new CPU(mem);
		cpu.run();
	}
}
