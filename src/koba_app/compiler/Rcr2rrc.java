package koba_app.compiler;

import java.io.*;
import java.util.*;
import static koba_app.compiler.Consts.*;
public class Rcr2rrc
{
	public static void main(String[] s)
	{
		try
		{
			new Rcr2rrc();
		}
		catch(Exception x)
		{
			x.printStackTrace();
		}
	}
	public Rcr2rrc()throws Exception
	{
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String cmd=br.readLine();
		String[] res=Tokenizer.split(cmd);
		//trans(res,new Judger(),KLOW);
		for(int k=0;k<res.length;k++)
		{
			System.out.println(res[k]);
		}
		
	}
}
