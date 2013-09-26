package kobae964_app.kvm3;

import static org.junit.Assert.*;
import kobae964_app.kvm3.VarEntry.DataTypeMismatchException;
import kobae964_app.kvm3.inline.KString;

import org.junit.Test;

public class VarEntryTest {

	@Test
	public void testRealToVarEntry0() {
		final double value=1/.0;
		VarEntry ve=VarEntry.valueOf(value);
		assertEquals(DataType.REAL.ordinal(),ve.type);
		assertEquals(Double.doubleToLongBits(value),ve.value);
	}

	@Test
	public void testVarEntryToReal() {
		final int n=100;
		for(int i=0;i<n;i++){
			double value=Math.random();
			VarEntry entry=new VarEntry(DataType.REAL,Double.doubleToLongBits(value));
			assertEquals(value,entry.toReal(),1e-9);
		}
	}

	@Test
	public void testCheckDataType() {
		for(DataType type:DataType.values()){
			new VarEntry(type,0).checkDataType(type);//It should not throw any exceptions.
		}
		for(DataType type1:DataType.values()){
			for(DataType type2:DataType.values()){
				if(type1==type2)continue;
				try{
					new VarEntry(type1, 0).checkDataType(type2);//It should throw an DataTypeMismatchException.
				}catch(DataTypeMismatchException ex){
					//ok
					continue;
				}
				fail(DataTypeMismatchException.class.getName()+" was not thrown.");
			}
		}
	}
	@Test
	public void testConstructor0(){
		try{
			new VarEntry(DataType.INT.ordinal()|Flags.CONSTOBJ,10000);//this statement will throw IllegalArgumentException
		}catch(IllegalArgumentException ex){
			//ok
			return;
		}
		fail("Error! "+IllegalArgumentException.class.getName()+" should be thrown.");
	}
	@Test
	public void testEquals0(){
		VarEntry ve0=new VarEntry(DataType.INT,3);
		VarEntry ve1=new VarEntry(DataType.REAL,3);
		assertFalse(ve0.equals(ve1));
		VarEntry ve2=new VarEntry(DataType.INT,3);
		assertTrue(ve0.equals(ve2));
	}
	@Test
	public void testValueOf(){
		VarEntry v0=VarEntry.valueOf((Integer)15);
		assertEquals(new VarEntry(DataType.INT,15),v0);
		VarEntry v1=VarEntry.valueOf((Float)13.0f);
		assertEquals(VarEntry.valueOf(13.0),v1);
		VarEntry v2=VarEntry.valueOf("StringTest");
		assertEquals("StringTest",KString.getContent(v2.value));
		VarEntry v3=VarEntry.valueOf(true);
		assertEquals(new VarEntry(DataType.BOOL,1),v3);
		VarEntry v4=VarEntry.valueOf((Character)'2');
		assertEquals(new VarEntry(DataType.INT,0x32),v4);
		VarEntry v5=VarEntry.valueOf(Byte.valueOf((byte) -19));
		assertEquals(new VarEntry(DataType.INT,-19L),v5);
	}
}
