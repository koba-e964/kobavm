package koba_app.simpl;


public abstract class BasicTree
{
	public abstract BasicTree[] children();
	public abstract byte[] toMachine();
	public abstract int varId();
	public abstract String asm(int addr);
}