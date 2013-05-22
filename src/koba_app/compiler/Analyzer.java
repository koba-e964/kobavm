package koba_app.compiler;
import java.io.*;
import java.util.*;
public class Analyzer implements Consts
{
	/*
		{list} includes no operators.
	*/
	public static void comp_func(List<String> list,IL il)
	{
		System.out.println("list:"+getStr(list));
		while(true)
		{
			int[] ks=getDeepestKakkos(list);
			if(ks==null)
				break;
			String funcname=list.get(ks[0]-1);
			if(Tokenizer.getType(funcname)!=VAL)
				throw new IllegalArgumentException();
			String tmpname=Var.getTempVar();
			List<String> funccall=list.subList(ks[0]-1,ks[1]+1);
			il.add(tmpname+" : "+getStr(funccall));
			funccall.clear();
			list.add(ks[0]-1,tmpname);
		}
		il.add(getStr(list));
	}
	private static String getStr(List<String> list)
	{
		String out="";
		for(String tmp:list)
		{
			out+=tmp+" ";
		}
		return out;
	}
	public static void comp_func(String s,IL il)
	{
		String[] cmds=Tokenizer.split(s);
		List<String> list=new ArrayList<String>(cmds.length);
		for(int k=0;k<cmds.length;k++)
		{
			list.add(cmds[k]);
		}
		comp_func(list,il);
	}
	private static int[] getDeepestKakkos(List<String> vs)
	{
		/*{
			System.out.print("getDeepestKakko(List,int)");
			for(String tmp : vs)
				System.out.println(tmp);
		}*/
		int end=0;
		while(end<vs.size()&&vs.get(end)!=null&&!vs.get(end).equals(")"))
		{
			end++;
		}
		if(end>=vs.size())
		{
			return (int[])null;
		}
		int pos=end;
		while(pos>0&&vs.get(pos)!=null&&!vs.get(pos).equals("("))
		{
			pos--;
		}
		return new int[]{pos,end};
	}
	private static int getType(InExpr ie)
	{
		return Tokenizer.getType(ie.toString());
	}
	private static int[] getNextKakko(List<InExpr> vs,int starts)//Returns "(",")"'s indexes
	{
		/*{
			System.out.print("getNextKakko(List,int)");
			for(InExpr tmp : vs)
				System.out.println(tmp);
			System.out.println("starts="+starts);
		}*/
		int end=starts;
		while(end<vs.size()&&vs.get(end)!=null&&!vs.get(end).toString().equals(")"))
		{
			end++;
		}
		if(end>=vs.size())
		{
			return (int[])null;
		}
		int pos=end;
		while(pos>starts&&vs.get(pos)!=null&&!vs.get(pos).toString().equals("("))
		{
			pos--;
		}
		return new int[]{pos,end};
	}
	private static int getNext(List<InExpr> vs,Judger jd,int starts,int type)
	{
		if(vs==null)
			return -1;
		int end=starts;
		while(end<vs.size())
		{
			if(getType(vs.get(end))==type)
				return end;
			end++;
		}
		return -1;
	}
	/**
	
	*/
	public static InExpr comp_c_like(List<InExpr> list,String[][] opers)
	{
		List<InExpr> exprs=new ArrayList<InExpr>(list);
		List<String> oplist=new ArrayList<String>();
		for(String[] sl:opers)
		{
			for(String s :sl)
				oplist.add(s);
		}
		/*
		System.out.print("comp_c_like:{");
		for(InExpr ie : exprs)
		{
			System.out.print(ie.debugInfo());
		}
		System.out.print("}");
		*/
		while(true)
		{
			int[] ks=getNextKakko(exprs,0);
			if(ks==null)
				break;
			InExpr funcname=list.get(ks[0]-1);//実際に関数名かどうかチェックする
			System.out.println("comp_c_like.funcname="+funcname.toString());
			if(oplist.contains(funcname)||funcname.toString().equals("("))
			{
				InExpr result=comp_c_like(exprs.subList(ks[0]+1,ks[1]),opers);
				exprs.subList(ks[0],ks[1]+1).clear();
				exprs.add(ks[0],result);
				continue;
			}
			//funcnameは関数名だった
			InExpr result=comp_args(funcname.toString(),exprs.subList(ks[0]+1,ks[1]),opers);
			exprs.subList(ks[0]-1,ks[1]+1).clear();
			exprs.add(ks[0]-1,result);
		}
		return comp_opers(exprs,opers);
		
	}
	public static InExpr comp_args(String funcname,List<InExpr> list,String[][] opers)
	{
		//","を探す
		List<InExpr> exprs=new ArrayList<InExpr>(list);
		List<InExpr> arglist=new ArrayList<InExpr>();
		System.out.print("comp_args:{");
		for(InExpr ie : exprs)
		{
			System.out.print(ie.debugInfo());
		}
		System.out.print("}");
		int commaplace=-1;
		int parendepth=0;
		for(int pos=0;pos<list.size();pos++)
		{
			String cur=exprs.get(pos).toString();
			if(cur.equals("("))
			{
				parendepth++;
				continue;
			}
			if(cur.equals(")"))
			{
				parendepth--;
				if(parendepth<0)
					throw new RuntimeException();
				continue;
			}
			if(parendepth==0 && cur.equals(","))
			{
				InExpr argv=comp_c_like(exprs.subList(commaplace+1,pos),opers);
				arglist.add(argv);
				commaplace=pos;
			}
		}
		InExpr argv=comp_c_like(exprs.subList(commaplace+1,list.size()),opers);
		arglist.add(argv);
		if(parendepth>=1)
			throw new RuntimeException("Analyzer.comp_args()");
		return new TreeInExpr(funcname.toString(),
			arglist.toArray(new InExpr[0]));
	}
	public static InExpr comp_opers(List<InExpr> list,String[][] opers)
	{
		List<InExpr> exprs=new ArrayList<InExpr>(list);
		System.out.print("comp_opers:\r\n{");
		for(InExpr ie : exprs)
		{
			System.out.print(ie.debugInfo());
		}System.out.print("}");
		for(int i=0;i<opers.length;i++)
		{
			String[] priorlist=opers[i];
			for(int j=0;j<exprs.size();j++)
			{
				InExpr cur=exprs.get(j);
				int k;
				for(k=0;k<priorlist.length;k++)
				{
					if(cur.toString().equals(priorlist[k]))
					{
						String funcname="oper"+priorlist[k];
						InExpr result=new TreeInExpr(funcname,new InExpr[]{exprs.get(j-1),exprs.get(j+1)});
						exprs.subList(j-1,j+2).clear();
						exprs.add(j-1,result);
						j--;
					}
				}
			}
		}
		if(exprs.size()>=2)
		{
			System.out.print("comp_opers:Error!  ");
			for(InExpr qw :exprs)
				System.out.print(qw.debugInfo());
			throw new RuntimeException();
		}
		return exprs.get(0);
	}
}