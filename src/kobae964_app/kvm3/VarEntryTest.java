package kobae964_app.kvm3;

import static org.junit.Assert.*;
import kobae964_app.kvm3.VarEntry.DataTypeMismatchException;

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

}
