package kobae964_app.kvm3;

import static org.junit.Assert.*;

import org.junit.Test;

public class CallStackTest {

	@Test
	public void testCheckDataType() {
		CallStack sol=new CallStack();
		sol.pushBool(false);
		try{
			sol.popInt();//throws IllegalStateException
		}catch(IllegalStateException ex){
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
	
}
