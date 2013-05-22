package koba_app.compiler;

public class Analyzer2
{
	public static void comp_cmd(String in,IL il2,VarInfo vi)
	{
		String[] cmds=Tokenizer.split_il(in);
		if(cmds[0].equals("decl"))
		{
			vi.regist(cmds[1],4);
			return;
		}
		if(cmds[1].equals(":"))
		{
			
			int dest=vi.getIndex(cmds[0]);
			int argc=cmds.length-2;
			for(int k=argc-1;k>=0;k++)
			{
				il2.add("push "+cmds[k+2]);
			}
			il2.add("call "+cmds[0]);
			il2.add("add esp,"+(4*argc));
		}
	}
}