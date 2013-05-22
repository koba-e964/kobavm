package koba_app.compiler;
import java.util.*; 
public class VarInfo implements Debugged
{
	public VarInfo()
	{
		names=new ArrayList<String>();
		indexes=new ArrayList<Integer>();
		resp=0;
	}
	public boolean regist(String varname,int size)
	{
		resp-=size;
		if(!names.contains(varname))
		{
			names.add(varname);
			indexes.add(resp);
			return true;
		}
		return false;
	}
	public int getIndex(String varname)
	{
		if(names.contains(varname))
		{
			return indexes.get(names.indexOf(varname))-resp;
		}
		throw new RuntimeException();
	}
	public String getName(int index)
	{
		if(indexes.contains(index+resp))
		{
			return names.get(indexes.indexOf(index+resp));
		}
		throw new RuntimeException();
	}
	public int unregist(String varname)
	{
		int tmp=names.indexOf(varname);
		names.subList(tmp,names.size()).clear();
		indexes.subList(tmp,indexes.size()).clear();
		int oldresp=resp;
		if(tmp>=1)
		{
			resp=indexes.get(tmp-1);
		}
		else
		{
			resp=0;
		}
		return -oldresp+resp;
	}
	public int relStack()
	{
		return resp;
	}
	private int resp;
	private List<String> names;
	private List<Integer> indexes;
	public String toString()
	{
		String out="resp:";
		out+=resp+"\r\n";
		for(int k=0;k<names.size();k++)
		{
			out+=names.get(k)+":"+indexes.get(k)+"\r\n";
		}
		return out;
	}
	public String debugInfo()
	{
		return "class VarInfo:\r\n"+toString();
	}
	public static void main(String[] args)
	{
		VarInfo vi=new VarInfo();
		vi.regist("a",4);
		vi.regist("b",8);
		vi.unregist("a");
		System.out.println(vi.debugInfo());
	}
}