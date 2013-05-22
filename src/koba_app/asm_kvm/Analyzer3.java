package koba_app.asm_kvm;
import java.util.*;
import java.io.*;

public class Analyzer3
{
	public static void analyze(String code,Info info)
	{
		List<String> list=Token.split(code);
		if(list.get(0).equals("mov"))
			mov(list,info);
	}
	public static void mov(List<String> code,Info info)
	{
		info.addCode(new byte[]{(byte)0x66,(byte)0xe8,(byte)0xc0});
	}
}
