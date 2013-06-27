package kobae964_app.kvm3;

import static org.junit.Assert.*;

import java.util.Random;

import kobae964_app.kvm3.VarEntry.DataTypeMismatchException;

import org.junit.Test;

public class CallStackTest {

	@Test
	public void testCheckDataType() {
		CallStack sol=new CallStack();
		sol.pushBool(false);
		try{
			sol.popInt();//throws IllegalStateException
		}catch(VarEntry.DataTypeMismatchException ex){
			System.out.println(ex.toString());
			return;
		}
		fail("No exception was thrown.");
	}
	@Test
	public void testCheckIndex0(){
		CallStack sol=new CallStack();
		try{
			sol.setAt(0,null);
		}catch(RuntimeException ex){
			System.out.println(ex);
			return;
		}
		fail();
	}
	@Test
	public void testCheckIndex1(){
		CallStack sol=new CallStack();
		sol.pushInt(20);
		VarEntry result=sol.getAt(0);
		assertEquals(20,result.value);
		assertEquals(1,sol.size());
	}
	@Test
	public void testPushPop0(){
		CallStack cs=new CallStack();
		cs.pushString("xfyxfxg");
		cs.pushReal(3.0);
		double r=cs.popReal();
		assertEquals(3.0, r, 1e-9);
		String v=cs.popString();
		assertEquals("xfyxfxg",v);
	}
	@Test
	public void testPushPop1(){
		CallStack cs=new CallStack();
		cs.pushReal(2.2360679);
		double r=cs.popReal();
		assertEquals(2.2360679, r, 1e-9);
	}
	/**
	 * Random test of pushInt/pushReal/popInt/popReal
	 */
	@Test
	public void testPushPop2(){
		final int n=10000;
		Random rnd=new Random();
		CallStack cs=new CallStack();
		for(int i=0;i<n;i++){
			long val=rnd.nextLong();
			cs.pushInt(val);
			assertEquals(val, cs.popInt());
		}
		for(int i=0;i<n;i++){
			double value=rnd.nextDouble();
			cs.pushReal(value);
			assertEquals(value,cs.popReal(),1e-9);
		}
	}
	
}
