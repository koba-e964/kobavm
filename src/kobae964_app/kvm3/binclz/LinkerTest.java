package kobae964_app.kvm3.binclz;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class LinkerTest {

	@Test
	public void testYieldCodeConstsOnly() throws IOException {
		Linker inst = new Linker();
		inst.addConst((Long)3L);
		inst.addConst("testString");
		inst.setCode(new byte[0]);
		byte[] code = inst.yieldCode();
		FileOutputStream fw = new FileOutputStream("linktest.bin");
		fw.write(code);
		fw.close();
		byte[] expected = {'k', 'v', 'm', 0x7f,
			36,0,0,0,0,0,0,0,//code:36~36
			36,0,0,0,24,0,0,0,//constpool:36~(36+24)
			60,0,0,0,0,0,0,0,//method:60~60
			60,0,0,0,0,0,0,0,//field:60~60
			Loader.INT, 0, 0, 0,
			3, 0, 0, 0, 0, 0, 0, 0, 
		};
		System.out.println(Arrays.toString(code));
		assertArrayEquals(expected, Arrays.copyOf(code, expected.length));
	}

}
