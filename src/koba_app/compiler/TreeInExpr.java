package koba_app.compiler;

public class TreeInExpr extends InExpr implements Consts
{
	private String parent=null;
	private InExpr[] child=null;
	public int getTag()
	{
		return VAL;
	}
	public TreeInExpr(String parent,InExpr[] ies)
	{
		this.parent=parent;
		child=ies;
	}
	public InExpr get(int k)
	{
		return child[k];
	}
	public void toIL(IL il)
	{
		throw new InternalError();
	}
	public String debugInfo()
	{
		String out="class TreeInExpr:InExpr (parent="+parent
					+",child:";
		if(child==null)
			return out;
		for(int k=0;k<child.length;k++)
		{
			out+=child[k].debugInfo();
		}
		return out+")";
	}
}