package debug;

import mainpackage.Webservices;

public class Tester {

	public static void main(String[] args) {
		mainpackage.Webservices ws = new Webservices();
//		ws.register("اردشیر", "بهاریان", "09119588591", "a.baharian", "dreadlord", 224, null, "ardeshir.baharian@gmail.com", 1);
//		ws.getOfficeInfo2(1);
//		ws.getUserInfo("a.baharian", "123", 1);
//		ws.getUserInfoWithoutPic("test", "123", 1);
//		ws.getDoctorPic("test", "123", 1);
//		ws.addSecretaryToOffice2("test", "123", 1, "n1");
//		ws.getReservationByUser("ahodasht", "123", 1, 10, 0);
//		ws.setQuestion("test", "123", 1, "asdhjasgdhgdhgasgj", 1);
//		ws.setReply("test", "123", 1, 1, "ashgdhasgdhgadhgjsa");
//		ws.login2("user3", "123456", 1);
//		ws.register("ali", "abdi", "0911", "ali", "123", 111, "NULL", "NULL", 1);
//		int[] q = new int[]{1,2,3,6,11,4,5,10};
//		String[] r = new String[] {"12","12","12","12","12","1","1","0"}; 
//		ws.setReplyBatchForUser("test1", "40bd001563085fc35165329ea1ff5c5ecbdbbeef", 1, "hos", q, r);
//		ws.setReply("ziari", "123", 1, 1, "yyyy");
//		ws.getOfficeForUser("ali", "123");
//		ws.getOfficeForUser("doctor", "123456");
//		ws.getRoleInAll("doctor", "123456");
//		ws.getOfficeForUser("ahmad", "123");
//		ws.getAllUnreadMessages("hadi1", "7c4a8d09ca3762af61e59520943dc26494f8941b");
//		ws.getAllOfficeForCity("hadi1", "7c4a8d09ca3762af61e59520943dc26494f8941b", 43, 1, 10);
//		ws.getOfficeByFilter("hadi1", "7c4a8d09ca3762af61e59520943dc26494f8941b", -1, 204, -1, -1, "", "", 5, 1);
//		ws.updateUserInfo3("hadi", "7c4a8d09ca3762af61e59520943dc26494f8941b", "سید هادی", "سعیدی", "09118646086", 43, "123456", "hadi");
//		ws.getReservationByUser2("hadi", "7c4a8d09ca3762af61e59520943dc26494f8941b", 10, 1);
//		ws.getRoleInOffice("hadi", "7c4a8d09ca3762af61e59520943dc26494f8941b", 2548);
//		ws.getProvince();
//		ws.getOfficeForDoctorOrSecretary("vida", "7c4a8d09ca3762af61e59520943dc26494f8941b");
//		ws.setResNum("hadi", "f7c3bc1d808e04732adf679965ccc34ca7ae3441", 50000);
//		ws.reserveForMe("hadi", "f7c3bc1d808e04732adf679965ccc34ca7ae3441", 4, 0, 2, 1);
		ws.reserveForGuestFromUser("hadi", "f7c3bc1d808e04732adf679965ccc34ca7ae3441", 4, 0, 2, 1, "test", "test", "09121", 12);
	}

}
