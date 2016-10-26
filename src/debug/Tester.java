package debug;

import java.util.Calendar;
import java.util.Date;

import mainpackage.Helper;
import mainpackage.Webservices;
import persindatepicker.PersianCalendar;

public class Tester {

	public static void main(String[] args) {
		mainpackage.Webservices ws = new Webservices();
//		ws.reserveForGuest("khorshid", "123", 7, 0, 3, 1, "Ù…Ø±ÛŒÙ…", "Ù„Ø·Ù�Ø³", "09356214578", 78);
//		ws.getTaskGroups("khorshid", "123", 1);
//		ws.addTask("khorshid", "123", 1, "تست", 2, 2000);
//		ws.setGalleryPic("khorshid", "123", 1, "salam sosis", "salam sosis");
//		ws.deleteFromGallery("khorshid", "123", 1, 7);
//		ws.deleteFromGallery("khorshid", "123", 1, 6);
//		ws.updateTask("khorshid", "123", 1, 3, "تست", 19000);
//		ws.getTodayPatient("khorshid", "123", 1);
//		ws.addSecretaryToOffice("khorshid", "123", 1, "dsfvwr");
		ws.getAllGalleyPicId2("drmostafavi", "123456", 1);
	}
	
	public static void sendMessages(Webservices ws) {
		String[] receiver = new String[3];
		receiver[0] = "bahar";
		receiver[1] = "ahodasht";
		receiver[2] = "ahodasht";
		
		String message = "Ø³Ù„Ø§Ù… Ø¨Ø± Ø´Ù…Ø§ Ú©Ù‡ Ø§Ø² Ø§ÛŒÙ† Ø§Ù¾Ù„ÛŒÚ©ÛŒØ´Ù† Ø§Ø³ØªÙ�Ø§Ø¯Ù‡ Ù…ÛŒ Ú©Ù†ÛŒØ¯. Ø§Ù…ÛŒØ¯ÙˆØ§Ø±ÛŒÙ… Ø§ÛŒÙ† Ø¨Ø±Ù†Ø§Ù…Ù‡ Ø¨ØªÙˆØ§Ù†Ø¯ Ù‚Ø¯Ù… Ù‡Ø± Ú†Ù†Ø¯ Ú©ÙˆÚ†Ú©ÛŒ Ø¯Ø± Ø¨Ù‡Ø¨ÙˆØ¯ Ú©Ø§Ø±Ú©Ø±Ø¯ Ù…Ø·Ø¨ Ø´Ù…Ø§ Ø¨Ø±Ø¯Ø§Ø±Ø¯. Ø¯Ø± Ø¹ÛŒÙ† Ø­Ø§Ù„ Ø¨ÛŒÙ…Ø§Ø±Ø§Ù† Ù…Ø­ØªØ±Ù… Ø¨ØªÙˆØ§Ù†Ù†Ø¯ Ø¨Ø§ Ø³Ø§Ø¯Ú¯ÛŒ Ùˆ Ø³Ù‡ÙˆÙ„Øª Ù‡Ø± Ú†Ù‡ Ø¨Ù‡ØªØ± Ø§Ù‚Ø¯Ø§Ù… Ø¨Ù‡ Ù†ÙˆØ¨Øª Ú¯ÛŒØ±ÛŒ Ù†Ù…ÙˆØ¯Ù‡ Ùˆ Ø²Ù…Ø§Ù† Ù‡Ø±Ú†Ù†Ø¯ Ú©Ù…ØªØ±ÛŒ Ø¯Ø± Ù…Ø·Ø¨ Ù¾Ø²Ø´Ú© Ù…Ù†ØªØ¸Ø± Ø¨Ù…Ø§Ù†Ù†Ø¯. \n Ùˆ Ù…Ù† Ø§Ù„Ù„Ù‡ ØªÙˆÙ�ÛŒÙ‚\n Ú¯Ø±ÙˆÙ‡ Ù…Ø¯ÛŒØ±ÛŒØª Ù¾Ø²Ø´Ú©ÛŒØ§Ø±";
		ws.sendMessageBatch("bahar", "123", 1, receiver, new String[3], "Hello 8", message);
		
	}

	public static void addSomeTurn(Webservices ws){
		Date date = new Date(); 
		Calendar cal = Calendar.getInstance();
		String persianDate;
		for(int i = 0; i < 10; i++){
			cal.setTime(date);
			cal.add(Calendar.DATE, 1); // add 10 days
			date = cal.getTime();
			persianDate = Helper.getShortDate(date);
			ws.addTurn("bahar", "123", 1, persianDate, 8, 0, 240, 10);
			ws.addTurn("bahar", "123", 1, persianDate, 16, 30, 180, 6);
		}
	}

	public static void addTurnBatch(){
		Date d = new Date();
		PersianCalendar cal1 = new PersianCalendar(d.getTime());
		String startDate = cal1.getPersianShortDate();
		cal1.add(PersianCalendar.MONTH, 1);
		String endDate = cal1.getPersianShortDate();
		
		Webservices ws = new Webservices();
		
		ws.addTurnByDate("bahar", "123", 1, startDate, endDate, 8, 30, 210, 6, "01234");
	}
	
	public static void testCalendar(){
		PersianCalendar cal = new PersianCalendar(new Date().getTime());
		System.out.println(cal.getPersianLongDate());
		cal.add(PersianCalendar.DAY_OF_MONTH, 24);
		System.out.println(cal.getPersianLongDate());
		PersianCalendar cal2 = new PersianCalendar();
		System.out.println(cal2.getPersianDay());
	}
}
