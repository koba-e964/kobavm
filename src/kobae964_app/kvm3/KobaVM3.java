package kobae964_app.kvm3;

public class KobaVM3 {
	public static void main(String[] args)
	{
		Mem mem=new Mem(0x8000);
		mem.load(new byte[]{

				-1,0,0,0,//EXIT
		}, 0);
		CPU cpu=new CPU(mem);
		cpu.run();
	}
}
