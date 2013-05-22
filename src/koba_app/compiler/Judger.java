package koba_app.compiler;

import java.util.*;
import static koba_app.compiler.Consts.*;

public class Judger extends Object
{
	protected List<String> types;
	protected List<String> methods;
	protected List<String> vars;
	public Judger()
	{
		types=new ArrayList<String>(0);
		methods=new ArrayList<String>(0);
		vars=new ArrayList<String>(0);
		addType("int").
		addType("short").
		addType("char").
		addType("void");
	}
	public Judger addType(String name)
	{
		types.add(name);
		return this;
	}
	public boolean isType(String s)
	{
		return types.contains(s);
	}
	public Judger clearType()
	{
		types.clear();
		return this;
	}
	public int judge(String s)
	{
		if(isVar(s))	return VAR;
		if(isType(s))	return TYPE;
		if(isMethod(s))	return METHOD;
				return 0;
	}
	public Judger addMethod(String name)
	{
		methods.add(name);
		return this;
	}
	public boolean isMethod(String s)
	{
		return methods.contains(s);
	}
	public Judger clearMethod()
	{
		methods.clear();
		return this;
	}
	public Judger addVar(String name)
	{
		vars.add(name);
		return this;
	}
	public boolean isVar(String s)
	{
		return vars.contains(s);
	}
	public Judger clearVar()
	{
		vars.clear();
		return this;
	}
	public Judger clearAll()
	{
		clearType();
		clearMethod();
		return clearVar();
	}
	public String toString()
	{
		StringBuffer res=new StringBuffer("Registered Types:\r\n");
		res.append(getStr(types));
		res.append("Registered Methods:\r\n").append(getStr(methods));
		res.append("Registered Variables:\r\n").append(getStr(vars));
		return res.toString();
	}
	private static String getStr(List<String> vs)
	{
		int vssize=vs.size();
		StringBuffer res=new StringBuffer(vssize*15);
		for(int k=0;k<vssize;k++)
			res.append(vs.get(k)).append("\r\n");
		return res.toString();
	}
	public boolean isNum(String s)//‚»‚Ì‚¤‚¿non-static‚É‚È‚é‚©‚à
	{
		char tmp=s.charAt(0);
		return tmp>='0'&&tmp<='9';
	}
}