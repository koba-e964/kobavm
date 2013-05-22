package kobae964_app.kvmclzlib;

public class ManagedIntSample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testCode0();
		System.out.println(ManagedInt.getStatistics());
	}
	static void testCode0()
	{
		ManagedInt sum=ManagedInt.valueOf(0);
		for(ManagedInt i=ManagedInt.valueOf(0),s=ManagedInt.valueOf(100);i.lt(s);i=i.add(ManagedInt.valueOf(1)))
		{
			sum=sum.add(i);
		}
	}

}
