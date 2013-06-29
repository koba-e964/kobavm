package kobae964_app.kvm3;

/**
 * This class expresses data that is compiled from source and should be stored in class File.
 * @author koba-e964
 */
public class BinaryClassData implements Cloneable{
	public BinaryClassData(){}
	public byte[] code;
	public Object[] constPool;
	public String[] methodNames;
	public String[] methodSigns;
	public int[] methodOffsets;
	public int[] methodNumberOfVariable;//number of variables used in the method
	public String[] fieldNames;
	public String[] fieldSigns;
	public int[] fieldOffsets;
	public BinaryClassData clone(){
		try {
			BinaryClassData inst=(BinaryClassData) super.clone();
			inst.code=code.clone();
			inst.constPool=constPool.clone();
			inst.methodNames=methodNames.clone();
			inst.methodSigns=methodSigns.clone();
			inst.methodOffsets=methodOffsets.clone();
			inst.methodNumberOfVariable=methodNumberOfVariable.clone();
			inst.fieldNames=fieldNames.clone();
			inst.fieldSigns=fieldSigns.clone();
			inst.fieldOffsets=fieldOffsets.clone();
			return inst;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new AssertionError(e);
		}
	}
}
