package debug;

import mainpackage.Webservices;

public class Tester {

	public static void main(String[] args) {
		mainpackage.Webservices ws = new Webservices();
//		ws.register("اردشیر", "بهاریان", "09119588591", "a.baharian", "dreadlord", 224, null, "ardeshir.baharian@gmail.com", 1);
		ws.getOfficeInfo2(1);
		ws.getUserInfo("doctor", "123456", 1);
	}
	
}
