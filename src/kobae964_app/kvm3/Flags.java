package kobae964_app.kvm3;

public class Flags {
	/**
	 * static class
	 */
	private Flags(){}
	/**
	 * Flags
	 */
	public final static int
		TYPE_MASK=7,
		VAROBJ=0x10,
		CONSTOBJ=0x20,
		IMMUTABLE=VAROBJ|CONSTOBJ;
}
