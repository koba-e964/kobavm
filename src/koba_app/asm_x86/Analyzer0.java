package koba_app.asm_x86;

import java.io.*;
import java.util.*;

public class Analyzer0
{
	private static boolean isNum(char a)
	{
		return ('0'<=a)&&(a<='9');
	}
	public static long decode(String s)
	{
		if(!isNum(s.charAt(0)))
			return 0;
		int base=10;
		String raw = null;
		if(s.charAt(0)=='0'&&s.length()>=1)
		{
			switch(s.charAt(1))
			{
			case 'x':
			case 'X':
				base=16;
				raw=s.substring(2,s.length());
				break;
			case 'b':
			case 'B':
				base=2;
				raw=s.substring(2,s.length());
				break;
			default:
				raw=s.substring(0,s.length());
			}
		}
		char suffix=s.charAt(s.length()-1);
		if(!isNum(suffix))
		{
			switch(suffix)
			{
			case 'h':
			case 'H':
				base=16;
				raw=raw.substring(0,raw.length()-1);
				break;
			case 'q':
			case 'Q':
				base=8;
				raw=raw.substring(0,raw.length()-1);
				break;
			default:
				throw new NumberFormatException();
			}
		}
		else
			raw=s;
		return Long.parseLong(raw,base);
		
	}
}
