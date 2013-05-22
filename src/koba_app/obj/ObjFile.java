package koba_app.obj;

import java.io.*;

public class ObjFile implements Closeable
{
	private File obj;
	private RandomAccessFile ra;
	public ObjFile(File f)throws IOException
	{
	}
	public String[] depend()
	{
		return null;
	}
	public String[] export()
	{
		return null;
	}
	public void close()throws IOException
	{
		ra.close();
	}
}
