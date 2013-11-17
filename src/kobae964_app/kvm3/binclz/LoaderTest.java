package kobae964_app.kvm3.binclz;

import static kobae964_app.kvm3.CPU.LDCim;
import static kobae964_app.kvm3.CPU.RET;
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
		@SuppressWarnings("unused")
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
				48,0,0,0,48,0,0,0,//constpool:48~(48+48)
				96,0,0,0,0,0,0,0,//method:96~96
				96,0,0,0,0,0,0,0,//field:96~96
				0x03,(byte)0xd8,0x02,0x26,
				0x32,0x5e,(byte)0x85,(byte)0xee,
				0x32,0x5e,(byte)0x85,(byte)0xee,
				//48
				Loader.INT,0,0,0,
				0x12,0x34,0x56,(byte)0xe8,(byte)0x9a,0,0,0,
				Loader.STRING,0,0,0,
				96,0,0,0,7,0,0,0,//72~(72+7)
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
	byte[] intsToBytes(int[] array){
		byte[] result=new byte[array.length*4];
		for(int i=0;i<array.length;i++){
			int v=array[i];
			for(int j=0;j<4;j++){
				result[4*i+j]=(byte)(v>>>(8*j));
			}
		}
		return result;
	}
	@Test
	public void testFields() {
		byte[] source={'k','v','m',0x7f,//header
				36,0,0,0,12,0,0,0,//code:36~(36+12)
				48,0,0,0,36,0,0,0,//constpool:48~(48+36)
				92,0,0,0,0,0,0,0,//method:92~92
				92,0,0,0,12,0,0,0,//field:92~(92+16)
				0x03,(byte)0xd8,0x02,0x26,
				0x32,0x5e,(byte)0x85,(byte)0xee,
				0x32,0x5e,(byte)0x85,(byte)0xee,
				//48
				Loader.INT,0,0,0,
				0x12,0x34,0x56,(byte)0xe8,(byte)0x9a,0,0,0,
				Loader.STRING,0,0,0,
				84,0,0,0,5,0,0,0,//84~(84+5):test0
				Loader.STRING,0,0,0,
				89,0,0,0,1,0,0,0,//92~93:I
				//84
				't','e','s','t','0',
				//89
				'I',0,0,
				//92
				1,0,0,0,//name=sid[1]:test0
				0,0,0,0,//offset=0
				2,0,0,0,//sign=sid[2]:I
		};
		Loader sol=new Loader(source);
		assertArrayEquals(sol.bdat.code, Arrays.copyOfRange(source, 36, 36+12));
		assertArrayEquals(sol.bdat.constPool,
				new Object[]{(Long)0x9ae8563412L,"test0","I",});
		assertArrayEquals(new String[]{"test0"},sol.bdat.fieldNames);
	}
	@Test
	public void testExe(){
		byte[] header={'k','v','m',0x7f};
		assert header.length==4;
		int[] code=new int[]{
			LDCim+256*1,//LDC.im 1
			RET+0x100,//RET st[0]=>1
		};
		int headInfoLen=32;//bytes
		int cpoolLen=24;
		int codePos=header.length+headInfoLen;
		int methodPos=codePos+4*code.length+cpoolLen;
		int methodLen=16;
		int namePos=methodPos+methodLen;
		byte[] name="main".getBytes();
		int signPos=namePos+name.length;
		byte[] sign="".getBytes();
		int[] cpool=new int[]{
			Loader.STRING,namePos,name.length,
			Loader.STRING,signPos,sign.length,
		};
		assert 4*cpool.length==cpoolLen;
		int[] method=new int[]{
			0,//name-sid:"main"
			1,//num-var:1
			0,//offset:relative to codePos
			1,//sign-sid:""
		};
		assert 4*method.length==methodLen;
		int[] field=new int[0];
		int[] headInfo=new int[]{
			codePos,//code-place
			4*code.length,//code-length
			codePos+4*code.length,//cpool-place
			4*cpool.length,//cpool-length
			methodPos,//method-place
			4*method.length,
			methodPos+4*method.length,//field-place
			4*field.length,//field-length
		};
		assert 4*headInfo.length==headInfoLen;
		byte[] ld=new byte[signPos+sign.length];
		System.arraycopy(header, 0, ld, 0, header.length);
		System.arraycopy(intsToBytes(headInfo), 0, ld, header.length, headInfoLen);
		System.arraycopy(intsToBytes(code), 0, ld,codePos,4*code.length);
		System.arraycopy(intsToBytes(cpool), 0, ld, codePos+4*code.length, cpoolLen);
		System.arraycopy(intsToBytes(method), 0, ld, methodPos,methodLen);
		System.arraycopy(name, 0, ld, namePos, name.length);
		System.arraycopy(sign, 0, ld, signPos, sign.length);
		Loader sol=new Loader(ld);
		assertArrayEquals(new String[]{"main",""},sol.bdat.constPool);
		assertArrayEquals(new String[]{"main"},sol.bdat.methodNames);
		assertArrayEquals(new int[]{0},sol.bdat.methodOffsets);
	}
}
