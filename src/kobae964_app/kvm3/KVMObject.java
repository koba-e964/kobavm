package kobae964_app.kvm3;
import static kobae964_app.kvm3.Flags.*;

import java.util.Arrays;

/**
 * Internal expression of objects in VM.
 * Every instance of KVMObject should be created by Heap.
 *
 */
public class KVMObject {
	private int typeInfo;
	private byte[] data;
	/**
	 * This constructor should be called only by {@link Heap#create(int, byte[], int)}.
	 * @param classID
	 * @param data
	 * @param flags
	 */
	KVMObject(int classID,byte[] data,int flags)
	{
		if((flags&IMMUTABLE)==IMMUTABLE)
		{
			throw new IllegalStateException("An object could not be both varobj and constobj.");
		}
		this.data=data;
		typeInfo=classID*4+(flags&IMMUTABLE)/16;//0-3
	}
	public int getClassID()
	{
		return typeInfo/4;
	}
	
	public boolean isVarObj()
	{
		return (typeInfo&(VAROBJ/16))!=0;
	}
	public boolean isConstObj()
	{
		return (typeInfo&(CONSTOBJ/16))!=0;
	}
	public long getInt(int start,int len){
		long v=0;
		for(int i=0;i<len&&i<8;i++){
			v|=(data[start+i]&0xffL)<<(8*i);
		}
		return v;
	}
	public VarEntry getVarEntry(int start){
		int type=(int)getInt(start, 4);
		long value=getInt(start+4, 8);
		return new VarEntry(type, value);
	}
	public void setInt(int start,int len,long value){
		for(int i=0;i<len&&i<8;i++){
			data[start+i]=(byte)(value>>>(8*i));
		}
	}
	/**
	 * size=12
	 * @param start 
	 * @param ve VarEntry
	 */
	public void setVarEntry(int start,VarEntry ve){
		setInt(start,4,ve.type);
		setInt(start+4,8,ve.value);
	}
	public int length(){
		return data.length;
	}
	public byte[] getContent(){
		return Arrays.copyOf(data,data.length);
	}

}
