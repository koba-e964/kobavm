package koba_app.compiler;

public class Var
{
	private static int i;
	public static String getTempVar()
	{
		i++;
		return "$temp"+i;
	}
	public Var(String name)
	{
		this.name=name;
	}
	public String getName()
	{
		return name;
	}
	public Var toIL(IL il)
	{
		return this;
	}
	private String name;
}