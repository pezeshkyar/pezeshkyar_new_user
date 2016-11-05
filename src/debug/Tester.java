package debug;

import mainpackage.Webservices;

public class Tester {

	public static void main(String[] args) {
		mainpackage.Webservices ws = new Webservices();
//		ws.register("اردشیر", "بهاریان", "09119588591", "a.baharian", "dreadlord", 224, null, "ardeshir.baharian@gmail.com", 1);
//		ws.getOfficeInfo2(1);
//		ws.getUserInfo("a.baharian", "123", 1);
		ws.getAllUserAllTicket("a.baharian", "123", 0);
	}

}
