package koba_app.simpl;


public class Util
{
	
	public static byte[] intToBytes(int val)
	{
		byte[] out=new byte[4];
		for(int i=0;i<4;i++)
		{
			out[i]=(byte)val;
			val>>>=8;
		}
		return out;
	}
	public static String intToBytesStr(int val)
	{
		final char[] format="0123456789abcdef".toCharArray();
		char[] out=new char[3*4-1];
		for(int i=0;i<4;i++)
		{
			out[3*i+1]=format[val&0x0f];
			out[3*i]=format[(val>>4)&0xf];
			if(i<=2)out[3*i+2]=' ';
			val>>>=8;
		}
		return new String(out);
	}
}
