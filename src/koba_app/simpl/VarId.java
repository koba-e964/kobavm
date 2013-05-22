package koba_app.simpl;

public class VarId
{
	private int current=0;
	public VarId(){}
	public int nextInt(){return current++;}
}