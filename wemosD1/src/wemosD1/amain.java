package wemosD1;

public class amain {
	static int ctnum=0;
	public static void main(String[] args) {
		//timecl tc = new timecl();
		//tc.start();
		server s1 = new server();
		Thread a = new Android();
		a.start();
	}
}
