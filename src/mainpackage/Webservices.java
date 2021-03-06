package mainpackage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

import persindatepicker.PersianCalendar;
import primitives.AppInfo;
import primitives.City;
import primitives.Info_Message;
import primitives.Info_Message1;
import primitives.Info_Patient;
import primitives.Info_Reservation;
import primitives.Info_Reservation2;
import primitives.Info_User;
import primitives.Info_patientFile;
import primitives.Office;
import primitives.Office2;
import primitives.Payment;
import primitives.PhotoDesc;
import primitives.Province;
import primitives.Question;
import primitives.Reply;
import primitives.Reservation2;
import primitives.Reservation4;
import primitives.Reservation_new;
import primitives.Role;
import primitives.Spec;
import primitives.Subspec;
import primitives.Task;
import primitives.TaskGroup;
import primitives.Ticket;
import primitives.TicketMessage;
import primitives.TicketSubject;
import primitives.Turn;
import primitives.User;
import primitives.UserTurn;

public class Webservices {
	public static final String OK_MESSAGE = "OK";

	///////////////////////// public services

	public Province[] getProvince() {
		Database db = new Database();
		Vector<Province> vec = db.getAllProvinceNames();
		Province[] res = new Province[vec.size()];
		for (int i = 0; i < vec.size(); i++) {
			res[i] = vec.elementAt(i);
		}

		return res;
	}

	public City[] getCityOfProvince(int provinceID) {
		Database db = new Database();
		Vector<City> vec = db.getCityByProvince(provinceID);
		City[] res = new City[vec.size()];
		for (int i = 0; i < vec.size(); i++) {
			res[i] = vec.elementAt(i);
		}

		return res;
	}

	public City[] getCity() {
		Database db = new Database();
		Vector<City> vec = db.getAllCityNames();
		City[] res = new City[vec.size()];
		for (int i = 0; i < vec.size(); i++) {
			res[i] = vec.elementAt(i);
		}

		return res;
	}

	public Spec[] getSpec() {
		Database db = new Database();
		Vector<Spec> vec = db.getAllSpec();
		Spec[] res = new Spec[vec.size()];
		for (int i = 0; i < vec.size(); i++) {
			res[i] = vec.elementAt(i);
		}

		return res;
	}

	public Subspec[] getSubSpec(int specId) {
		Database db = new Database();
		Vector<Subspec> vec = db.getSubspec(specId);
		Subspec[] res = new Subspec[vec.size()];
		for (int i = 0; i < vec.size(); i++) {
			res[i] = vec.elementAt(i);
		}

		return res;

	}

	public Info_User[] searchUser(	String username, String name,
									String lastName, String mobileNo,
									int officeId) {
		Info_User[] result = null;
		Vector<Info_User> vec;
		Database db = new Database();

		if (db.openConnection()) {
			try {
				vec = db.searchUserWithoutPic(username, name, lastName,
						mobileNo, officeId);
				result = new Info_User[vec.size()];
				for (int i = 0; i < vec.size(); i++) {
					result[i] = vec.elementAt(i);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		return result;
	}

	/////////////////////////////// user management services

	public boolean isUserNameAvailable(String username) {
		Database db = new Database();
		boolean res;
		if (db.openConnection()) {
			res = db.isUsernameAvailable(username);
			db.closeConnection();
		} else
			res = false;
		return res;
	}

	public String register(	String name, String lastname, String mobileno,
							String username, String password, int cityid,
							String pic, String email) {
		String res = OK_MESSAGE;
		Database db = new Database();
		if (!db.openConnection())
			return Helper.getMessageUnknownError();
		if (!db.isUsernameAvailable(username)) {
			db.closeConnection();
			return Helper.getMessageUserNameNotAvailabe();
		}

		byte[] picbyte = null;
		if (pic != null && pic.length() > 0) {
			picbyte = Helper.getBytes(pic);
		}
		try {
			db.register(name, lastname, mobileno, username, password, cityid,
					picbyte, email);
		} catch (SQLException e) {
			res = Helper.getMessageUnknownError();
		}
		db.closeConnection();
		return res;
	}

	public String login(String username, String password) {
		Database db = new Database();
		String res = Helper.getMessageUnknownError();

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password))
					res = OK_MESSAGE;
				else
					res = Helper.getMessageIncorrectUserPass();
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public int login2(String username, String password, int officeId) {
		Database db = new Database();
		int role = 0;
		if (!db.openConnection())
			return role;

		try {
			if (db.checkUserPass(username, password)) {
				role = db.getPermissionOnOffice(officeId, username);
			}
		} catch (Exception ex) {
			role = 0;
		}
		db.closeConnection();
		return role;
	}

	public int getRoleInAll(String username, String password) {
		Database db = new Database();
		int role;
		try {
			if (db.openConnection()) {
				role = db.getRoleInAll(username, password);

			} else {
				role = Role.none;
			}
		} catch (SQLException e) {
			role = Role.none;
		}
		return role;
	}

	// Remove Office id
	public String updateUserInfo(	String username, String password,
									String name, String lastname,
									String mobileno, int cityid) {
		String res = OK_MESSAGE;
		Database db = new Database();
		if (!db.openConnection()) {
			res = Helper.getMessageUnknownError();
		} else {
			try {
				if (!db.checkUserPass(username, password)) {
					res = Helper.getMessageIncorrectUserPass();
				} else {
					int userId = db.getUserId(username);
					if (!db.updateUserInfo(userId, name, lastname, mobileno,
							cityid)) {
						res = Helper.getMessageUnknownError();
					}
				}
			} catch (SQLException e) {
				res = Helper.getMessageUnknownError();
			}
			db.closeConnection();
		}
		return res;
	}

	public String updateUserInfo3(	String username, String password,
									String name, String lastname,
									String mobileno, int cityid,
									String newPassword, String email) {
		String res = Helper.getMessageUnknownError();
		Database db = new Database();

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					if (db.updateUserInfo(userId, name, lastname, mobileno,
							cityid, newPassword, email)) {
						res = OK_MESSAGE;
					}
				} else {
					res = Helper.getMessageIncorrectUserPass();
				}

			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public boolean updateUserPic2(	String username, String password,
									String pic) {
		boolean result = false;
		Database db = new Database();
		if (!db.openConnection()) {
			return false;
		}
		try {
			if (!db.checkUserPass(username, password)) {
				result = false;
			} else {
				byte[] imageAsBytes = Helper.getBytes(pic);
				int userId = db.getUserId(username);
				result = db.updateUserPic(userId, imageAsBytes);
				result = true;
			}
		} catch (SQLException e) {
			result = false;
		} finally {
			db.closeConnection();
		}
		return result;
	}

	public boolean updateUserPassword(	String username, String password,
										String newPassword) {
		boolean res;
		Database db = new Database();
		if (!db.openConnection())
			return false;
		try {
			if (!db.checkUserPass(username, password))
				res = false;
			else {
				int userId = db.getUserId(username);
				res = db.updateUserPassword(userId, newPassword);
			}
		} catch (SQLException e) {
			res = false;
		} finally {
			db.closeConnection();
		}
		return res;
	}

	public String getUserPic(String username, String password) {
		Database db = new Database();
		byte[] res = null;
		if (!db.openConnection())
			return null;
		try {
			if (!db.checkUserPass(username, password))
				res = null;
			int userId = db.getUserId(username);
			res = db.getUserPic(userId);
		} catch (SQLException e) {
			res = null;
		} finally {
			db.closeConnection();
		}
		String ret = Helper.getString(res);
		return ret;
	}

	public User getUserInfo(String username, String password, int officeId) {
		return getUserInfoHelper(username, password, officeId, true);
	}

	public User getUserInfoWithoutPic(	String username, String password,
										int officeId) {
		return getUserInfoHelper(username, password, officeId, false);
	}

	private User getUserInfoHelper(	String username, String password,
									int officeId, boolean picSw) {
		Database db = new Database();
		User user = User.getErrorUser();
		if (!db.openConnection())
			return user;
		try {
			if (!db.checkUserPass(username, password)) {
				user.name = Helper.getMessageIncorrectUserPass();
				user.lastname = "Username or Password is incorrect";
			} else {
				int userId = db.getUserId(username);
				if (picSw) {
					user = db.getUserInfo(userId);
				} else {
					user = db.getUserInfoWithoutPic(userId);
				}
				user.role = db.getPermissionOnOffice(officeId, username);
			}
		} catch (SQLException e) {
			return user;
		}
		return user;
	}

	/////////////////////////////////////// Office Management Services

	public int registerOffice(	String username, String password, int cityid,
								int spec, int subspec, String address,
								String tellNo, double latitude,
								double longitude, int timeQuantum,
								String biography) {
		Database db = new Database();
		int id = 0;
		if (db.openConnection()) {
			try {
				if (db.checkMasterPassword(username, password)
						|| db.isHaveSupportPermission(username, password)) {
					id = db.InsertInOffice(spec, subspec, address, tellNo,
							cityid, latitude, longitude, timeQuantum,
							biography);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.closeConnection();
			}
		}
		return id;
	}

	public boolean updateOfficeInfo(String username, String password,
									int officeId, int cityId, int spec,
									int subspec, String address,
									String tellNo, String biography) {
		Database db = new Database();
		boolean ret = false;
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					db.updateOffice(officeId, spec, subspec, address, tellNo,
							cityId);
					db.addBiography(officeId, biography);
					ret = true;
				}
			} catch (SQLException e) {
				ret = false;
			} finally {
				db.closeConnection();
			}
		}
		return ret;
	}

	public int[] getDoctorOffice(String doctorUsername) {
		Database db = new Database();

		if (!db.openConnection())
			return null;
		try {
			Vector<Integer> vec = db.getDoctorOffice(doctorUsername);
			int[] res = new int[vec.size()];
			for (int i = 0; i < vec.size(); i++) {
				res[i] = vec.get(i);
			}
			return res;
		} catch (SQLException e) {
			return null;
		}
	}

	public String getDoctorPic(	String username, String password,
								int officeId) {
		Database db = new Database();
		byte[] res = null;
		if (!db.openConnection())
			return null;
		try {
			if (!db.checkUserPass(username, password))
				res = null;
			res = db.getDrPic(officeId);
		} catch (SQLException e) {
			res = null;
		} finally {
			db.closeConnection();
		}
		String ret = Helper.getString(res);
		return ret;
	}

	public boolean updateOfficeLocation(String username, String password,
										int officeId, String latitude,
										String longitude) {
		Database db = new Database();
		double lat, lng;
		boolean ret = false;
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					lat = Double.parseDouble(latitude);
					lng = Double.parseDouble(longitude);
					db.updateOffice(officeId, lat, lng);
					ret = true;
				}
			} catch (SQLException e) {
			} finally {
				db.closeConnection();
			}
		}
		return ret;
	}

	public boolean addBiography(String username, String password,
								int officeId, String biography) {
		Database db = new Database();
		try {
			if (!db.openConnection())
				return false;
			if (!db.isHaveSecretaryPermission(username, password, officeId))
				return false;

			db.addBiography(officeId, biography);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public String getBiography(	String username, String password,
								int officeId) {
		Database db = new Database();
		String str = "";
		try {
			if (!db.openConnection())
				return str;
			if (!db.isHavePatientPermission(username, password, officeId))
				return str;

			str = db.getBiography(officeId);
		} catch (Exception e) {
		}
		return str;
	}

	public Task[] getAllTasks(	String username, String password,
								int officeId) {
		Task[] res = null;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					Vector<Task> vec = db.getAllTasks(officeId);
					res = new Task[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public TaskGroup[] getTaskGroups(	String username, String password,
										int officeId) {
		TaskGroup[] res = null;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					Vector<TaskGroup> vec = db.getTaskGroups(officeId);
					res = new TaskGroup[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public Task[] getTasks(	String username, String password, int officeId,
							int taskGroupId) {
		Task[] res = null;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					Vector<Task> vec = db.getAllTasks(officeId, taskGroupId);
					res = new Task[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public int addTaskGroup(String username, String password, int officeId,
							String taskGroupName) {
		Database db = new Database();
		int id = 0;
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					id = db.addTaskGroup(taskGroupName, officeId);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return id;
	}

	public String addTaskGroup2(String username, String password,
								int officeId, String taskGroupName) {
		Database db = new Database();
		String res = "First";
		int id = 0;
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					id = db.addTaskGroup(taskGroupName, officeId);
					res = OK_MESSAGE;
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				res = e.getMessage();
			} finally {
				db.closeConnection();
			}
		}
		return res + ", id = " + id;
	}

	// used
	public String updateTaskGroup(	String username, String password,
									int officeId, int taskGroupId,
									String taskGroupName) {
		String res;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					if (!db.isAnyoneReserveTaskGroup(taskGroupId)) {
						db.updateTaskGroup(taskGroupId, taskGroupName);
						res = OK_MESSAGE;
					} else {
						res = Helper.getMessageNotPermittedChangeTaskGroup();
					}
				} else {
					res = Helper.getMessageNotPermittedAcces();
				}
			} catch (SQLException e) {
				res = Helper.getMessageUnknownError();
			} finally {
				db.closeConnection();
			}
		} else {
			res = Helper.getMessageUnknownError();
		}
		return res;
	}

	// used
	public String deleteTaskGroup(	String username, String password,
									int officeId, int taskGroupId) {
		String res;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					if (!db.isAnyoneReserveTaskGroup(taskGroupId)) {
						db.deleteTaskGroup(taskGroupId);
						res = OK_MESSAGE;
					} else {
						res = Helper.getMessageNotPermittedDeleteTaskGroup();
					}
				} else {
					res = Helper.getMessageNotPermittedAcces();
				}
			} catch (SQLException e) {
				res = Helper.getMessageUnknownError();
			} finally {
				db.closeConnection();
			}
		} else {
			res = Helper.getMessageUnknownError();
		}
		return res;
	}

	// used
	public int addTask(	String username, String password, int officeId,
						String name, int taskGroupId, int price) {
		int id = 0;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					id = db.addTask(name, officeId, taskGroupId, price);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return id;
	}

	// used
	public String updateTaskPrice(	String username, String password,
									int officeId, int taskId, int price) {
		String res;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					db.updateTaskPrice(taskId, price);
					res = OK_MESSAGE;
				} else {
					res = Helper.getMessageNotPermittedAcces();
				}
			} catch (SQLException e) {
				res = Helper.getMessageUnknownError();
			} finally {
				db.closeConnection();
			}
		} else {
			res = Helper.getMessageUnknownError();
		}
		return res;
	}

	public String updateTaskName(	String username, String password,
									int officeId, int taskId,
									String taskName) {
		String res;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					if (!db.isAnyoneReserveTask(taskId)) {
						db.updateTaskName(taskId, taskName);
						res = OK_MESSAGE;
					} else {
						res = Helper.getMessageNotPermittedChangeTask();
					}
				} else {
					res = Helper.getMessageNotPermittedAcces();
				}
			} catch (SQLException e) {
				res = Helper.getMessageUnknownError();
			} finally {
				db.closeConnection();
			}
		} else {
			res = Helper.getMessageUnknownError();
		}
		return res;
	}

	public String deleteTask(	String username, String password, int officeId,
								int taskId) {
		String res;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					if (!db.isAnyoneReserveTask(taskId)) {
						db.deleteTask(taskId);
						res = OK_MESSAGE;
					} else {
						res = Helper.getMessageNotPermittedDeleteTask();
					}
				} else {
					res = Helper.getMessageNotPermittedAcces();
				}
			} catch (SQLException e) {
				res = Helper.getMessageUnknownError();
			} finally {
				db.closeConnection();
			}
		} else {
			res = Helper.getMessageUnknownError();
		}
		return res;
	}

	///////////////////////////////////// secretary management services

	public boolean addSecretaryToOffice(String username, String password,
										int officeId, String secretary) {
		boolean ret = false;
		Database db = new Database();
		int secretaryId = db.getUserId(secretary);
		try {
			if (db.openConnection()) {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					db.InsertInSecretary(officeId, secretaryId);
					ret = true;
				}
			}
		} catch (SQLException e) {
			ret = false;
		} finally {
			db.closeConnection();
		}
		return ret;
	}

	// used
	public User addSecretaryToOffice2(	String username, String password,
										int officeId, String secretary) {
		User ret = User.getErrorUser();
		Database db = new Database();
		try {
			if (db.openConnection()) {
				int secretaryId = db.getUserId(secretary);
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					ret = db.InsertInSecretary2(officeId, secretaryId);
					ret.role = Role.secretary;
				}
			}
		} catch (Exception e) {
		} finally {
			db.closeConnection();
		}
		return ret;
	}

	public boolean
			removeSecretaryFromOffice(	String username, String password,
										int officeId, String secretary) {
		boolean ret = false;
		Database db = new Database();
		try {
			if (db.openConnection()) {
				if (db.isHaveDoctorPermission(username, password,
						officeId)) {
					db.removeFromSecretary(officeId, secretary);
					ret = true;
				}
			}
		} catch (SQLException e) {
			ret = false;
		} finally {
			db.closeConnection();
		}
		return ret;
	}

	public Info_User[] getSecretaryInfo(String username, String password,
										int officeId) {
		Info_User[] result = null;
		Database db = new Database();

		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					Vector<Info_User> vec = db.getAllSecretary(officeId);
					result = new Info_User[vec.size()];
					for (int i = 0; i < vec.size(); i++) {
						result[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		return result;
	}

	/////////////////////////////////////////// turn management services

	public boolean addTurn(	String username, String password, int officeId,
							String date, int startHour, int startMin,
							int duration, int capacity) {
		Database db = new Database();
		if (capacity <= 0 || duration <= 0 || startHour <= 0
				|| startMin <= 0)
			return false;
		try {
			if (!db.openConnection())
				return false;
			if (!db.isHaveSecretaryPermission(username, password, officeId))
				return false;
			if (Helper.isBeforeToday(date))
				return false;

			Turn t = new Turn();
			t.officeId = officeId;
			t.capacity = capacity;
			t.date = date;
			t.duration = duration;
			t.hour = startHour;
			t.min = startMin;
			t.reserved = 0;

			addTurnWithoutId(db, t);
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	private void addTurnWithoutId(Database db, Turn t) throws SQLException {
		t.id = db.getMaxTurnId() + 1;
		db.addTurn(t);
	}

	public boolean addTurnByDate(	String username, String password,
									int officeId, String fromDate,
									String toDate, int hour, int min,
									int duration, int capacity,
									String dayOfWeek) {
		PersianCalendar cal1 = Helper.getCalendarFromShortDate(fromDate);
		PersianCalendar cal2 = Helper.getCalendarFromShortDate(toDate);
		Vector<Integer> days = new Vector<Integer>();
		Vector<Turn> vec = new Vector<Turn>();
		Database db = new Database();
		int firstId = 0;
		if (capacity <= 0 || duration <= 0 || hour < 0 || min < 0)
			return false;
		if (cal1 == null || cal2 == null)
			return false;
		if (Helper.isBeforeToday(fromDate))
			return false;
		if (cal1.after(cal2))
			return false;
		if (!db.openConnection())
			return false;
		firstId = db.getMaxTurnId() + 1;

		char[] temp = dayOfWeek.toCharArray();
		for (char ch : temp) {
			days.addElement(ch - '0');
		}
		while (cal2.after(cal1) || cal2.equals(cal1)) {
			int todayDayOfWeek = cal1.getPersianWeekDay();
			if (days.contains(todayDayOfWeek)) {
				cal1.getPersianLongDate();
				Turn turn = new Turn();
				turn.capacity = capacity;
				turn.date = cal1.getPersianShortDate();
				turn.duration = duration;
				turn.hour = hour;
				turn.min = min;
				turn.id = firstId++;
				turn.isReserved = false;
				turn.longDate = cal1.getPersianLongDate();
				turn.officeId = officeId;
				turn.reserved = 0;
				vec.addElement(turn);
			}
			cal1.add(PersianCalendar.DAY_OF_YEAR, 1);
		}

		boolean res = true;
		try {
			if (vec.size() > 0) {
				db.addTurnBatch(vec);

				res = true;
			} else {
				res = false;
			}
		} catch (SQLException e) {
			res = false;
		} finally {
			db.closeConnection();
		}
		return res;
	}

	public String addTurnByDate2(	String username, String password,
									int officeId, String fromDate,
									String toDate, int hour, int min,
									int duration, int capacity,
									String dayOfWeek) {
		PersianCalendar cal1 = Helper.getCalendarFromShortDate(fromDate);
		PersianCalendar cal2 = Helper.getCalendarFromShortDate(toDate);
		Vector<Integer> days = new Vector<Integer>();
		Vector<Turn> vec = new Vector<Turn>();
		Database db = new Database();
		int firstId = 0;
		String msg;
		if (capacity <= 0 || duration <= 0 || hour < 0 || min < 0) {
			msg = Helper.getMessageInvalidParam();
			// parametrhaye zaman va zarfiat ra barresi konid
			msg += "\u067e\u0627\u0631\u0627\u0645\u062a\u0631\u0647\u0627\u06cc "
					+ "\u0632\u0645\u0627\u0646 \u0648 \u0638\u0631\u0641\u06cc\u062a "
					+ "\u0631\u0627 \u0628\u0631\u0631\u0633\u06cc "
					+ "\u06a9\u0646\u06cc\u062f";
			return msg;
		}
		if (cal1 == null || cal2 == null) {
			msg = Helper.getMessageInvalidParam();
			// tarikh shoro va payan ra barresi konid
			msg += "\u062a\u0627\u0631\u06cc\u062e \u0634\u0631\u0648\u0639 "
					+ "\u0648 \u067e\u0627\u06cc\u0627\u0646 \u0631\u0627 "
					+ "\u0628\u0631\u0631\u0633\u06cc \u06a9\u0646\u06cc\u062f";
			return msg;
		}
		if (Helper.isBeforeToday(fromDate)) {
			msg = Helper.getMessageInvalidParam();
			// tarikh shoro nemitavanad ghabl az emrooz bashad
			msg += "\u062a\u0627\u0631\u06cc\u062e \u0634\u0631\u0648\u0639 "
					+ "\u0646\u0645\u06cc \u062a\u0648\u0627\u0646\u062f "
					+ "\u0642\u0628\u0644 \u0627\u0632 "
					+ "\u0627\u0645\u0631\u0648\u0632 \u0628\u0627\u0634\u062f";
			return msg;
		}
		if (cal1.after(cal2)) {
			msg = Helper.getMessageInvalidParam();
			// tarikh shoro va payan ra barresi konid
			msg += "\u062a\u0627\u0631\u06cc\u062e \u0634\u0631\u0648\u0639 "
					+ "\u0648 \u067e\u0627\u06cc\u0627\u0646 \u0631\u0627 "
					+ "\u0628\u0631\u0631\u0633\u06cc \u06a9\u0646\u06cc\u062f";
			return msg;
		}
		if (!db.openConnection()) {
			return Helper.getMessageUnknownError();
		}
		firstId = db.getMaxTurnId() + 1;

		char[] temp = dayOfWeek.toCharArray();
		for (char ch : temp) {
			days.addElement(ch - '0');
		}
		while (cal2.after(cal1) || cal2.equals(cal1)) {
			int todayDayOfWeek = cal1.getPersianWeekDay();
			if (days.contains(todayDayOfWeek)) {
				cal1.getPersianLongDate();
				Turn turn = new Turn();
				turn.capacity = capacity;
				turn.date = cal1.getPersianShortDate();
				turn.duration = duration;
				turn.hour = hour;
				turn.min = min;
				turn.id = firstId++;
				turn.isReserved = false;
				turn.longDate = cal1.getPersianLongDate();
				turn.officeId = officeId;
				turn.reserved = 0;
				vec.addElement(turn);
			}
			cal1.add(PersianCalendar.DAY_OF_YEAR, 1);
		}

		try {
			if (vec.size() > 0) {
				db.addTurnBatch(vec);

				msg = OK_MESSAGE;
			} else {
				// be dalile na-hamkhani rooz va tarikh, hich nobati ezefe
				// nashod
				msg = "\u0628\u0647 \u062f\u0644\u06cc\u0644 "
						+ "\u0646\u0627\u0647\u0645\u062e\u0648\u0627\u0646\u06cc "
						+ "\u0631\u0648\u0632 "
						+ "\u0648 \u062a\u0627\u0631\u06cc\u062e\u060c "
						+ "\u0647\u06cc\u0686 \u0646\u0648\u0628\u062a "
						+ "\u062c\u062f\u06cc\u062f\u06cc "
						+ "\u0627\u0636\u0627\u0641\u0647 \u0646\u0634\u062f\u002e";
			}
		} catch (SQLException e) {
			msg = Helper.getMessageUnknownError();
		} finally {
			db.closeConnection();
		}
		return msg;
	}

	public boolean removeTurn(	String username, String password, int officeId,
								int turnId) {
		boolean res = false;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					db.removeFromTurn(officeId, turnId);
					res = true;
				}
			} catch (SQLException e) {
				res = false;
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public Turn[] getAllTurn(	String username, String password, int officeId,
								String fromDate, String toDate) {
		Database db = new Database();
		Turn[] res = null;
		if (Helper.checkDateBoundary(fromDate, toDate)) {
			if (db.openConnection()) {
				Vector<Turn> vec =
						db.getTurn(username, officeId, fromDate, toDate);
				res = new Turn[vec.size()];
				for (int i = 0; i < vec.size(); i++) {
					res[i] = vec.elementAt(i);
				}
				db.closeConnection();
			}
		}

		return res;
	}

	public Turn[] getAllTurnFromToday(	String username, String password,
										int officeId) {
		Database db = new Database();
		Turn[] res = null;
		Date now = new Date();
		String fromDate = Helper.getShortDate(now);
		String toDate = Helper.getShortDateAfterSomeDay(now, 30);
		if (db.openConnection()) {
			Vector<Turn> vec =
					db.getTurn(username, officeId, fromDate, toDate);
			res = new Turn[vec.size()];
			for (int i = 0; i < vec.size(); i++) {

				res[i] = vec.elementAt(i);
			}
		}
		return res;
	}

	public int reserveForMe(String username, String password, int turnId,
							int firstReservationId, int taskId,
							int numberOfTurns, int resNum) {
		boolean bool = false;
		Database db = new Database();
		Reservation_new r = new Reservation_new();
		try {
			if (db.openConnection()) {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					r.userId = userId;
					r.firstReservationId = firstReservationId;
					r.numberOfTurns = numberOfTurns;
					r.patientId = userId;
					r.payment = db.getTaskPrice(taskId);
					r.taskId = taskId;
					r.turnId = turnId;
					r.price = db.getTaskPrice(taskId);

					r.id = 0;
					bool = db.checkRefNum(resNum);
					if ((resNum == -1) || (bool == true)) {
						if (db.getWallet(userId) >= r.price) {
							if (db.checkCapacity(r)) {
								r.id = db.getMaxReservationId() + 1;
								db.decreseCapacity(r);
								db.reserveTurn(r);
								db.increseWallet(userId, r.price);
							}
						} else {
							r.id = -1;
						}
					} else {
						r.id = -2;
					}
				}
			}
		} catch (SQLException e) {
			r.id = 0;
			System.out.println(e.getMessage());
		}
		return r.id;
	}

	public int reserveForUser(	String username, String password, int turnId,
								int firstReservationId, int taskId,
								int numberOfTurns, String patientUserName) {
		Database db = new Database();
		Reservation_new r = new Reservation_new();
		try {
			if (db.openConnection()) {
				if (db.checkUserPass(username, password)) {

					r.userId = db.getUserId(username);
					r.firstReservationId = firstReservationId;
					r.numberOfTurns = numberOfTurns;
					r.patientId = db.getUserId(patientUserName);
					r.payment = 0;
					r.taskId = taskId;
					r.turnId = turnId;
					r.price = db.getTaskPrice(taskId);

					r.id = 0;
					if (db.checkCapacity(r)) {
						r.id = db.getMaxReservationId() + 1;
						db.decreseCapacity(r);
						db.reserveTurn(r);

						Info_Reservation info = db.getUserOfficeTurn(r.id);
						Helper.sendReservationMessage(db, r.userId, info);
					}
				}
			}
		} catch (SQLException e) {
			r.id = 0;
			System.out.println(e.getMessage());
		}
		return r.id;
	}

	public int reserveForUser2(	String username, String password,
								int officeId, int turnId,
								int firstReservationId, int taskId,
								int numberOfTurns, String patientUserName,
								int price) {
		Database db = new Database();
		Reservation_new r = new Reservation_new();
		try {
			if (db.openConnection()) {
				if (db.checkUserPass(username, password)) {
					r.userId = db.getUserId(username);
					r.firstReservationId = firstReservationId;
					r.numberOfTurns = numberOfTurns;
					r.patientId = db.getUserId(patientUserName);
					r.payment = 0;
					r.taskId = taskId;
					r.turnId = turnId;
					if (db.isHaveSecretaryPermission(username, password,
							officeId)) {
						r.price = price;
					} else {
						r.price = db.getTaskPrice(taskId);
					}

					r.id = 0;
					if (db.checkCapacity(r)) {
						r.id = db.getMaxReservationId() + 1;
						db.decreseCapacity(r);
						db.reserveTurn(r);

						Info_Reservation info = db.getUserOfficeTurn(r.id);
						Helper.sendReservationMessage(db, r.userId, info);
					}
				}
			}
		} catch (SQLException e) {
			r.id = 0;
			System.out.println(e.getMessage());
		}
		return r.id;
	}

	public int reserveForGuest(	String username, String password, int turnId,
								int firstReservationId, int taskId,
								int numberOfTurns, String patientFirstName,
								String patientLastName,
								String patientPhoneNo, int patientCityId) {
		Database db = new Database();
		Reservation_new r = new Reservation_new();
		try {
			if (db.openConnection()) {
				if (db.checkUserPass(username, password)) {
					int guestId = db.insertGuest(patientFirstName,
							patientLastName, patientPhoneNo, patientCityId);
					r.userId = db.getUserId(username);
					r.firstReservationId = firstReservationId;
					r.numberOfTurns = numberOfTurns;
					r.patientId = guestId;
					r.payment = 0;
					r.taskId = taskId;
					r.turnId = turnId;
					r.price = db.getTaskPrice(taskId);

					r.id = 0;
					if (db.checkCapacity(r)) {
						r.id = db.getMaxReservationId() + 1;
						db.decreseCapacity(r);
						db.reserveTurn(r);
					}
				}
			}
		} catch (SQLException e) {
			r.id = 0;
			System.out.println(e.getMessage());
		}
		return r.id;
	}

	public int reserveForGuestFromUser(	String username, String password,
										int turnId, int firstReservationId,
										int taskId, int numberOfTurns,
										String patientFirstName,
										String patientLastName,
										String patientPhoneNo,
										int patientCityId, int resNum) {
		boolean bool = false;
		Database db = new Database();
		Reservation_new r = new Reservation_new();
		try {
			if (db.openConnection()) {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					int guestId = db.insertGuest(patientFirstName,
							patientLastName, patientPhoneNo, patientCityId);
					r.userId = db.getUserId(username);
					r.firstReservationId = firstReservationId;
					r.numberOfTurns = numberOfTurns;
					r.patientId = guestId;
					r.payment = 0;
					r.taskId = taskId;
					r.turnId = turnId;
					r.price = db.getTaskPrice(taskId);

					r.id = 0;
					bool = db.checkRefNum(resNum);
					if ((resNum == -1) || (bool == true)) {

						if (db.getWallet(userId) >= r.price) {
							if (db.checkCapacity(r)) {
								r.id = db.getMaxReservationId() + 1;
								db.decreseCapacity(r);
								db.reserveTurn(r);
								db.increseWallet(userId, r.price);
							}
						} else {
							r.id = -1;
						}
					} else {
						r.id = -2;
					}
				}
			}
		} catch (SQLException e) {
			r.id = 0;
			System.out.println(e.getMessage());
		}
		return r.id;
	}

	public int reserveForGuest2(String username, String password,
								int officeId, int turnId,
								int firstReservationId, int taskId,
								int numberOfTurns, String patientFirstName,
								String patientLastName,
								String patientPhoneNo, int patientCityId,
								int price) {
		Database db = new Database();
		Reservation_new r = new Reservation_new();
		int guestId;
		try {
			if (db.openConnection()) {
				if (db.checkUserPass(username, password)) {
					guestId = db.insertGuest(patientFirstName,
							patientLastName, patientPhoneNo, patientCityId);
					r.userId = db.getUserId(username);
					r.firstReservationId = firstReservationId;
					r.numberOfTurns = numberOfTurns;
					r.patientId = guestId;
					r.payment = 0;
					r.taskId = taskId;
					r.turnId = turnId;
					if (db.isHaveSecretaryPermission(username, password,
							officeId)) {
						r.price = price;
					} else {
						r.price = db.getTaskPrice(taskId);
					}

					r.id = 0;
					if (db.checkCapacity(r)) {
						r.id = db.getMaxReservationId() + 1;
						db.decreseCapacity(r);
						db.reserveTurn(r);
					}
				}
			}
		} catch (SQLException e) {
			r.id = 0;
			System.out.println(e.getMessage());
		}
		return r.id;
	}

	public String cancelReservation(String username, String password,
									int reservationId) {
		String res = "";
		Database db = new Database();
		Info_Reservation info;
		try {
			if (db.openConnection()) {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					info = db.getUserOfficeTurn(reservationId);
					if (info.username == null)
						return Helper.getMessageUserNameIsNull();

					int role = db.getPermissionOnOffice(info.officeId,
							username);
					if (role == Role.secretary || role == Role.doctor) {
						Helper.sendCancelationMessage(db, userId, info);
						db.setWallet2(userId, reservationId);
						db.removeFromReserve(reservationId);
						db.increseCapacity(info.turnId, info.numberOfTurns);
						res = OK_MESSAGE;
					} else if (info.username.equals(username)
							|| info.patientId == userId) {
						String nobatDate = db.getTurnDate(reservationId);
						String str = Helper
								.getShortDateAfterSomeDay(new Date(), 1);
						int result = nobatDate.compareTo(str);
						if (result == 1) {
							Helper.sendCancelationMessage(db, userId, info);
							db.setWallet2(userId, reservationId);
							db.removeFromReserve(reservationId);
							db.increseCapacity(info.turnId,
									info.numberOfTurns);
							res = OK_MESSAGE;
						} else {
							res = Helper.getMessageUserNameIsNull();
						}
					} else {
						res = Helper.getMessageUserNameIsNull();
					}
				}
			}
		} catch (SQLException e) {
			res = Helper.getMessageUnknownError();
			System.out.println(e.getMessage());
		}
		return res;
	}

	public Reservation2[]
			getReservationByTurnId(	String username, String password,
									int officeId, int turnId) {
		Database db = new Database();
		Vector<Reservation2> vec;
		Reservation2[] res = null;
		try {
			if (!db.openConnection())
				return null;
			if (!db.checkUserPass(username, password))
				return null;
			int perm = db.getPermissionOnOffice(officeId, username);
			if (perm == Role.doctor || perm == Role.secretary) {
				vec = db.getReservationForAdmin(turnId);
			} else {
				vec = db.getReservationForUser(turnId, username);
			}
			res = new Reservation2[vec.size()];
			for (int i = 0; i < vec.size(); i++) {
				res[i] = vec.elementAt(i);
			}
		} catch (Exception e) {
			return res;
		}
		return res;
	}

	public Reservation4[]
			getReservationByUser(	String username, String password,
									int officeId, int count, int index) {
		Database db = new Database();
		Vector<Reservation4> vec = new Vector<Reservation4>();
		Reservation4[] res = null;
		try {
			if (!db.openConnection())
				return null;
			if (!db.checkUserPass(username, password))
				return null;

			vec = db.getReservation(username, officeId);
			int firstIndex = index * count;
			if (firstIndex >= vec.size())
				return null;
			int size = Math.min(count, vec.size() - firstIndex);
			res = new Reservation4[size];
			for (int i = 0; i < res.length; i++) {
				res[i] = vec.elementAt(i + firstIndex);
			}
		} catch (Exception e) {
			return res;
		}
		return res;
	}

	public Reservation2[]
			getReservationByDate(	String username, String password,
									int officeId, String fromDate,
									String toDate) {
		Database db = new Database();
		Vector<Reservation2> vec = new Vector<Reservation2>();
		Reservation2[] res = null;
		try {
			if (!Helper.checkDateBoundary(fromDate, toDate))
				return null;
			if (!db.openConnection())
				return null;
			if (db.checkUserPass(username, password)) {
				int perm = db.getPermissionOnOffice(officeId, username);
				if (perm != Role.doctor && perm != Role.secretary) {
					vec = db.getReservation(officeId, fromDate, toDate);
					res = new Reservation2[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			}
		} catch (Exception e) {

		} finally {
			db.closeConnection();
		}
		return res;
	}

	public UserTurn[]
			getPatientTurnInfoByDate(	String username, String password,
										int officeId, String fromDate,
										String toDate) {
		Database db = new Database();
		Vector<UserTurn> vec = new Vector<UserTurn>();
		UserTurn[] res = null;
		if (!Helper.checkDateBoundary(fromDate, toDate))
			return res;
		try {
			if (!db.openConnection())
				return null;
			if (!db.isHaveSecretaryPermission(username, password, officeId))
				return null;

			vec = db.getUserTurn(officeId, fromDate, toDate);
			res = new UserTurn[vec.size()];
			for (int i = 0; i < vec.size(); i++) {
				res[i] = vec.elementAt(i);
			}
		} catch (Exception e) {
			return res;
		} finally {
			db.closeConnection();
		}
		return res;
	}

	//////////////////////////////////////////// Message Management Services

	public boolean sendMessage(	String username, String password,
								int officeId, String receiver,
								String subject, String message) {
		boolean res = false;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int senderId = db.getUserId(username);
					int receiverId = db.getUserId(receiver);
					String dateStr = Helper.getTodayShortDate();
					String timeStr = Helper.getCurrentTime();

					db.sendMessage(officeId, senderId, receiverId, subject,
							message, dateStr, timeStr);
					res = true;
				}
			} catch (SQLException e) {
				System.out.println("err = " + e.getMessage());
				res = false;
			}
		} else {
			res = false;
		}
		return res;
	}

	public boolean sendMessageBatch(String username, String password,
									int officeId, String[] receivers,
									String[] phoneNo, String subject,
									String message) {
		boolean res = true;
		Database db = new Database();
		Vector<String> receiverVec;
		Vector<String> phoneVec;
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					receiverVec = Helper.removeDuplicates(receivers);
					phoneVec = Helper.removeDuplicates(phoneNo);
					int[] receiverIds = new int[receiverVec.size()];
					for (int i = 0; i < receiverIds.length; i++) {
						receiverIds[i] =
								db.getUserId(receiverVec.elementAt(i));
					}
					int senderId = db.getUserId(username);
					String dateStr = Helper.getTodayShortDate();
					String timeStr = Helper.getCurrentTime();

					db.sendMessageBatch(officeId, senderId, receiverIds,
							subject, message, dateStr, timeStr);
					Helper.sendSMS(officeId, senderId, phoneVec, subject,
							message, dateStr, timeStr);
				}
			} catch (SQLException e) {
				res = false;
			}
		} else {
			res = false;
		}
		return res;
	}

	public Info_Message[] getUnreadMessages(String username, String password,
											int officeId) {
		return getMessages(username, password, officeId, true);
	}

	public Info_Message[] getAllMessages(	String username, String password,
											int officeId) {
		return getMessages(username, password, officeId, false);
	}

	private Info_Message[] getMessages(	String username, String password,
										int officeId, boolean onlyUnread) {
		Info_Message[] res = null;
		Vector<Info_Message> vec;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					vec = db.getMessages(userId, officeId, onlyUnread);
					res = new Info_Message[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public void setMessageRead(	String username, String password,
								int officeId, int messageId) {
		Database db = new Database();

		if (db.openConnection()) {
			int receiverId = db.getUserId(username);
			try {
				if (db.checkUserPass(username, password)) {
					db.setMessageRead(officeId, receiverId, messageId);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());

			} finally {
				db.closeConnection();
			}
		}
	}

	public void setAllMessageRead(	String username, String password,
									int officeId) {
		Database db = new Database();

		if (db.openConnection()) {
			int receiverId = db.getUserId(username);
			try {
				if (db.checkUserPass(username, password)) {
					db.setAllMessageRead(officeId, receiverId);
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}

	}

	public boolean removeMessage(	String username, String password,
									int officeId, int messageId) {
		boolean res = false;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userid = db.getUserId(username);
					db.removeMessage(officeId, userid, messageId);
					res = true;
				}
			} catch (SQLException e) {
				res = false;
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	////////////////////////////////// patient reception and management

	public Info_Patient[] getTodayPatient(	String username, String password,
											int officeId) {
		Info_Patient[] res = null;
		Vector<Info_Patient> vec = null;
		Database db = new Database();
		if (db.openConnection()) {
			String today = Helper.getTodayShortDate();
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					vec = db.getOneDayPatient(officeId, today);
					res = new Info_Patient[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public boolean reception(	String username, String password, int officeId,
								int reservationId, int payment,
								String description) {
		boolean res = true;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					db.reception(reservationId, payment, description);
				}
			} catch (SQLException e) {
				res = false;
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public Info_patientFile[]
			getPatientFile(	String username, String password, int officeId,
							String patientUsername) {
		Database db = new Database();
		Vector<Info_patientFile> vec = null;
		Info_patientFile[] res = null;
		if (db.openConnection()) {
			// System.out.println();
			boolean patientFlag = username.equals(patientUsername) && db
					.isHavePatientPermission(username, password, officeId);
			if (patientFlag || db.isHaveSecretaryPermission(username,
					password, officeId)) {
				try {
					vec = db.getPatientAllTurn(patientUsername, officeId);
					res = new Info_patientFile[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				} catch (SQLException e) {

				} finally {
					db.closeConnection();
				}
			}
		}
		return res;
	}

	////////////////////////////////// Gallery Management Services

	public int[] getAllGalleryPicId(String username, String password,
									int officeId) {
		int[] res = null;
		Vector<Integer> vec = null;
		Database db = new Database();
		if (db.openConnection()) {
			if (db.isHavePatientPermission(username, password, officeId)) {
				try {
					vec = db.getAllPicId(officeId);
				} catch (SQLException e) {

				} finally {
					db.closeConnection();
				}
				res = new int[vec.size()];
				for (int i = 0; i < res.length; i++) {
					res[i] = vec.elementAt(i);
				}
			}
		}

		return res;
	}

	public PhotoDesc[] getAllGalleryPicId2(	String username, String password,
											int officeId) {
		PhotoDesc[] res = null;
		Vector<PhotoDesc> vec = null;
		Database db = new Database();

		if (db.openConnection()) {
			if (db.isHavePatientPermission(username, password, officeId)) {
				try {
					vec = db.getAllPicIdDesc(officeId);
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				} finally {
					db.closeConnection();
				}
				if (vec != null) {
					res = new PhotoDesc[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			}
		}

		return res;
	}

	public PhotoDesc getGalleryPic(	String username, String password,
									int officeId, int picId) {
		PhotoDesc res = null;
		Database db = new Database();
		if (db.openConnection()) {
			if (db.isHavePatientPermission(username, password, officeId)) {
				try {
					res = db.getGalleryPic(officeId, picId);

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					db.closeConnection();
				}
			}
		}
		return res;
	}

	public int setGalleryPic(	String username, String password, int officeId,
								String pic, String description) {
		int picId = 0;
		String dateTime =
				Helper.getTodayShortDate() + " " + Helper.getCurrentTime();
		Database db = new Database();

		if (pic == null || pic.length() <= 0)
			return 0;

		if (db.openConnection()) {
			picId = db.getMaxPicId() + 1;
			if (db.isHaveSecretaryPermission(username, password, officeId)) {
				try {
					if (db.isGalleryPicsNumberLessThanMax(officeId)) {
						db.insertIntoGallery(officeId, picId, pic,
								description, dateTime);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					db.closeConnection();
				}
			}
		}
		return picId;
	}

	public void deleteFromGallery(	String username, String password,
									int officeId, int picId) {
		Database db = new Database();
		if (db.openConnection()) {
			if (db.isHaveSecretaryPermission(username, password, officeId)) {
				try {
					db.deleteFromGallery(officeId, picId);
				} catch (SQLException e) {

				} finally {
					db.closeConnection();
				}
			}
		}
	}

	public void changeGalleryPicDescription(String username, String password,
											int officeId, int picId,
											String description) {
		Database db = new Database();
		if (db.openConnection()) {
			if (db.isHaveSecretaryPermission(username, password, officeId)) {
				try {
					db.changeGalleryPicDescription(officeId, picId,
							description);
				} catch (SQLException e) {

				} finally {
					db.closeConnection();
				}
			}
		}
	}

	////////////////////////////////// test webservice
	public String helloWorld() {
		return "Hello World!";
	}

	////////////////////////////////// unknown services!!!
	public String register2(String name, String lastname, String mobileno,
							String username, String password, int cityid,
							String pic, String email) {
		String res = OK_MESSAGE;
		Database db = new Database();
		if (!db.openConnection())
			return Helper.getMessageUnknownError();
		if (!db.isUsernameAvailable(username)) {
			db.closeConnection();
			return Helper.getMessageUserNameNotAvailabe();
		}

		byte[] picbyte = null;
		if (pic != null && pic.length() > 0) {
			picbyte = Helper.getBytes(pic);
		}
		try {
			db.register(name, lastname, mobileno, username, password, cityid,
					picbyte, email);
		} catch (SQLException e) {
			res = Helper.getMessageUnknownError();
		}
		db.closeConnection();
		return res;
	}

	public int registerOffice2(	String username, String password, int cityid,
								int spec, int subspec, String address,
								String tellNo, int timeQuantum,
								String biography) {
		Database db = new Database();
		int id = 0;
		if (db.openConnection()) {
			try {
				if (db.checkMasterPassword(username, password)
						|| db.isHaveSupportPermission(username, password)) {
					id = db.InsertInOffice(spec, subspec, address, tellNo,
							cityid, 0.0, 0.0, timeQuantum, biography);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.closeConnection();
			}
		}
		return id;
	}

	///////////////////////////////////////// Support Utility Services

	public boolean addDoctorToOffice(	String username, String password,
										int officeId, String doctor) {
		boolean ret = false;
		Database db = new Database();
		int doctorId = db.getUserId(doctor);
		try {
			if (db.openConnection()) {
				if (db.checkMasterPassword(username, password)
						|| db.isHaveSupportPermission(username, password)) {
					db.InsertInDoctorOffice(officeId, doctorId);
					ret = true;
				}
			}
		} catch (SQLException e) {
			ret = false;
		}
		return ret;
	}

	////////////////////////////////////////////////////////////

	// used
	public Office getOfficeInfo(String username, String password,
								int officeId) {
		Office office = new Office();
		Database db = new Database();
		if (!db.openConnection()) {
			return office;
		}
		try {
			office = db.getOfficeInfo(officeId);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return office;
		}
		return office;
	}

	// used
	public Ticket[] getUserTicket(String username, String password) {
		Database db = new Database();
		Vector<Ticket> vec;
		Ticket[] res = null;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					vec = db.getUserTicket(userId);
					res = new Ticket[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public Ticket[] getUserTicketSupporter(	String username, String password) {
		Database db = new Database();
		Vector<Ticket> vec;
		Ticket[] res = null;

		if (db.openConnection()) {
			try {
				if (db.isHaveSupportPermission(username, password)) {
					vec = db.getUserTicketSupporter();
					res = new Ticket[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	// used
	public int setUserTicket(	String username, String password, int subject,
								String topic, int priority) {
		Database db = new Database();
		int ticketId = 0;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					ticketId = db.setUserTicket(userId, subject, topic,
							priority);
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return ticketId;
	}

	// used
	public TicketMessage[] getUserTicketMessage(int ticketId,
												String username,
												String password) {
		Database db = new Database();
		Vector<TicketMessage> vec;
		TicketMessage[] res = null;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					vec = db.getUserTicketMessage(ticketId);
					res = new TicketMessage[vec.size()];

					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	// used
	public String setUserTicketMessage(	int ticketId, String username,
										String password, String message) {
		Database db = new Database();
		String Str = "error";

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					Str = db.setUserTicketMessage(userId, ticketId, message);
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return Str;
	}

	public Ticket[] getUserAllTicket(	String username, String password,
										int officeId) {
		Database db = new Database();
		Vector<Ticket> vec;
		Ticket[] res = null;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					vec = db.getUserTicket(userId);
					res = new Ticket[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	// used
	public TicketSubject[] getUserTicketSubject(String username,
												String password) {
		Database db = new Database();
		Vector<TicketSubject> vec;
		TicketSubject[] res = null;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					vec = db.getUserTicketSubject();
					res = new TicketSubject[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public Ticket[] getAllUserAllTicket(String username, String password,
										int officeId) {
		Database db = new Database();
		Vector<Ticket> vec;
		Ticket[] res = null;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password) && officeId == 0) {
					// int userId = db.getUserId(username, officeId);
					vec = db.getAllUserTicket();
					res = new Ticket[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	// used
	public int setQuestion(	String username, String password, int officeId,
							String lable, int replyType) {

		Database db = new Database();
		int res = 0;

		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					res = db.setQuestion(lable, replyType, officeId);
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public boolean setReply(String username, String password, int officeId,
							int questionId, String reply) {
		Database db = new Database();
		boolean res = false;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					db.setReply(userId, questionId, reply);
					res = true;
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	// used
	public boolean setReplyBatch(	String username, String password,
									int officeId, int[] questionId,
									String[] reply) {
		Database db = new Database();
		boolean res = false;

		if (questionId.length != reply.length)
			return false;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					for (int i = 0; i < reply.length; i++)
						db.setReply(userId, questionId[i], reply[i]);
					res = true;
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	// used
	public boolean setReplyBatchForUser(String username, String password,
										int officeId, String patientUserName,
										int[] questionId, String[] reply) {
		Database db = new Database();
		boolean res = false;

		if (questionId.length != reply.length)
			return false;

		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					int userId = db.getUserId(patientUserName);
					for (int i = 0; i < reply.length; i++)
						db.setReply(userId, questionId[i], reply[i]);
					res = true;
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	// used
	public Question[] getQuestion(	String username, String password,
									int officeId) {
		Database db = new Database();
		Vector<Question> vec;
		Question[] res = null;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					vec = db.getQuestion(officeId);
					res = new Question[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	// used
	public Reply[] getReply(String username, String password, int officeId,
							String patientUserName) {
		Database db = new Database();
		Vector<Reply> vec;
		Reply[] res = null;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(patientUserName);
					vec = db.getReply(userId);
					res = new Reply[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {
				System.err.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	// used
	public boolean deleteQuestion(	String username, String password,
									int officeId, int questionId) {
		Database db = new Database();
		boolean res = false;

		if (db.openConnection()) {
			try {
				if (db.isHaveSecretaryPermission(username, password,
						officeId)) {
					db.deleteFromQuestion(questionId, officeId);
					res = true;
				}
			} catch (SQLException e) {
				res = false;
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	// used
	public String addOfficeForUser(	String username, String password,
									int officeId) {
		Database db = new Database();
		String msg;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					if (db.isHaveDoctorPermission(username, password,
							officeId)) {
						return "\u0634\u0645\u0627 \u067e\u0632\u0634\u06a9 "
								+ "\u0627\u06cc\u0646 \u0645\u0637\u0628 "
								+ "\u0647\u0633\u062a\u06cc\u062f \u0648 "
								+ "\u0646\u0645\u06cc \u062a\u0648\u0627"
								+ "\u0646\u06cc\u062f \u0628\u06cc\u0645\u0627"
								+ "\u0631 \u0622\u0646 \u0628\u0627\u0634\u06cc\u062f";
					}
					int userid = db.getUserId(username);
					if (db.isOfficeIdAvailable(officeId)) {
						db.addOfficeForUser(userid, officeId);
						msg = OK_MESSAGE;
					} else {
						msg = Helper.getMessageIncorrectOfficeId();
					}
				} else {
					msg = Helper.getMessageIncorrectUserPass();
				}
			} catch (SQLException e) {
				if (e.getErrorCode() == 1062) {
					// Ignore Duplicate Error
					msg = OK_MESSAGE;
				} else {
					msg = Helper.getMessageUnknownError();
				}
			} finally {
				db.closeConnection();
			}
		} else {
			msg = Helper.getMessageUnknownError();
		}
		return msg;
	}

	// used
	public String deleteOfficeForUser(	String username, String password,
										int officeId) {
		Database db = new Database();
		String msg;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					if (db.isOfficeIdAvailable(officeId)) {
						int userid = db.getUserId(username);
						db.deleteOfficeForUser(userid, officeId);
						msg = OK_MESSAGE;
					} else {
						msg = Helper.getMessageIncorrectOfficeId();
					}
				} else {
					msg = Helper.getMessageIncorrectUserPass();
				}
			} catch (SQLException e) {
				msg = Helper.getMessageUnknownError();
			} finally {
				db.closeConnection();
			}
		} else {
			msg = Helper.getMessageUnknownError();
		}
		return msg;
	}

	// used
	public Office[] getOfficeForUser(String username, String password) {
		Database db = new Database();
		Vector<Integer> vec = new Vector<Integer>();
		Office[] res = null;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userid = db.getUserId(username);
					vec = db.getOfficeIdForUser(userid);
					if (vec != null) {
						res = new Office[vec.size()];
						for (int i = 0; i < res.length; i++)
							res[i] = db.getOfficeInfo(vec.elementAt(i));
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	// used
	public Office2[] getOfficeForDoctorOrSecretary(	String username,
													String password) {
		Database db = new Database();
		Vector<Integer> vec1 = new Vector<Integer>();
		Office2[] res = null;

		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userid = db.getUserId(username);
					vec1 = db.getOfficeIdForDoctorOrSecretary(userid);

					res = new Office2[vec1.size()];
					for (int i = 0; i < vec1.size(); i++) {
						Office temp = db.getOfficeInfo(vec1.elementAt(i));
						res[i] = new Office2(temp);
						res[i].isMyOffice = true;
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public Ticket[] getAllTicketsByDate(String username, String password,
										int offset, int count) {
		Ticket[] res = null;
		Vector<Ticket> vec;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.isHaveSupportPermission(username, password)) {
					vec = db.getAllTickets(offset, count);
					if (vec != null) {
						res = new Ticket[vec.size()];
						for (int i = 0; i < res.length; i++) {
							res[i] = vec.elementAt(i);
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public String setSupportTicketMessage(	int ticketId, String username,
											String password,
											String message) {
		Database db = new Database();
		String Str = OK_MESSAGE;

		if (db.openConnection()) {
			try {
				if (db.isHaveSupportPermission(username, password)) {
					int userId = db.getUserId(username);
					Str = db.setUserTicketMessage(userId, ticketId, message);
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return Str;
	}

	public String registerAddDoctor(String usernameSupport,
									String passwordSupport, int officeId,
									String name, String lastname,
									String mobileno, String username,
									String password, int cityid, String pic,
									String email) {
		String res = "OK";
		Database db = new Database();
		if (db.openConnection()) {
			if (db.isUsernameAvailable(username)) {
				byte[] picbyte = null;
				if (pic != null && pic.length() > 0) {
					picbyte = Helper.getBytes(pic);
				}

				if (db.isHaveSupportPermission(usernameSupport,
						passwordSupport)) {
					try {
						int userId = db.register(name, lastname, mobileno,
								username, password, cityid, picbyte, email);
						db.InsertInDoctorOffice(officeId, userId);
					} catch (SQLException e) {
						res = Helper.getMessageUnknownError();
					} finally {
						db.closeConnection();
					}
				} else {
					res = Helper.getMessageNotPermittedAcces();
				}
			} else {
				res = Helper.getMessageUserNameNotAvailabe();
			}
		} else {
			res = Helper.getMessageUnknownError();
		}

		return res;
	}

	public String loginSupporter(String username, String password) {
		Database db = new Database();
		String res = OK_MESSAGE;
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userid = db.getUserId(username);
					res = db.isHaveSupporter(userid);
				}
				else {
					res=Helper.getMessageIncorrectUserPass();
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				res=Helper.getMessageUnknownError();
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public Info_Message1[] getAllUnreadMessages(String username,
												String password) {
		Info_Message1[] res = null;
		Vector<Info_Message1> vec;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					vec = db.getAllUnreadMessages(userId);
					res = new Info_Message1[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public void setMessageRead2(String username, String password,
								int messageId) {
		Database db = new Database();

		if (db.openConnection()) {
			int receiverId = db.getUserId(username);
			try {
				if (db.checkUserPass(username, password)) {
					db.setMessageRead2(receiverId, messageId);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());

			} finally {
				db.closeConnection();
			}
		}
	}

	public Office[] getAllOfficeForCity(String username, String password,
										int cityId, int count, int index) {
		Database db = new Database();
		Vector<Integer> vec = new Vector<Integer>();
		Office[] res = null;
		int j = count * (index - 1);
		int k = count * index;
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					vec = db.getAllOfficeIdForCity(cityId, userId);
					if (vec != null) {
						res = new Office[count];
						int l = 0;
						if (k > vec.size()) {
							k = vec.size();
						}
						for (int i = j; i < k; i++) {
							res[l] = db.getOfficeInfo(vec.elementAt(i));
							l++;
						}
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public Office[] getOfficeByFilter(	String username, String password,
										int provinceId, int cityId,
										int specId, int subspecId,
										String firstName, String lastName,
										int count, int index) {
		Database db = new Database();
		Vector<Integer> vec = new Vector<Integer>();
		Office[] res = null;
		int j = count * (index - 1);
		int k = count * index;
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					vec = db.getOfficeByFilter(userId, provinceId, cityId,
							specId, subspecId, firstName, lastName);
					if (vec != null) {
						res = new Office[count];
						int l = 0;
						if (k > vec.size()) {
							k = vec.size();
						}
						for (int i = j; i < k; i++) {
							res[l] = db.getOfficeInfo(vec.elementAt(i));
							l++;
						}
					}
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public int getRoleInOffice(	String username, String password,
								int officeId) {
		Database db = new Database();
		int role;
		try {
			if (db.openConnection()) {
				role = db.getRoleInOffice(username, password, officeId);

			} else {
				role = Role.none;
			}
		} catch (SQLException e) {
			role = Role.none;
		}
		return role;
	}

	public Reservation4[] getReservationByUser2(String username,
												String password, int count,
												int index) {
		int k = count * (index - 1);
		int l = count * index;
		Database db = new Database();
		Vector<Reservation4> vec = new Vector<Reservation4>();
		Reservation4[] res = null;
		try {
			if (!db.openConnection())
				return null;
			if (!db.checkUserPass(username, password))
				return null;
			Vector<Integer> vec1 = db.getUserOfficeId(username);
			int counter = 0;
			int[] res1 = new int[vec1.size()];
			int size1 = 0;
			for (int i = 0; i < vec1.size(); i++) {
				res1[i] = vec1.get(i);
				vec = db.getReservation(username, res1[i]);
				counter = counter + vec.size();
			}
			res = new Reservation4[counter];

			for (int i = 0; i < vec1.size(); i++) {
				res1[i] = vec1.get(i);
				vec = db.getReservation(username, res1[i]);
				int j1 = 0;
				for (int j = size1; j < size1 + vec.size(); j++) {
					res[j] = vec.elementAt(j1);
					j1++;
				}
				size1 += vec.size();
			}
		} catch (Exception e) {
			return res;
		}
		return res;
	}

	public Info_Message1[] getAllMessages1(	String username,
											String password) {
		Info_Message1[] res = null;
		Vector<Info_Message1> vec;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					vec = db.getAllMessages(userId);
					res = new Info_Message1[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public boolean removeMessage2(	String username, String password,
									int messageId) {
		boolean res = false;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userid = db.getUserId(username);
					db.removeMessage1(userid, messageId);
					res = true;
				}
			} catch (SQLException e) {
				res = false;
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public Payment[] setResNum(	String username, String password,
								int amount) {

		Payment[] res = null;
		Vector<Payment> vec;
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					vec = db.setResNum(userId, amount);
					res = new Payment[vec.size()];
					for (int i = 0; i < res.length; i++) {
						res[i] = vec.elementAt(i);
					}
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public void setWallet(int resNum, int amount) {
		Database db = new Database();

		if (db.openConnection()) {
			try {
				int userId = db.getUserIdFromPayment(resNum);
				if (userId != -1) {
					db.setWallet(userId, amount);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());

			} finally {
				db.closeConnection();
			}
		}
	}

	public int getWallet(String username, String password) {
		Database db = new Database();
		int res = -1;
		if (db.openConnection()) {
			try {
				if (db.checkUserPass(username, password)) {
					int userId = db.getUserId(username);
					res = db.getWallet(userId);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				return res;
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public String forgetPassWord(String username) {
		String res = OK_MESSAGE;
		String password = "NO";
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (!db.isUsernameAvailable(username)) {
					int userId = db.getUserId(username);
					password = db.hashingPassWord(userId);
					if (!password.equals("NO")) {
						res = db.publishPassWord(password, userId);
					} else {
						res = Helper.getMessageUnknownError();
					}
				} else {
					res = Helper.getMessageUserNameInvalid();
				}
			} catch (SQLException e) {
				res = Helper.getMessageUnknownError();
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}

	public String verifySecurityCode(String username, String password) {
		String res = "";
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (!db.isUsernameAvailable(username)) {
					res = db.verifySecurityCode(username, password);
					if (!res.equals(OK_MESSAGE)) {
						res = Helper.getMessageVerifyCodeInvalid();
					}
				}
			} catch (SQLException e) {
				res = Helper.getMessageUnknownError();
			}
		}
		return res;
	}

	public String changePassword(String username, String password) {
		String res = "";
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (!db.isUsernameAvailable(username)) {
					db.changePassword(username, password);
					res = OK_MESSAGE;
				}
			} catch (SQLException e) {
				res = Helper.getMessageUnknownError();
			}
		}
		return res;
	}

	public AppInfo getVersionInfo(String versionName, String password) {
		AppInfo res = null;
		double version = Double.parseDouble(versionName);
		Database db = new Database();
		if (db.openConnection()) {
			try {
				if (password.equals(
						"1882cd559e560601efdc452fa074c215b55262cd")) {
					res = db.getVersionName(version);
				}
			} catch (SQLException e) {

			} finally {
				db.closeConnection();
			}
		}
		return res;
	}
}
