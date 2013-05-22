package koba_app.compiler;

public abstract class InExpr implements Debugged
{
	public abstract int getTag();
	protected InExpr()
	{
	}
	public abstract String debugInfo();
	public abstract void toIL(IL il);
}
