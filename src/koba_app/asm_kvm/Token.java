package koba_app.asm_kvm;
import java.util.*;
public class Token
{
	private Token(){}
	public static List<String> split(String str)//----------------------------------------------Œã‚ÅŒ©’¼‚¹------------------------------------------------------------------------------------------------
	{
		List<String> list=new ArrayList<String>(0);
		int b4=0;
		StringBuilder cur=new StringBuilder(0x100);
		for(int pos=0;pos<str.length();pos++)
		{
			int tmp=getType(str.charAt(pos));
			if(b4!=tmp)
			{
				if(b4!=1)
					list.add(cur.toString());
				cur=new StringBuilder(0x100);
				b4=tmp;
			}
			cur.append(str.charAt(pos));
		}
		if(b4!=1)
			list.add(cur.toString());
		return list;
	}
	public static int getType(char c)
	{
		if('\0'<=c&&c<=' ')
			return 1;
		//if(c=='\"')
		//	return 2;
		return 3;
	}
	public static void main(String[] args)
	{
		String test="mov rdx,[rsp+ffffffffh]";
		List<String> list=split(test);
		for(String s:list)
			System.out.println(s);
	}
}