package kobae964_app.kvm3.binclz;

import static org.junit.Assert.*;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import static kobae964_app.kvm3.CPU.*;

public class MainRunnerTest {
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
	public void testSimpleCode() throws IOException {
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
		String exeName="test.bin";
		FileOutputStream fos=new FileOutputStream(exeName);
		fos.write(ld);
		fos.close();
		MainRunner.main(new String[]{exeName,"main"+"."});
	}

}
