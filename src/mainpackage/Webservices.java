package mainpackage;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import persindatepicker.PersianCalendar;
import primitives.City;
import primitives.Info_Message;
import primitives.Info_Patient;
import primitives.Info_Reservation;
import primitives.Info_Reservation2;
import primitives.Info_User;
import primitives.Info_patientFile;
import primitives.Office;
import primitives.PhotoDesc;
import primitives.Province;
import primitives.Reservation2;
import primitives.Reservation4;
import primitives.Reservation_new;
import primitives.Role;
import primitives.Spec;
import primitives.Subspec;
import primitives.Task;
import primitives.TaskGroup;
import primitives.Turn;
import primitives.User;
import primitives.UserTurn;

public class Webservices {
	public String[] getProvinceName(){
		String[] res = null;
		Database db = new Database();
		Vector<Province> vec = db.getAllProvinceNames();
		res = new String[vec.size()];
		for(int i = 0; i < vec.size(); i++){
			res[i] = vec.elementAt(i).name;
		}
		return res;
	}
	
	public Province[] getProvince(){
		Database db = new Database();
		Vector<Province> vec = db.getAllProvinceNames();
		Province[] res = new Province[vec.size()];
		for(int i = 0; i < vec.size(); i++){
			res[i] = vec.elementAt(i);
		}
		
		return res;
	}
	
	public String[] getCityName(){
		String[] res = null;
		Database db = new Database();
		Vector<City> vec = db.getAllCityNames();
		res = new String[vec.size()];
		for(int i = 0; i < vec.size(); i++){
			res[i] = vec.elementAt(i).name;
		}
		return res;
	}
	
	public City[] getCity(){
		Database db = new Database();
		Vector<City> vec = db.getAllCityNames();
		City[] res = new City[vec.size()];
		for(int i = 0; i < vec.size(); i++){
			res[i] = vec.elementAt(i);
		}
		
		return res;
	}
	
	public City[] getCityOfProvince(int provinceID){
		Database db = new Database();
		Vector<City> vec = db.getCityByProvince(provinceID);
		City[] res = new City[vec.size()];
		for(int i = 0; i < vec.size(); i++){
			res[i] = vec.elementAt(i);
		}
		
		return res;
	}
	
	public String helloWorld(){
		return "Hello World!";
	}
	
	public Spec[] getSpec(){
		Database db = new Database();
		Vector<Spec> vec = db.getAllSpec();
		Spec[] res = new Spec[vec.size()];
		for(int i = 0; i < vec.size(); i++){
			res[i] = vec.elementAt(i);
		}
		
		return res;
	}
	
	public Subspec[] getSubSpec(int specId){
		Database db = new Database();
		Vector<Subspec> vec = db.getSubspec(specId);
		Subspec[] res = new Subspec[vec.size()];
		for(int i = 0; i < vec.size(); i++){
			res[i] = vec.elementAt(i);
		}
		
		return res;
		
	}
	
	public boolean isUserNameAvailable(String username){
		Database db = new Database();
		return db.isUsernameAvailable(username);
	}
	
	public String register(String name, String lastname, String mobileno, String username,
			String password, int role, int cityid, String pic){
		Database db = new Database();
		if(!db.isUsernameAvailable(username)){
			// "نام کاربری انتخابی مجاز نیست";
			return "\u0646\u0627\u0645 \u06a9\u0627\u0631\u0628\u0631\u06cc "
					+ "\u0627\u0646\u062a\u062e\u0627\u0628\u06cc "
					+ "\u0645\u062c\u0627\u0632 \u0646\u06cc\u0633\u062a";
		}
		byte[] picbyte = null;
		if(pic != null && pic.length() > 0){
			picbyte = Helper.getBytes(pic);
		}
		return db.register(name, lastname, mobileno, username, password, role, cityid, picbyte, "");
	}
	
	public String register2(String name, String lastname, String mobileno, String username,
			String password, int role, int cityid, String pic, String email){
		Database db = new Database();
		if(!db.isUsernameAvailable(username)){
			// "نام کاربری انتخابی مجاز نیست";
						return "\u0646\u0627\u0645 \u06a9\u0627\u0631\u0628\u0631\u06cc "
								+ "\u0627\u0646\u062a\u062e\u0627\u0628\u06cc "
								+ "\u0645\u062c\u0627\u0632 \u0646\u06cc\u0633\u062a";
		}
		byte[] picbyte = null;
		if(pic != null && pic.length() > 0){
			picbyte = Helper.getBytes(pic);
		}
		return db.register(name, lastname, mobileno, username, password, role, cityid, picbyte, email);
	}

	
	public int login(String username, String password){
		Database db = new Database();
		return db.loginAndGetRole(username, password);
	}
	
	public int login2(String username, String password, int officeId){
		Database db = new Database();
		int role = 0;
		if(!db.openConnection()) return role;

		try{
			if(db.checkUserPass(username, password)){
				role = db.getPermissionOnOffice(officeId, username);
			}
		}catch(Exception ex){
			role = 0;
		}
		return role;
	}

	
	public int registerOffice(String username, String password, int cityid,
			int spec, int subspec, String address, String tellNo, 
			double latitude, double longitude, String[] secretaryNames){
		Database db = new Database();
		int role;
		int id;
		if(db.openConnection()){
			try{
				int userId = db.getUserId(username);
				if(userId > 0){
					role = db.getRole(userId);
					if(role == Role.doctor){
						id = db.InsertInOffice(userId, spec, subspec, address, tellNo, 
								cityid, latitude, longitude);
						if(secretaryNames != null && secretaryNames.length > 0)
							db.InsertInSecretary(id, secretaryNames);
					}
				}
			}catch (SQLException e){
				e.printStackTrace();
			} finally{
				db.closeConnection();
			}
		}
		return -1;
	}
	
	public boolean updateOfficeInfo(String username, String password, int officeId,
			int cityId, int spec, int subspec, String address, String tellNo, String biography) {
		Database db = new Database();
		int perm;
		boolean ret = false;
		System.out.println("address = " + address);
		if(db.openConnection()){
			try{
				if(db.checkUserPass(username, password)){
					perm = db.getPermissionOnOffice(officeId, username);
					if(perm == Role.doctor || perm == Role.secretary){
						db.updateOffice(officeId, spec, subspec, address, tellNo, cityId);
						db.addBiography(officeId, biography);
						ret = true;
					}
				}
			}catch (SQLException e){
				ret = false;
			} finally{
				db.closeConnection();
			}
		}
		return ret;
	}
	
	public boolean addSecretaryToOffice(String username, String password, int officeId, String secretary){
		String[] sec = new String[1];
		sec[0] = secretary;
		boolean ret = false;
		Database db = new Database();
		int perm;
		try{
			if(db.openConnection()){
				if(db.checkUserPass(username, password)){
					perm = db.getPermissionOnOffice(officeId, username);
					if(perm == Role.doctor || perm == Role.secretary){
						db.InsertInSecretary(officeId, sec);
						ret = true;
					}
				}
			}
		} catch(SQLException e){
			ret = false;
		} finally{
			db.closeConnection();
		}
		return ret;
	}

	
	public boolean removeSecretaryFromOffice(String username, String password, int officeId, String secretary){
		boolean ret = false;
		Database db = new Database();
		int perm;
		try{
			if(db.openConnection()){
				if(db.checkUserPass(username, password)){
					perm = db.getPermissionOnOffice(officeId, username);
					if(perm == Role.doctor){
						db.removeFromSecretary(officeId, secretary);
						ret = true;
					}
				}
			}
		} catch(SQLException e){
			ret = false;
		} finally{
			db.closeConnection();
		}
		return ret;
	}

	public boolean addTurn(String username, String password, int officeId, 
			String date, int startHour, int startMin, int duration, int capacity){
		Database db = new Database();
		if(capacity <= 0 || duration <= 0 || startHour <=0 || startMin <= 0) return false;
		try{
			if(!db.openConnection()) return false;
			if(!db.checkUserPass(username, password)) return false;
			int perm = db.getPermissionOnOffice(officeId, username);
			if(perm != Role.doctor && perm != Role.secretary) return false;
			if(Helper.isBeforeToday(date)) return false;
			
			Turn t = new Turn();
			t.officeId = officeId;
			t.capacity = capacity;
			t.date = date;
			t.duration = duration;
			t.hour = startHour;
			t.min = startMin;
			t.reserved = 0;
			
			addTurnWithoutId(db, t);
		} catch(SQLException e){
			return false;
		}
		return true;
	}

	public boolean addTurnByDate(String username, String password, int officeId, String fromDate, 
			String toDate, int hour, int min, int duration, int capacity, String dayOfWeek ){
		PersianCalendar cal1 = Helper.getCalendarFromShortDate(fromDate);
		PersianCalendar cal2 = Helper.getCalendarFromShortDate(toDate);
		Vector<Integer> days = new Vector<Integer>();
		Vector<Turn> vec = new Vector<Turn>();
		Database db = new Database();
		int firstId = 0;
		if(capacity <= 0 || duration <= 0 || hour < 0 || min < 0) return false;
		if(cal1 == null || cal2 == null) return false;
		if(Helper.isBeforeToday(fromDate)) return false;
		if(cal1.after(cal2)) return false;
		
		if(!db.openConnection()) return false;
		firstId = db.getMaxTurnId() + 1;
		
		char[] temp = dayOfWeek.toCharArray();
		for(char ch : temp){
			days.addElement(ch - '0');
		}
		while(cal2.after(cal1) || cal2.equals(cal1)){
			int todayDayOfWeek = cal1.getPersianWeekDay();
			if(days.contains(todayDayOfWeek)){
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
			if(vec.size() > 0){
				db.addTurnBatch(vec);
				res = true;
			}
			else{
				res = false;
			}
		} catch (SQLException e) {
			res = false;
		} finally {
			db.closeConnection();
		}
		return res;
	}
	
	
//	public boolean addTurnBatch(String username, String password, int officeId, Rturn[] turns){
//		boolean res = true;
//		Database db = new Database();
//		Turn t = new Turn();
//		t.officeId = officeId;
//		t.reserved = 0;
//		try{
//			if(!db.openConnection()) return false;
//			if(!db.checkUserPass(username, password)) return false;
//			int perm = db.getPermissionOnOffice(officeId, username);
//			if(perm != Role.doctor && perm != Role.secretary) return false;
//		} catch(SQLException e) {
//			return false;
//		}
//		for(Rturn rt : turns){
//			Helper.setTurnValues(t, rt);
//			try {
//				addTurnWithoutId(db, t);
//			} catch (SQLException e) {
//				res= false;
//			}
//		}
//
//		return res;
//	}
	
	private void addTurnWithoutId(Database db, Turn t) throws SQLException{
		t.id = db.getMaxTurnId() + 1;
		db.addTurn(t);
	}
	
	public Turn[] getAllTurn(String username, String password, int officeId, String fromDate, String toDate){
		Database db = new Database();
		Turn[] res = null;
		if(Helper.checkDateBoundary(fromDate, toDate)){
			if(db.openConnection()){
				Vector<Turn> vec = db.getTurn(username, officeId, fromDate, toDate);
				res = new Turn[vec.size()];
				for(int i = 0; i < vec.size(); i++){
					res[i] = vec.elementAt(i);
				}
				db.closeConnection();
			}
		}
		
		return res;
	}
	
	public Turn[] getAllTurnFromToday(String username, String password, int officeId){
		Database db = new Database();
		Turn[] res = null;
		Date now = new Date();
		String fromDate = Helper.getShortDate(now);
		String toDate = Helper.getShortDateAfterSomeDay(now, 30);
		if(db.openConnection()){
			Vector<Turn> vec = db.getTurn(username, officeId, fromDate, toDate);
			res = new Turn[vec.size()];
			for(int i = 0; i < vec.size(); i++){
				
				res[i] = vec.elementAt(i);
			}
		}
		return res;
	}

	
	public boolean addBiography(String username, String password, int officeId, String biography){
		Database db = new Database();
		try{
			if(!db.openConnection()) return false;
			if(!db.checkUserPass(username, password)) return false;
			int perm = db.getPermissionOnOffice(officeId, username);
			if(perm != Role.doctor && perm != Role.secretary) return false;
			
			db.addBiography(officeId, biography);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	public String getBiography(String username, String password, int officeId){
		Database db = new Database();
		String str = "";
		try{
			if(!db.openConnection()) return str;
			if(!db.checkUserPass(username, password)) return str;
			
			str = db.getBiography(officeId);
		}catch(Exception e){
		}
		return str;
	}

	public int reserveForMe(String username, String password, int turnId,
			                int firstReservationId, int taskId, int numberOfTurns){
		Database db = new Database();
		Reservation_new r = new Reservation_new();
		try{
			if(db.openConnection()){
				if(db.checkUserPass(username, password)){
					int userId = db.getUserId(username);
					r.userId = userId;
					r.firstReservationId = firstReservationId;
					r.numberOfTurns = numberOfTurns;
					r.patientId = userId;
					r.payment = 0;
					r.taskId = taskId;
					r.turnId = turnId;
					r.price = db.getTaskPrice(taskId);
					
					r.id = 0;
					if(db.checkCapacity(r)){
						r.id = db.getMaxReservationId() + 1;
						db.decreseCapacity(r);
						db.reserveTurn(r);
					}
				}
			}
		}catch(SQLException e){
			r.id = 0;
			System.out.println(e.getMessage());
		}
		return r.id;
	}

	
	public int reserveForUser(String username, String password, int turnId,
			                  int firstReservationId, int taskId,
			                  int numberOfTurns, String patientUserName){
		Database db = new Database();
		Reservation_new r = new Reservation_new();
		try{
			if(db.openConnection()){
				if(db.checkUserPass(username, password)){
					
					r.userId = db.getUserId(username);
					r.firstReservationId = firstReservationId;
					r.numberOfTurns = numberOfTurns;
					r.patientId = db.getUserId(patientUserName);
					r.payment = 0;
					r.taskId = taskId;
					r.turnId = turnId;
					r.price = db.getTaskPrice(taskId);
					
					r.id = 0;
					if(db.checkCapacity(r)){
						r.id = db.getMaxReservationId() + 1;
						db.decreseCapacity(r);
						db.reserveTurn(r);
						
						Info_Reservation info = db.getUserOfficeTurn(r.id);
						Helper.sendReservationMessage(db, r.userId, info);
					}
				}
			}
		}catch(SQLException e){
			r.id = 0;
			System.out.println(e.getMessage());
		}
		return r.id;
	}
	
	public int reserveForGuest(String username, String password, int turnId,
							   int firstReservationId, int taskId,
			                   int numberOfTurns, String patientFirstName,
			                   String patientLastName, String patientPhoneNo,
			                   int patientCityId){
		Database db = new Database();
		Reservation_new r = new Reservation_new();
		try{
			if(db.openConnection()){
				if(db.checkUserPass(username, password)){
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
					if(db.checkCapacity(r)){
						r.id = db.getMaxReservationId() + 1;
						db.decreseCapacity(r);
						db.reserveTurn(r);
					}
				}
			}
		}catch(SQLException e){
			r.id = 0;
			System.out.println(e.getMessage());
		}
		return r.id;
	}

	
	public int reserveForUser2(String username, String password, int officeId,
			int turnId, int firstReservationId, int taskId,
			int numberOfTurns, String patientUserName, int price){
		Database db = new Database();
		Reservation_new r = new Reservation_new();
		try{
			if(db.openConnection()){
				if(db.checkUserPass(username, password)){

					r.userId = db.getUserId(username);
					r.firstReservationId = firstReservationId;
					r.numberOfTurns = numberOfTurns;
					r.patientId = db.getUserId(patientUserName);
					r.payment = 0;
					r.taskId = taskId;
					r.turnId = turnId;
					if(db.isHaveSecretaryPermission(username, password, officeId)){
						r.price = price;
					} else {
						r.price = db.getTaskPrice(taskId);
					}

					r.id = 0;
					if(db.checkCapacity(r)){
						r.id = db.getMaxReservationId() + 1;
						db.decreseCapacity(r);
						db.reserveTurn(r);

						Info_Reservation info = db.getUserOfficeTurn(r.id);
						Helper.sendReservationMessage(db, r.userId, info);
					}
				}
			}
		}catch(SQLException e){
			r.id = 0;
			System.out.println(e.getMessage());
		}
		return r.id;
	}

	public int reserveForGuest2(String username, String password, int officeId, 
			int turnId, int firstReservationId, int taskId,
			int numberOfTurns, String patientFirstName,
			String patientLastName, String patientPhoneNo,
			int patientCityId, int price){
		Database db = new Database();
		Reservation_new r = new Reservation_new();
		try{
			if(db.openConnection()){
				if(db.checkUserPass(username, password)){
					int guestId = db.insertGuest(patientFirstName, 
							patientLastName, patientPhoneNo, patientCityId);
					r.userId = db.getUserId(username);
					r.firstReservationId = firstReservationId;
					r.numberOfTurns = numberOfTurns;
					r.patientId = guestId;
					r.payment = 0;
					r.taskId = taskId;
					r.turnId = turnId;
					if(db.isHaveSecretaryPermission(username, password, officeId)){
						r.price = price;
					} else {
						r.price = db.getTaskPrice(taskId);
					}

					r.id = 0;
					if(db.checkCapacity(r)){
						r.id = db.getMaxReservationId() + 1;
						db.decreseCapacity(r);
						db.reserveTurn(r);
					}
				}
			}
		}catch(SQLException e){
			r.id = 0;
			System.out.println(e.getMessage());
		}
		return r.id;
	}
	
	public Reservation2[] getReservationByTurnId(String username, String password, int officeId, int turnId){
		Database db = new Database();
		Vector<Reservation2> vec;
		Reservation2[] res = null;
		try{
			if(!db.openConnection()) return null;
			if(!db.checkUserPass(username, password)) return null;
			int perm = db.getPermissionOnOffice(officeId, username);
			if(perm == Role.doctor || perm == Role.secretary) {
				vec = db.getReservationForAdmin(turnId);
			} else {
				vec = db.getReservationForUser(turnId, username);
			}
			res = new Reservation2[vec.size()];
			for(int i = 0; i < vec.size(); i++){
				res[i] = vec.elementAt(i);
			}
		}catch(Exception e){
			return res;
		}
		return res;
	}
	
	public Reservation4[] getReservationByUser(String username, String password, int officeId, int count, int index){
		Database db = new Database();
		Vector<Reservation4> vec = new Vector<Reservation4>();
		Reservation4[] res = null;
		try{
			if(!db.openConnection()) return null;
			if(!db.checkUserPass(username, password)) return null;
			
			vec = db.getReservation(username, officeId);
			int firstIndex = index * count;
			if(firstIndex >= vec.size()) return null;
			int size = Math.min(count, vec.size() - firstIndex);
			res = new Reservation4[size];
			for(int i = 0; i < res.length; i++){
				res[i] = vec.elementAt(i + firstIndex);
			}
		}catch(Exception e){
			return res;
		}
		return res;
	}

	public Reservation2[] getReservationByDate(String username, String password,
			                                   int officeId, String fromDate, String toDate){
		Database db = new Database();
		Vector<Reservation2> vec = new Vector<Reservation2>();
		Reservation2[] res = null;
		try{
			if(!Helper.checkDateBoundary(fromDate, toDate)) return null;
			if(!db.openConnection()) return null;
			if(db.checkUserPass(username, password)){
				int perm = db.getPermissionOnOffice(officeId, username);
				if(perm != Role.doctor && perm != Role.secretary){ 
					vec = db.getReservation(officeId, fromDate, toDate);
					res = new Reservation2[vec.size()];
					for(int i = 0; i < res.length; i++){
						res[i] = vec.elementAt(i);
					}
				}
			}
		}catch(Exception e){
			
		} finally{
			db.closeConnection();
		}
		return res;  
	}

	public User getUserInfo(String username, String password, int officeId){
		Database db = new Database();
		User user = User.getErrorUser();
		if(!db.openConnection()) return user;
		try{
			if(!db.checkUserPass(username, password)){
				user.username = "نام کاربری یا رمز عبور اشتباه است";
				user.name = "Username or Password is incorrect";
			} else {
				int userId = db.getUserId(username);
				user = db.getUserInfo(userId);
				if(user.role == Role.doctor)
					user.role = db.getPermissionOnOffice(officeId, username);
			}
		}catch(SQLException e){
			return user;
		}
		return user;
	}
	
	public User getUserInfoWithoutPic(String username, String password, int officeId){
		Database db = new Database();
		User user = User.getErrorUser();
		if(!db.openConnection()) return user;
		
		try{
			if(!db.checkUserPass(username, password)){
				user.username = "نام کاربری یا رمز عبور اشتباه است";
				user.name = "Username or Password is incorrect";
			}
			user = db.getUserInfoWithoutPic(username);
			if(user.role == Role.doctor)
				user.role = db.getPermissionOnOffice(officeId, username);
		}catch(SQLException e){
			return user;
		}
		return user;
	}
	
	public String updateUserInfo(String username, String password, String name, String lastname,
			String mobileno, int cityid){
		String res = "OK";
		Database db = new Database();
		if(!db.openConnection()){
			res = "خطای سمت سرور";
		}
		try{
			if(!db.checkUserPass(username, password)){
				res = "نام کاربری یا رمز عبور اشتباه است";
			}

			if(!db.updateUserInfo(username, name, lastname, mobileno, cityid)){
				res = "خطای سمت سرور";
			}
		} catch(SQLException e){
			res = "خطای سمت سرور";
		}
		return res;
	}
	
	public String updateUserInfo2(String username, String password, String name, String lastname,
			String mobileno, int cityid, String newPassword){
		String res = "OK";
		Database db = new Database();
		if(!db.openConnection()){
			res = "خطای سمت سرور";
		}
		try{
			if(!db.checkUserPass(username, password)){
				res = "نام کاربری یا رمز عبور اشتباه است";
			}

			if(!db.updateUserInfo(username, name, lastname, mobileno, cityid, newPassword)){
				res = "خطای سمت سرور";
			}
		} catch(SQLException e){
			res = "خطای سمت سرور";;
		}
		return res;
	}
	
	public String updateUserInfo3(String username, String password, String name, String lastname,
			String mobileno, int cityid, String newPassword, String email){
		String res = "OK";
		Database db = new Database();
		if(!db.openConnection()){
			res = "خطای سمت سرور";
		}
		try{
			if(!db.checkUserPass(username, password)){
				res = "نام کاربری یا رمز عبور اشتباه است";
			}

			if(!db.updateUserInfo(username, name, lastname, mobileno, cityid, newPassword, email)){
				res = "خطای سمت سرور";
			}
		} catch(SQLException e){
			res = "خطای سمت سرور";;
		}
		return res;
	}


		
	public boolean updateUserPic2(String username, String password, String pic){
		boolean result = false;
		Database db = new Database();
		if(!db.openConnection()){
			return false;
		}
		try{
			if(!db.checkUserPass(username, password)){
				result = false;
			}
			byte[] imageAsBytes = Helper.getBytes(pic);
			result = db.updateUserPic(username, imageAsBytes);
			result = true;
		} catch(SQLException e){

			result = false;
		}		
		return result;
	}


	public boolean updateUserPassword(String username, String password, String newPassword){
		Database db = new Database();
		if(!db.openConnection()) return false;
		try{
			if(!db.checkUserPass(username, password)) return false;
			return db.updateUserPassword(username, newPassword);
		} catch(SQLException e){
			return false;
		}		
	}
	
	public Office getOfficeInfo(String username, String password, int officeId){
		Office office = new Office();
		Database db = new Database();
		if(!db.openConnection()) {
			return office;
		}
		try {
			if(!db.checkUserPass(username, password)){
				return office;
			}
			office = db.getOfficeInfo(officeId);
		} catch (SQLException e) {
			return office;
		}
		return office;
	}
	
	public int[] getDoctorOffice(String doctorUsername){
		Database db = new Database();
		
		if(!db.openConnection()) return null;
		try {
			Vector<Integer> vec = db.getDoctorOffice(doctorUsername);
			int[] res = new int[vec.size()];
			for(int i = 0; i < vec.size(); i++){
				res[i] = vec.get(i);
			}
			return res;
		} catch (SQLException e) {
			return null;
		}
	}

	public String getUserPic(String username, String password){
		Database db = new Database();
		byte[] res = null;
		if(!db.openConnection()) return null;
		try {
			if(!db.checkUserPass(username, password)) res = null;
			int userId = db.getUserId(username);
			res = db.getUserPic(userId);
		} catch (SQLException e) {
			res = null;
		} finally{
			db.closeConnection();
		}
		String ret = Helper.getString(res);
		return ret;
	}
	
	public String getDoctorPic(String username, String password, int officeId){
		Database db = new Database();
		byte[] res = null;
		if(!db.openConnection()) return null;
		try {
			if(!db.checkUserPass(username, password)) res = null;
			res = db.getDrPic(officeId);
		} catch (SQLException e) {
			res = null;
		} finally{
			db.closeConnection();
		}
		String ret = Helper.getString(res);
		return ret;
	}
	
	
	public boolean updateOfficeLocation(String username, String password, int officeId, 
			String latitude, String longitude) {
		Database db = new Database();
		double lat, lng;
		boolean ret = false;
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					lat = Double.parseDouble(latitude);
					lng = Double.parseDouble(longitude);
					db.updateOffice(officeId, lat, lng);
					ret = true;
				}
			}catch (SQLException e){
			} finally{
				db.closeConnection();
			}
		}
		return ret;
	}

	public boolean cancelReservation(String username, String password, int reservationId){
		boolean res = false;
		Database db = new Database();
		Info_Reservation info;
		try{
			if(db.openConnection()){
				if(db.checkUserPass(username, password)){
					int userId = db.getUserId(username);
					info = db.getUserOfficeTurn(reservationId);
					if(info.username == null) return false;
					
					int role = db.getPermissionOnOffice(info.officeId, username);
					if(role == Role.secretary || role == Role.doctor
							|| info.username.equals(username) || info.patientId == userId){
						Helper.sendCancelationMessage(db, userId, info);
						db.removeFromReserve(reservationId);
						db.increseCapacity(info.turnId, info.numberOfTurns);
						res = true;
					}
				}
			}
		}catch(SQLException e){
			res = false;
			System.out.println(e.getMessage());
		}
		return res;
	}
	
	public Info_Reservation2[] getReservation(String username, String password, int officeId, int turnId){
		Database db = new Database();
		Vector<Info_Reservation2> vec;
		
		Info_Reservation2[] result = null;
		if(!db.openConnection()) return null;
		try {
			if(db.checkUserPass(username, password)){
				int role = db.getPermissionOnOffice(officeId, username);
				if(role == Role.doctor || role == Role.secretary){
					vec = db.getAllReservation(turnId, "");
				} else {
					vec = db.getAllReservation(turnId, username);
				}
				result = new Info_Reservation2[vec.size()];
				for(int i = 0; i < vec.size(); i++){
					result[i] = vec.elementAt(i);
				}
			}
		} catch (SQLException e) {
			
		} finally {
			db.closeConnection();
		}
		return result;
	}

	public Info_User[] searchUser(String username, String name, String lastName, String mobileNo){
		Info_User[] result = null;
		Database db = new Database();
		
		if(db.openConnection()){
			try {
				Vector<Info_User> vec = db.searchUser(username, name, lastName, mobileNo);
				result = new Info_User[vec.size()];
				for(int i = 0; i < vec.size(); i++){
					result[i] = vec.elementAt(i);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		
		return result;
	}
	
	public Task[] getAllTasks(String username, String password, int officeId){
		Task[] res = null;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					Vector<Task> vec = db.getAllTasks(officeId);
					res = new Task[vec.size()];
					for(int i = 0; i < res.length; i++){
						res[i] = vec.elementAt(i);
					}
				}
			}catch(SQLException e){
				
			} finally{
				db.closeConnection();
			}
		}
		return res;
	}
	
	public boolean sendMessage(String username, String password, int officeId, String receiver,
			String subject, String message){
		boolean res = true;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.checkUserPass(username, password)){
					int senderId = db.getUserId(username);
					int receiverId = db.getUserId(receiver);
					String dateStr = Helper.getTodayShortDate();
					String timeStr = Helper.getCurrentTime();
					
					db.sendMessage(officeId, senderId, receiverId, subject, message, dateStr, timeStr);
					
				}
			}catch(SQLException e){
				res = false;
			}
		} else{
			res = false;
		}
		return res;
	}
	
	public boolean sendMessageBatch(String username, String password, int officeId,
			String[] receivers, String[] phoneNo, String subject, String message){
		boolean res = true;
		Database db = new Database();
		Vector<String> receiverVec;
		Vector<String> phoneVec;
		if(db.openConnection()){
			try{
				if(db.checkUserPass(username, password)){
					receiverVec = Helper.removeDuplicates(receivers);
					phoneVec = Helper.removeDuplicates(phoneNo);
					int[] receiverIds = new int[receiverVec.size()];
					for(int i = 0; i < receiverIds.length; i++){
						receiverIds[i] = db.getUserId(receiverVec.elementAt(i));
					}
					int senderId = db.getUserId(username);
					String dateStr = Helper.getTodayShortDate();
					String timeStr = Helper.getCurrentTime();
					
					
					
					db.sendMessageBatch(officeId, senderId, receiverIds, subject, message, dateStr, timeStr);
					Helper.sendSMS(officeId, senderId, phoneVec, subject, message, dateStr, timeStr);
				}
			}catch(SQLException e){
				res = false;
			}
		} else{
			res = false;
		}
		return res;
	}
	
	public Info_Message[] getUnreadMessages(String username, String password, int officeId){
		return getMessages(username, password, officeId, true);
	}
	
	public Info_Message[] getAllMessages(String username, String password, int officeId){
		return getMessages(username, password, officeId, false);
	}
	
	private Info_Message[] getMessages(String username, String password, int officeId, boolean onlyUnread){
		Info_Message[] res = null;
		Vector<Info_Message> vec;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.checkUserPass(username, password)){
					vec = db.getMessages(username, officeId, onlyUnread);
					res = new Info_Message[vec.size()];
					for(int i = 0; i < res.length; i++){
						res[i] = vec.elementAt(i);
					}
				}
			}catch(SQLException e){
				
			} finally {
				db.closeConnection();
			}
		}
		return res;
	}
	
	public void setMessageRead(String username, String password, int officeId, int messageId){
		Database db = new Database();
		
		if(db.openConnection()){
			int receiverId = db.getUserId(username);
			try{
				if(db.checkUserPass(username, password)){
					db.setMessageRead(officeId, receiverId, messageId);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				
			} finally{
				db.closeConnection();
			}
		}
	}
	
	public void setAllMessageRead(String username, String password, int officeId){
		Database db = new Database();
		
		if(db.openConnection()){
			int receiverId = db.getUserId(username);
			try{
				if(db.checkUserPass(username, password)){
					db.setAllMessageRead(officeId, receiverId);
				}
			} catch (SQLException e) {
				
			} finally{
				db.closeConnection();
			}
		}
		
	}


	public boolean removeTurn(String username, String password, int officeId, int turnId){
		boolean res = false;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					db.removeFromTurn(officeId, turnId);
					res = true;
				}
			} catch (SQLException e) {
				res = false;
			}  
			finally{
				db.closeConnection();
			}
		}
		return res;
	}

	public UserTurn[] getPatientTurnInfoByDate(String username, String password,
			int officeId, String fromDate, String toDate){
		Database db = new Database();
		Vector<UserTurn> vec = new Vector<UserTurn>();
		UserTurn[] res = null;
		if(!Helper.checkDateBoundary(fromDate, toDate))
			return res;
		try{
			if(!db.openConnection()) return null;
			if(!db.isHaveSecretaryPermission(username, password, officeId)) return null;

			vec = db.getUserTurn(officeId, fromDate, toDate);
			res = new UserTurn[vec.size()];
			for(int i = 0; i < vec.size(); i++){
				res[i] = vec.elementAt(i);
			}
		}catch(Exception e){
			return res;
		} finally{
			db.closeConnection();
		}
		return res;  
	}
	
//	public Info_Office[] getOfficesBySpec(String username, String password, int specId){
//		Vector<Info_Office> vec;
//		Info_Office[] offices = null;
//		Database db = new Database();
//		if(!db.openConnection()) {
//			return offices;
//		}
//		try {
//			if(!db.checkUserPass(username, password)){
//				return offices;
//			}
//			vec = db.getOfficeBySpec(specId);
//			offices = new Office[vec.size()];
//			for(int i = 0; i < offices.length; i++){
//				offices[i] = vec.elementAt(i);
//			}
//		} catch (SQLException e) {
//			return offices;
//		}
//		return offices;
//	}
	
	
	public Office[] getOfficesBySpec(String username, String password, int specId){
		Office[] offices = null;
		Vector<Office> vec = new Vector<Office>();
		Database db = new Database();
		if(!db.openConnection()) {
			return offices;
		}
		try {
			if(!db.checkUserPass(username, password)){
				return offices;
			}
			vec = db.getOfficeBySpec(specId);
			offices = new Office[vec.size()];
			for(int i = 0; i < offices.length; i++){
				offices[i] = vec.elementAt(i);
			}
		} catch (SQLException e) {
			return offices;
		}
		return offices;
	}
	
	public boolean removeMessage(String username, String password, int officeId, int messageId){
		boolean res= false;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.checkUserPass(username, password)){
					int userid = db.getUserId(username);
					db.removeMessage(officeId, userid, messageId);
					res = true;
				}
			}catch(SQLException e){
				res = false;
			}finally{
				db.closeConnection();
			}
		} 
		return res;
	}
	
	public Info_Patient[] getTodayPatient(String username, String password, int officeId){
		Info_Patient[] res = null;
		Vector<Info_Patient> vec = null;
		Database db = new Database();
		if(db.openConnection()){
			String today = Helper.getTodayShortDate();
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					vec = db.getOneDayPatient(officeId, today);
					res = new Info_Patient[vec.size()];
					for(int i = 0; i < res.length; i++){
						res[i] = vec.elementAt(i);
					}
				}
			} catch(Exception e){
				System.out.println(e.getMessage());
			}finally{
				db.closeConnection();
			}
		}
		return res;
	}
	
	public boolean reception(String username, String password, int officeId, int reservationId, 
			int payment, String description){
		boolean res = true;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					db.reception(reservationId, payment, description);
				}
			}catch(SQLException e){
				res = false;
			}
		}
		return res;
	}
	
	public Info_patientFile[] getPatientFile(String username,
			String password, int officeId, String patientUsername){
		Database db = new Database();
		Vector<Info_patientFile> vec = null;
		Info_patientFile[] res = null;
		if(db.openConnection()){
//			System.out.println();
			if(db.isHavePatientPermission(username, password, officeId, patientUsername)){
				try {
					vec = db.getPatientAllTurn(patientUsername, officeId);
					res = new Info_patientFile[vec.size()];
					for(int i = 0; i < res.length; i++){
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
	
	public PhotoDesc getGalleryPic(String username, String password, int officeId, int picId){
		PhotoDesc res = null;
		Database db = new Database();
		System.out.println("*********** picId = " + picId);
		if(db.openConnection()){
			System.out.println("*********** 1 ");
			if(db.isHavePatientPermission(username, password, officeId, username)){
				System.out.println("*********** 2 ");
				try{
					System.out.println("*********** 3 ");

					res = db.getGalleryPic(officeId, picId);
					System.out.println("*********** 4 ");

				}catch(SQLException e) {
					System.out.println("*********** 5 : " + e.getMessage());

					e.printStackTrace();
				} finally {
					db.closeConnection();
				}
			}
		}
		System.out.println("*********** 6 ");

		return res;
	}
	
	public int setGalleryPic(String username, String password, int officeId,
			String pic, String description){
		int picId = 0;
		String dateTime = Helper.getTodayShortDate() + " " + Helper.getCurrentTime();
		Database db = new Database();
		
		if(pic == null || pic.length() <= 0) return 0;
		
		if(db.openConnection()){
			picId = db.getMaxPicId() + 1;
			if(db.isHaveSecretaryPermission(username, password, officeId)){
				try {
					if(db.isGalleryPicsNumberLessThanMax(officeId)){
						db.insertIntoGallery(officeId, picId, pic, description, dateTime);
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
	
	public String[] getAllGalleyPicId(String username, String password, int officeId){
		String[] res = null;
		Vector<Integer> vec = null;
		Database db = new Database();
		if(db.openConnection()){
			if(db.isHavePatientPermission(username, password, officeId, username)){
				try {
					vec = db.getAllPicId(officeId);
				} catch (SQLException e) {
					
				} finally {
					db.closeConnection();
				}
				res = new String[vec.size()];
				for(int i = 0; i < res.length; i++){
					res[i] = String.valueOf(vec.elementAt(i));
				}
			}
		}
		
		return res;
	}
	
	public void deleteFromGallery(String username, String password, int officeId, int picId){
		Database db = new Database();
		if(db.openConnection()){
			if(db.isHaveSecretaryPermission(username, password, officeId)){
				try {
					db.deleteFromGallery(officeId, picId);
				} catch (SQLException e) {
					
				} finally {
					db.closeConnection();
				}
			}
		}
	}
	
	public void changeGalleryPicDescription(String username, String password, int officeId, int picId, String description){
		Database db = new Database();
		if(db.openConnection()){
			if(db.isHaveSecretaryPermission(username, password, officeId)){
				try {
					db.changeGalleryPicDescription(officeId, picId, description);
				} catch (SQLException e) {
					
				} finally {
					db.closeConnection();
				}
			}
		}
	}
	
	public TaskGroup[] getTaskGroups(String username, String password, int officeId){
		TaskGroup[] res = null;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.checkUserPass(username, password)){
					Vector<TaskGroup> vec = db.getTaskGroups(officeId);
					res = new TaskGroup[vec.size()];
					for(int i = 0; i < res.length; i++){
						res[i] = vec.elementAt(i);
					}
				}
			}catch(SQLException e){
				System.out.println(e.getMessage());
			} finally{
				db.closeConnection();
			}
		}
		return res;
	}
	
	public Task[] getTasks(String username, String password, int officeId, int taskGroupId){
		Task[] res = null;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.checkUserPass(username, password)){
					Vector<Task> vec = db.getAllTasks(officeId, taskGroupId);
					res = new Task[vec.size()];
					for(int i = 0; i < res.length; i++){
						res[i] = vec.elementAt(i);
					}
				}
			}catch(SQLException e){
				
			} finally{
				db.closeConnection();
			}
		}
		return res;
	}
	
	public int addTaskGroup(String username, String password, int officeId, String taskGroupName){
		Database db = new Database();
		int id = 0;
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					id = db.addTaskGroup(taskGroupName, officeId);
				}
			}catch(SQLException e){
				System.out.println(e.getMessage());
			} finally{
				db.closeConnection();
			}
		}
		return id;
	}
	
	public String addTaskGroup2(String username, String password, int officeId, String taskGroupName){
		Database db = new Database();
		String res = "First";
		int id = 0;
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					id = db.addTaskGroup(taskGroupName, officeId);
					res = "OK";
				}
			}catch(SQLException e){
				System.out.println(e.getMessage());
				res = e.getMessage();
			} finally{
				db.closeConnection();
			}
		}
		return res + ", id = " + id;
	}

	public String updateTaskGroup(String username, String password, int officeId, int taskGroupId, String taskGroupName) {
		String res;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					if(!db.isAnyoneReserveTaskGroup(taskGroupId)){
						db.updateTaskGroup(taskGroupId, taskGroupName);
						res = "OK";
					} else {
						res = "شما اجازه تغییرنام این گروه را ندارید زیرا قبلاً حداقل یک نفر در این گروه نوبت رزرو کرده است";
					}
				} else {
					res = "شما مجوز دسترسی به این بخش را ندارید";
				}
			}catch(SQLException e){
				res = "خطای غیر منتظره در سمت سرور پیش آمده است";
			} finally {
				db.closeConnection();
			}
		} else {
			res = "خطای غیر منتظره در سمت سرور پیش آمده است";
		}
		return res;
	}
	
	
	public String deleteTaskGroup(String username, String password, int officeId, int taskGroupId) {
		String res;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					if(!db.isAnyoneReserveTaskGroup(taskGroupId)){
						db.deleteTaskGroup(taskGroupId);
						res = "OK";
					} else {
						res = "شما اجازه پاک کردن این گروه را ندارید زیرا قبلاً حداقل یک نفر در این گروه نوبت رزرو کرده است";
					}
				} else {
					res = "شما مجوز دسترسی به این بخش را ندارید";
				}
			}catch(SQLException e){
				res = "خطای غیر منتظره در سمت سرور پیش آمده است";
			} finally {
				db.closeConnection();
			}
		} else {
			res = "خطای غیر منتظره در سمت سرور پیش آمده است";
		}
		return res;
	}
	
	public int addTask(String username, String password, int officeId, String name, int taskGroupId, int price) {
		int id = 0;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					id = db.addTask(name, officeId, taskGroupId, price);
				}
			}catch(SQLException e){
				System.out.println(e.getMessage());
			} finally {
				db.closeConnection();
			}
		}
		return id;
	}
	
	public String updateTaskPrice(String username, String password, int officeId, int taskId, int price) {
		String res;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					db.updateTaskPrice(taskId, price);
					res = "OK";
				} else {
					res = "شما مجوز دسترسی به این بخش را ندارید";
				}
			}catch(SQLException e){
				res = "خطای غیر منتظره در سمت سرور پیش آمده است";
			} finally {
				db.closeConnection();
			}
		} else {
			res = "خطای غیر منتظره در سمت سرور پیش آمده است";
		}
		return res;
	}
	
	public String updateTaskName(String username, String password, int officeId, int taskId, String taskName) {
		String res;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					if(!db.isAnyoneReserveTask(taskId)){
						db.updateTaskName(taskId, taskName);
						res = "OK";
					} else {
						res = "شما اجازه  تغییرنام این عملیات را ندارید زیرا قبلاً حداقل یک نفر در این گروه نوبت رزرو کرده است";
					}
				} else {
					res = "شما مجوز دسترسی به این بخش را ندارید";
				}
			}catch(SQLException e){
				res = "خطای غیر منتظره در سمت سرور پیش آمده است";
			} finally {
				db.closeConnection();
			}
		} else {
			res = "خطای غیر منتظره در سمت سرور پیش آمده است";
		}
		return res;
	}
	
	public String deleteTask(String username, String password, int officeId, int taskId) {
		String res;
		Database db = new Database();
		if(db.openConnection()){
			try{
				if(db.isHaveSecretaryPermission(username, password, officeId)){
					if(!db.isAnyoneReserveTask(taskId)){
						db.deleteTask(taskId);
						res = "OK";
					} else {
						res = "شما اجازه پاک کردن این عملیات را ندارید زیرا قبلاً حداقل یک نفر در این گروه نوبت رزرو کرده است";
					}
				} else {
					res = "شما مجوز دسترسی به این بخش را ندارید";
				}
			}catch(SQLException e){
				res = "خطای غیر منتظره در سمت سرور پیش آمده است";
			} finally {
				db.closeConnection();
			}
		}
		else {
			res = "خطای غیر منتظره در سمت سرور پیش آمده است";
		}
		return res;
	}

	public Office getOfficeInfo2(int officeId){
		Office office = new Office();
		Database db = new Database();
		if(!db.openConnection()) {
			return office;
		}
		try {
			office = db.getOfficeInfo(officeId);
		} catch (SQLException e) {
			return office;
		}
		return office;
	}
}


