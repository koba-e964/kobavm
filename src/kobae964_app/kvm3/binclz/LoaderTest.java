package kobae964_app.kvm3.binclz;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class LoaderTest {
	
	@Test
	public void testGetData(){
		byte[] source={'k','v','m',0x7f,//header
				36,0,0,0,0,0,0,0,//code:36~36
				36,0,0,0,0,0,0,0,//constpool:36~36
				0,0,0,0,0,0,0,0,//method:0~0
				0,0,0,0,0,0,0,0,//field:0~0
				};
		Loader sol=new Loader(source);
		int[] res=sol.getData("code_place");
		int[] expecteds={36,0};
		assertArrayEquals(expecteds, res);
	}

	@Test
	public void testInit() {
		byte[] source={'k','v','m',0x7f,//header
				36,0,0,0,0,0,0,0,//code:36~36
				36,0,0,0,0,0,0,0,//constpool:36~36
				0,0,0,0,0,0,0,0,//method:0~0
				0,0,0,0,0,0,0,0,//field:0~0
				};
		Loader sol=new Loader(source);
	}
	@Test
	public void testGetCode() {
		byte[] source={'k','v','m',0x7f,//header
				36,0,0,0,12,0,0,0,//code:36~(36+12)
				36,0,0,0,0,0,0,0,//constpool:36~36
				0,0,0,0,0,0,0,0,//method:0~0
				0,0,0,0,0,0,0,0,//field:0~0
				0x03,(byte)0xd8,0x02,0x26,
				0x32,0x5e,(byte)0x85,(byte)0xee,
				0x32,0x5e,(byte)0x85,(byte)0xee,
		};
		Loader sol=new Loader(source);
		assertArrayEquals(sol.bdat.code, Arrays.copyOfRange(source, 36, 36+12));
	}
	@Test
	public void testGetConstPool() {
		byte[] source={'k','v','m',0x7f,//header
				36,0,0,0,12,0,0,0,//code:36~(36+12)
				48,0,0,0,48,0,0,0,//constpool:36~36
				0,0,0,0,0,0,0,0,//method:0~0
				0,0,0,0,0,0,0,0,//field:0~0
				0x03,(byte)0xd8,0x02,0x26,
				0x32,0x5e,(byte)0x85,(byte)0xee,
				0x32,0x5e,(byte)0x85,(byte)0xee,
				//48
				Loader.INT,0,0,0,
				0x12,0x34,0x56,(byte)0xe8,(byte)0x9a,0,0,0,
				Loader.STRING,0,0,0,
				96,0,0,0,7,0,0,0,//72~(72+6)
				Loader.REAL,0,0,0,
				0,0,0,0,0,0,0,0,
				Loader.ARRAY,0,0,0,
				104,0,0,0,16,0,0,0,
				//96
				'a','b','e','l','s','c','h',0,
				//104
				1,2,3,4,5,6,7,8,
				(byte)0x9f,0,0,0,0x13,0,0,0x57,
		};
		Loader sol=new Loader(source);
		assertArrayEquals(sol.bdat.code, Arrays.copyOfRange(source, 36, 36+12));
		assertArrayEquals(sol.bdat.constPool,
				new Object[]{(Long)0x9ae8563412L,"abelsch",(Double)0.0,
				new long[]{0x0807060504030201L,0x570000130000009fL}});
	}

}
