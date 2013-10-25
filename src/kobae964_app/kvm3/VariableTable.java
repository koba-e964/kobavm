package kobae964_app.kvm3;

import java.util.ArrayList;
import java.util.List;

public class VariableTable {
	List<VarEntry[]> st;
	VarEntry[] current;
	public VariableTable()
	{
		st=new ArrayList<VarEntry[]>();
		current=null;
	}
	public void allocate(int numVar)
	{
		if(current!=null)
		{
			st.add(current);
		}
		current=new VarEntry[numVar];
	}
	public void deallocate()
	{
		//unreference all objects in current table
		for(VarEntry ve:current){
			ObjManager.unrefer(ve);
		}
		current=st.get(st.size()-1);
		st.remove(st.size()-1);
	}
	public VarEntry load(int i)
	{
		return current[i];
	}
	public void store(int ind,VarEntry var){
		ObjManager.unrefer(current[ind]);//unrefer
		current[ind]=var;
		ObjManager.refer(var);//refer
	}
	public int size(){
		return current.length;
	}
}
