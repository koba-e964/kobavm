package kobae964_app.kvm3;

/**
 * This class expresses data that is compiled from source and should be stored in class File.
 * @author koba-e964
 */
public class BinaryClassData {
	public BinaryClassData(){}
	public byte[] code;
	public Object[] constPool;
	public String[] methodNames;
	public String[] methodSigns;
	public int[] methodOffsets;
	public String[] fieldNames;
	public String[] fieldSigns;
	public int[] fieldOffsets;
}
