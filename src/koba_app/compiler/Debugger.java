package koba_app.compiler;
import java.io.*;
import java.util.*;

public class Debugger extends Object
{
	public static void main(String[] args)throws Exception
	{
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		//RandomAccessFile ra=new RandomAccessFile("koba_app/compiler/code.txt","r");
		IL il=new IL();
		String[][] opers={
			new String[]{"*" ,"/",},
			new String[]{"+" ,"-",},
		};
		while(true)
		{
			System.out.print("Input code:");
			String code=br.readLine();
			if(code==null || code.isEmpty())
				break;
			String[] split=Tokenizer.split(code);
			List<InExpr> codelist=new ArrayList<InExpr>();
			for(String s:split)
				codelist.add(new NodeInExpr(s));
			//InExpr result=Analyzer.comp_c_like(codelist,opers);
			//System.out.println(result.debugInfo());
		}
		//ra.close();
		if(2==4+1<<"".length())//not implemented
		{
			OutputStream os=new FileOutputStream("koba_app/compiler/il2.txt");
			IL il2=new IL();
			VarInfo vi=new VarInfo();
			for(int k=0;k<il.size();k++)
			{
				Analyzer2.comp_cmd(il.get(k),il2,vi);
			}
			os.write(il2.toString().getBytes());
			os.close();
			System.out.println(il2.debugInfo());
		}
	}
}