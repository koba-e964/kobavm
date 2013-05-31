package kobae964_app.kvm3;

public class ClassData {
	int idAttr;
	String name;
	public ClassData(int id,boolean isConstObj,String name)
	{
		idAttr=id*4+(isConstObj?Flags.CONSTOBJ/16:Flags.VAROBJ/16);
		this.name=name;
	}
	public int classID()
	{
		return idAttr/4;
	}
	public boolean isConstObj()
	{
		return idAttr%4==Flags.CONSTOBJ/16;
	}
	public String getName()
	{
		return name;
	}
}
