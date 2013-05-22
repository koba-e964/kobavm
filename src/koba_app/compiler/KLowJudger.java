package koba_app.compiler;

import java.util.*;
import static koba_app.compiler.Consts.*;

public class KLowJudger extends Judger
{
	public KLowJudger()
	{
		super();
	}
	public boolean isType(String s)
	{
		String raw=null;
		for(int k=s.length()-1;k>=0;k--)
		{
			if(s.charAt(k)!='*')
			{
				raw=s.substring(0,k+1);
				break;
			}
		}
		return super.isType(raw);
	}
	public static void main(String[] args)
	{
		System.out.println(new KLowJudger().isType("char**"));
	}
}