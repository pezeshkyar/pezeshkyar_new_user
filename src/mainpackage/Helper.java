package mainpackage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import constant.Constants;
import persindatepicker.PersianCalendar;
import primitives.Info_Reservation;
import primitives.Office;
import primitives.Rturn;
import primitives.Turn;
import primitives.User;
import primitives.Version;

public class Helper {

	public static String getOfficeType() {
		String res;
		switch (Constants.CURRENT_VERSION) {
		case Version.ARAYESHYAR:
			res = "\u0622\u0631\u0627\u06cc\u0634\u06af\u0627\u0647";
			break;
		case Version.PIRAYESHYAR:
			res = "\u067e\u06cc\u0631\u0627\u06cc\u0634\u06af\u0627\u0647";
			break;
		case Version.PEZESHKYAR:
			res = "\u0645\u0637\u0628";
			break;
		default:
			res = "\u062f\u0641\u062a\u0631";
			break;
		}
		return res;
	}

	public static String getWorkerTitle() {
		String res;
		switch (Constants.CURRENT_VERSION) {
		case Version.ARAYESHYAR:
			res = "\u0622\u0631\u0627\u06cc\u0634\u06af\u0631";
			break;
		case Version.PIRAYESHYAR:
			res = "\u067e\u06cc\u0631\u0627\u06cc\u0634\u06af\u0631";
			break;
		case Version.PEZESHKYAR:
			res = "\u067e\u0632\u0634\u06a9";
			break;
		default:
			res = "\u0645\u0633\u0626\u0648\u0644 "
					+ "\u0645\u0631\u0628\u0648\u0637\u0647";
			break;
		}
		return res;

	}

	public static String getAssistantTitle() {
		String res;
		switch (Constants.CURRENT_VERSION) {
		case Version.ARAYESHYAR:
			res = "\u062f\u0633\u062a\u06cc\u0627\u0631 "
					+ "\u0622\u0631\u0627\u06cc\u0634\u06af\u0631";
			break;
		case Version.PIRAYESHYAR:
			res = "\u062f\u0633\u062a\u06cc\u0627\u0631 "
					+ "\u067e\u06cc\u0631\u0627\u06cc\u0634\u06af\u0631";
			break;
		case Version.PEZESHKYAR:
			res = "\u0645\u0646\u0634\u06cc \u0645\u0637\u0628";
			break;
		default:
			res = "\u062f\u0633\u062a\u06cc\u0627\u0631";
			break;
		}
		return res;

	}

	public static String getString(byte[] bytes) {
		if (bytes == null)
			return null;
		// return java.util.Base64.getEncoder().encodeToString(bytes);
		return org.apache.axis.encoding.Base64.encode(bytes);
	}

	public static byte[] getBytes(String str) {
		if (str == null || str.isEmpty())
			return null;
		// return java.util.Base64.getDecoder().decode(str);
		return org.apache.axis.encoding.Base64.decode(str);
	}

	public static String getTodayShortDate() {
		Date date = new Date();
		PersianCalendar cal = new PersianCalendar(date.getTime());

		return cal.getPersianShortDate();
	}

	public static String getShortDate(Date d) {
		PersianCalendar cal = new PersianCalendar(d.getTime());
		return cal.getPersianShortDate();
	}

	public static String getLongDate(Date d) {
		PersianCalendar cal = new PersianCalendar(d.getTime());
		return cal.getPersianLongDate();
	}

	public static String getShortDateAfterSomeDay(Date now, int someDay) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DATE, someDay); // add some days

		Date then = cal.getTime();
		PersianCalendar cal2 = new PersianCalendar(then.getTime());
		return cal2.getPersianShortDate();
	}

	public static String getLongDateAfterSomeDay(Date now, int someDay) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DATE, someDay); // add some days

		Date then = cal.getTime();
		PersianCalendar cal2 = new PersianCalendar(then.getTime());
		return cal2.getPersianLongDate();
	}

	public static String convertShortDateToLong(String shortDate) {
		PersianCalendar cal = getCalendarFromShortDate(shortDate);
		if (cal == null)
			return "Error";
		return cal.getPersianLongDate();
	}

	public static PersianCalendar
			getCalendarFromShortDate(String shortDate) {
		String[] splitted = shortDate.split("/");
		if (splitted.length != 3)
			return null;
		int year = Integer.parseInt(splitted[0]);
		int month = Integer.parseInt(splitted[1]);
		int day = Integer.parseInt(splitted[2]);

		PersianCalendar cal = new PersianCalendar();
		cal.setPersianDate(year, month, day);
		return cal;
	}

	public static boolean isAfterToday(String date) {
		PersianCalendar ps = getCalendarFromShortDate(date);
		PersianCalendar today = new PersianCalendar(new Date().getTime());
		return (ps.after(today));
	}

	public static boolean isBeforeToday(String date) {
		PersianCalendar ps = getCalendarFromShortDate(date);
		PersianCalendar today = new PersianCalendar(new Date().getTime());
		return (ps.before(today));
	}

	public static boolean checkDateBoundary(String fromDate, String toDate) {
		PersianCalendar cal1 = Helper.getCalendarFromShortDate(fromDate);
		PersianCalendar cal2 = Helper.getCalendarFromShortDate(toDate);
		if (cal2.before(cal1))
			return false;
		return true;
	}

	public static void setTurnValues(Turn t, Rturn r) {
		t.date = r.date;
		t.hour = r.hour;
		t.min = r.min;
		t.duration = r.duration;
		t.capacity = r.capacity;
	}

	public static String getCurrentTime() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	public static Vector<String> removeDuplicates(String[] input) {
		Vector<String> output = new Vector<String>();
		if (input.length > 1) {
			output.addElement(input[0]);
			for (int i = 0; i < input.length; i++) {
				boolean sw = false;
				for (String s : output) {
					if (s != null && s.equals(input[i])) {
						sw = true;
						break;
					}
				}
				if (!sw)
					output.addElement(input[i]);
			}
		}
		return output;
	}

	public static void sendSMS(	int officeId, int senderId,
								Vector<String> receiverPhones,
								String subject, String message,
								String dateStr, String timeStr) {

	}

	public static String getTime(int hour, int min, int duration) {
		String res;
		res = get2digitNumber(hour) + ":" + get2digitNumber(min);
		hour += (duration / 60);
		min += (duration % 60);
		res += " \u062a\u0627 " + get2digitNumber(hour) + ":"
				+ get2digitNumber(min);
		return res;
	}

	public static String get2digitNumber(int a) {
		String str;
		str = (a < 0) ? "0" : "";
		str += String.valueOf(a);
		return str;
	}

	public static void sendCancelationMessage(	Database db, int cancelerId,
												Info_Reservation info) {
		try {
			User canceler = db.getUserInfo(cancelerId);
			// User patient = db.getUserInfo(info.patientId);
			Office office = db.getOfficeInfo(info.officeId);
			int doctorId = db.getUserId(office.doctorUsername);
			int getterId = db.getUserId(info.username);
			String subject = "\u0644\u063a\u0648 \u0646\u0648\u0628\u062a"; // laghve
																			// nobat
			String msg =
					"\u0646\u0648\u0628\u062a \u0634\u0645\u0627 \u062f\u0631 "
							+ getOfficeType() + " "; // nobate shoma dar matabe
			msg += office.doctorName + " " + office.doctorLastName + "( ";
			msg += office.spec + " / " + office.subSpec + " )";
			msg += "\u062f\u0631 \u062a\u0627\u0631\u06cc\u062e "; // dar
																	// tarikhe
			msg += info.longDate + " ";
			msg += " \u0633\u0627\u0639\u062a "; // (saat)
			msg += info.time;
			msg += " \u062a\u0648\u0633\u0637 "; // tavasote
			if (cancelerId == info.patientId) {
				msg += " \u062e\u0648\u062f\u062a\u0627\u0646 "; // khodetan
			} else if (cancelerId == doctorId) {
				msg += " " + getWorkerTitle() + " "; // pezeshk
			} else {
				msg += " " + getAssistantTitle() + " "; // monshie matab
				msg += "( " + canceler.name + " " + canceler.lastname + " )";
			}
			msg += " \u0644\u063a\u0648 \u0634\u062f ";

			db.sendMessage(info.officeId, cancelerId, info.patientId,
					subject, msg, getTodayShortDate(), getCurrentTime());
			if (getterId != info.patientId && cancelerId != getterId)
				db.sendMessage(info.officeId, cancelerId, getterId, subject,
						msg, getTodayShortDate(), getCurrentTime());
		} catch (Exception e) {

		}

	}

	public static void sendReservationMessage(	Database db, int reserverId,
												Info_Reservation info) {
		try {
			User reserver = db.getUserInfo(reserverId);
			Office office = db.getOfficeInfo(info.officeId);
			int doctorId = db.getUserId(office.doctorUsername);

			String subject = "\u062f\u0631\u06cc\u0627\u0641\u062a "// daryafte
					+ "\u0646\u0648\u0628\u062a"; // nobat
			String msg =
					"\u0628\u0631\u0627\u06cc \u0634\u0645\u0627 \u062f\u0631 "
							+ getOfficeType() + " "; // baraye shoma dar matabe
			msg += office.doctorName + " " + office.doctorLastName + "( ";
			msg += office.spec + " / " + office.subSpec + " )";
			msg += "\u062f\u0631 \u062a\u0627\u0631\u06cc\u062e "; // dar
																	// tarikhe
			msg += info.longDate + " ";
			msg += " \u0633\u0627\u0639\u062a "; // (saat)
			msg += info.time;
			msg += " \u062a\u0648\u0633\u0637 "; // tavasote
			if (reserverId == info.patientId) {
				msg += " \u062e\u0648\u062f\u062a\u0627\u0646 "; // khodetan
			} else if (reserverId == doctorId) {
				msg += " " + getWorkerTitle() + " "; // pezeshk
			} else {
				msg += " " + getAssistantTitle() + " "; // monshie matab
				msg += "( " + reserver.name + " " + reserver.lastname + " )";
			}
			msg += " \u06cc\u06a9 \u0646\u0648\u0628\u062a "// yek nobat
					+ "\u0631\u0632\u0631\u0648 \u0634\u062f "; // reserve shod

			db.sendMessage(info.officeId, reserverId, info.patientId,
					subject, msg, getTodayShortDate(), getCurrentTime());
		} catch (Exception e) {

		}

	}

	private static String getMessageNotPermittedTask(	String task,
														String change) {
		String str;
		str = "\u0634\u0645\u0627 "; // shoma
		str += "\u0627\u062c\u0627\u0632\u0647 "; // ejazeh
		str += change;
		// str += "\u062a\u063a\u06cc\u06cc\u0631 \u0646\u0627\u0645 ";
		// //Taghhire nam
		str += "\u0627\u06cc\u0646 "; // in
		str += task;
		// str += "\u06af\u0631\u0648\u0647 ";// gorooh
		str += "\u0631\u0627 ";// ra
		str += "\u0646\u062f\u0627\u0631\u06cc\u062f ";// nadarid
		str += "\u0632\u06cc\u0631\u0627 ";// zira
		str += "\u0642\u0628\u0644\u0627\u064b ";// ghablan
		str += "\u062d\u062f\u0627\u0642\u0644 \u06cc\u06a9 ";// hadeaghal yek
		str += "\u0646\u0641\u0631 ";// nafar
		str += "\u062f\u0631 \u0627\u06cc\u0646 ";// dar in
		str += task;
		// str += "\u06af\u0631\u0648\u0647 ";// gorooh
		str += "\u0646\u0648\u0628\u062a ";// nobat
		str += "\u0631\u0632\u0631\u0648 ";// reserve
		str += "\u06a9\u0631\u062f\u0647 ";// kardeh
		str += "\u0627\u0633\u062a";// ast
		return str;
	}

	public static String getMessageNotPermittedChangeTaskGroup() {
		String str = getMessageNotPermittedTask(
				"\u06af\u0631\u0648\u0647 "/* gorooh */,
				"\u062a\u063a\u06cc\u06cc\u0631 \u0646\u0627\u0645 "/*
																	 * Taghhire
																	 * nam
																	 */);
		return str;
	}

	public static String getMessageNotPermittedDeleteTaskGroup() {
		String str = getMessageNotPermittedTask(
				"\u06af\u0631\u0648\u0647 "/* gorooh */,
				"\u067e\u0627\u06a9 \u06a9\u0631\u062f\u0646 "/*
																 * paak kardan
																 */);
		return str;
	}

	public static String getMessageNotPermittedChangeTask() {
		String str = getMessageNotPermittedTask(
				"\u0646\u062f\u0627\u0631\u06cc\u062f "/* amaliat */,
				"\u062a\u063a\u06cc\u06cc\u0631 \u0646\u0627\u0645 "/*
																	 * Taghhire
																	 * nam
																	 */);
		return str;
	}

	public static String getMessageNotPermittedDeleteTask() {
		String str = getMessageNotPermittedTask(
				"\u0646\u062f\u0627\u0631\u06cc\u062f "/* amaliat */,
				"\u067e\u0627\u06a9 \u06a9\u0631\u062f\u0646 "/*
																 * paak kardan
																 */);
		return str;
	}

	public static String getMessageNotPermittedAcces() {
		String str = "\u0634\u0645\u0627 " // shoma
				+ "\u0645\u062c\u0648\u0632 " // ejazeh
				+ "\u062f\u0633\u062a\u0631\u0633\u06cc " // dastresi
				+ "\u0628\u0647 \u0627\u06cc\u0646 " // be in
				+ "\u0628\u062e\u0634 \u0631\u0627 " // bakhsh ra
				+ "\u0646\u062f\u0627\u0631\u06cc\u062f";// nadarid
		return str;
	}

	public static String getMessageUnknownError() {
		String res;
		// khataye gheire montazere da samte server pish amade ast
		res = "\u062e\u0637\u0627\u06cc \u063a\u06cc\u0631 "
				+ "\u0645\u0646\u062a\u0638\u0631\u0647 "
				+ "\u062f\u0631 \u0633\u0645\u062a "
				+ "\u0633\u0631\u0648\u0631 \u067e\u06cc\u0634 "
				+ "\u0622\u0645\u062f\u0647 \u0627\u0633\u062a";
		return res;
	}

	public static String getMessageUserNameNotAvailabe() {
		// name karbarie Entekhabi Mojaz Nist
		return "\u0646\u0627\u0645 \u06a9\u0627\u0631\u0628\u0631\u06cc "
				+ "\u0627\u0646\u062a\u062e\u0627\u0628\u06cc "
				+ "\u0645\u062c\u0627\u0632 \u0646\u06cc\u0633\u062a";
	}

	public static String getMessageIncorrectUserPass() {
		// name karbari ya gozarvazheh sahih nist
		return "\u0646\u0627\u0645 \u06a9\u0627\u0631\u0628\u0631\u06cc "
				+ "\u06cc\u0627 \u06af\u0630\u0631\u0648\u0627\u0698\u0647 "
				+ "\u0627\u0634\u062a\u0628\u0627\u0647 \u0627\u0633\u062a";
	}

	public static String getMessageIncorrectOfficeId() {
		// code matabe vared shode nadorost ast
		return "\u06a9\u062f \u0645\u0637\u0628 \u0648\u0627\u0631\u062f "
				+ "\u0634\u062f\u0647 \u0646\u0627 \u062f\u0631\u0633\u062a "
				+ "\u0627\u0633\u062a\u002e";
	}

	public static String getMessageInvalidParam() {
		// khta dar parametrhaye voroodi
		return "\u062e\u0637\u0627 \u062f\u0631 "
				+ "\u067e\u0627\u0631\u0627\u0645\u062a\u0631\u0647\u0627\u06cc"
				+ " \u0648\u0631\u0648\u062f\u06cc\u002e";
	}
}
