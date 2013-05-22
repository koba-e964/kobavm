package koba_app.auto;

import java.io.IOException;

import koba_app.kvm.VMMain;
import koba_app.simpl.Simpl;



public class Auto {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filename="./dat/tcalc.bin";
		String asmname="./dat/tcalc.asm";
		try {
			Simpl.main(new String[]{filename,asmname});
			VMMain.main(new String[]{filename});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
