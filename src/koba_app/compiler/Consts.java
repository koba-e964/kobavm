package koba_app.compiler;
public interface Consts
{
	/**
		The kinds of words.
	*/
	public static int
			VAL=1,
			OPER=2,
			GRAM=3,
			SPACE=4;
	/**
		The kinds of identifiers.
	*/
	public static int 
			TYPE=8,
			METHOD=10,
			VAR=12;
	/**
		Compiling modes.
	*/
	public static int
			KLOW=0x10,
			KFLAT=0x20;
	/**
		Operator's modes.
	*/
	public static int
			UNARY_L=0x40,
			BINARY=0x50,
			UNARY_R=0x60;
	/**
		Invalid value.
	*/
	public static int INVALID=-1;
}