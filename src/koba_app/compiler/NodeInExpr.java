package koba_app.compiler;

public class NodeInExpr extends InExpr implements Debugged
{
	public int tag=0;
	public int getTag()
	{
		return tag;
	}
	private String val=null;
	public NodeInExpr(String s)
	{
		val=s;
	}
	public String toString()
	{
		return val;
	}
	public String debugInfo()
	{
		return "class NodeInExpr(\r\nval="+val+"\r\n)\r\n";
	}
	public void toIL(IL il)
	{
		throw new RuntimeException();
	}
}
