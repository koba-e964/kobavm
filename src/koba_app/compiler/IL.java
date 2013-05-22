package koba_app.compiler;
import java.util.*;
public class IL implements Debugged
{
	private List<String> code;
	public IL()
	{
		code=new ArrayList<String>();
	}
	public void add(String stmt)
	{
		code.add(stmt);
	}
	public String get(int i)
	{
		return code.get(i);
	}
	public int size()
	{
		return code.size();
	}
	public String toString()
	{
		String out="";
		for(String s : code)
		{
			out+=s+"\r\n";
		}
		return out;
	}
	public String debugInfo()
	{
		return "class IL:\r\n"+toString();
	}
}