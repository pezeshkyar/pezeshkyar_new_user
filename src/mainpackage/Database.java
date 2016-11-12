package mainpackage;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import constant.Constants;
import primitives.City;
import primitives.CityProvince;
import primitives.DoctorInfo;
import primitives.Info_Message;
import primitives.Info_Patient;
import primitives.Info_Reservation;
import primitives.Info_Reservation2;
import primitives.Info_User;
import primitives.Info_patientFile;
import primitives.Office;
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
import primitives.Version;

public class Database {
	Connection connection;
	private static final String GUEST_USERNAME = "guest";
	private static final String GUEST_PASSWORD = "8512046384";

	public boolean openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.print("Please attach the database library to project : ");
			System.err.println(e.getMessage());
			return false;
		}

		try {
			String databaseName;
			switch (Constants.CURRENT_VERSION) {
			case Version.ARAYESHYAR:
				databaseName = "arayeshyar";
				break;
			case Version.PIRAYESHYAR:
				databaseName = "pirayeshyar";
				break;
			case Version.PEZESHKYAR:
				databaseName = "pezeshkyar_all_in_one";
				break;
			default:
				databaseName = "unknown";
			}
			if (Constants.CUSTOMER_NAME.length() > 0)
				databaseName += ("_" + Constants.CUSTOMER_NAME);
			
			connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + databaseName, "root", "dreadlord");

		} catch (SQLException e) {
			System.out.print("Error Opening connection: ");
			System.out.println(e.getMessage());
			return false;
		}

		return true;
	}

	public void closeConnection() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Throwable t) {

		}
	}

	public Vector<Province> getAllProvinceNames() {
		Vector<Province> res = new Vector<Province>();
		String query = "select id, name from province";
		ResultSet rs;
		if (!openConnection())
			return null;
		try {
			Statement stmt = connection.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				Province p = new Province();
				p.id = rs.getInt(1);
				p.name = rs.getString(2);
				res.addElement(p);
			}
		} catch (SQLException e) {
		}
		closeConnection();
		return res;
	}

	public Vector<City> getAllCityNames() {
		Vector<City> res = new Vector<City>();
		String query = "select id, provinceid, name from city";
		ResultSet rs;
		if (!openConnection())
			return null;
		try {
			Statement stmt = connection.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				City c = new City();
				c.id = rs.getInt(1);
				c.provinceId = rs.getInt(2);
				c.name = rs.getString(3);
				res.addElement(c);
			}
		} catch (SQLException e) {
		}
		closeConnection();
		return res;
	}

	public Vector<City> getCityByProvince(int provinceid) {
		Vector<City> res = new Vector<City>();
		String query = "select id, provinceid, name from city where provinceid = " + provinceid;
		ResultSet rs;
		if (!openConnection())
			return null;
		try {
			Statement stmt = connection.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				City c = new City();
				c.id = rs.getInt(1);
				c.provinceId = rs.getInt(2);
				c.name = rs.getString(3);
				res.addElement(c);
			}
		} catch (SQLException e) {
		}
		closeConnection();
		return res;
	}

	public boolean isUsernameAvailable(String username, int officeId) {
		String query = "select * from user where username = ? and officeid = ?";
		boolean res;
		if (!openConnection())
			return false;
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, username);
			ps.setInt(2, officeId);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				res = false;
			else
				res = true;
		} catch (SQLException e) {
			res = false;
		}
		closeConnection();
		return res;
	}

	public String register(String name, String lastname, String mobileno, String username, String password,
			int officeId, int cityid, byte[] pic, String email) {
		String msg = "OK";
		String query = "insert into user(username, password, name, lastname, mobileno, "
				+ "officeid, cityid, photo, id, email) values(?,?,?,?,?,?,?,?,?,?)";
		if (!openConnection()) {
			msg = "\u0645\u0634\u06a9\u0644\u06cc \u062f\u0631 \u067e\u0627\u06cc\u06af\u0627\u0647 "
					+ "\u062f\u0627\u062f\u0647 \u0633\u0645\u062a \u0633\u0631\u0648\u0631 "
					+ "\u0628\u0647 \u0648\u062c\u0648\u062f \u0622\u0645\u062f\u0647 " + "\u0627\u0633\u062a";
		} else {
			int id = getMaxId("user") + 1;
			try {
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setString(1, username);
				ps.setString(2, password);
				ps.setString(3, name);
				ps.setString(4, lastname);
				ps.setString(5, mobileno);
				ps.setInt(6, officeId);
				ps.setInt(7, cityid);
				if (pic == null || pic.length == 0) {
					ps.setNull(8, java.sql.Types.BLOB);
				} else {
					ByteArrayInputStream temp = new ByteArrayInputStream(pic);
					ps.setBinaryStream(8, temp, pic.length);
				}
				ps.setInt(9, id);
				ps.setString(10, email);
				ps.execute();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
				msg = e.getMessage();
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
		}
		closeConnection();
		return msg;
	}

	public int getUserId(String username, int officeId) {
		int id = -1;
		String query = "select id from user where username = ? and officeid = ?";
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, username);
			ps.setInt(2, officeId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			id = -1;
		}
		return id;
	}

	public int InsertInOffice(int spec, int subspec, String address, String tellNo, int cityid, double latitude,
			double longitude, int timeQuantum, String bioghraphy) throws SQLException {
		int id = getMaxOfficdId() + 1;
		String query = "insert into office(id, spec, subspec, " + " address, phoneno, cityid, latitude, longitude,"
				+ " timequantum, biography) " + " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, id);
		ps.setInt(2, spec);
		ps.setInt(3, subspec);
		ps.setString(4, address);
		ps.setString(5, tellNo);
		ps.setInt(6, cityid);
		ps.setDouble(7, latitude);
		ps.setDouble(8, longitude);
		ps.setInt(9, timeQuantum);
		ps.setString(10, bioghraphy);

		ps.executeUpdate();
		ps.close();
		return id;
	}

	public void InsertInSecretary(int officeid, int secretaryId) throws SQLException {
		String query = "insert into secretary(officeid, secretaryid) values (?, ?)";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeid);
		ps.setInt(2, secretaryId);

		ps.executeUpdate();
	}

	public User InsertInSecretary2(int officeid, int secretaryId) {
		try {
			InsertInSecretary(officeid, secretaryId);
			return getUserInfoWithoutPic(secretaryId);
		} catch (SQLException e) {
			return User.getErrorUser();
		}
	}

	public void removeFromSecretary(int officeid, String secName) throws SQLException {
		int secId = getUserId(secName, officeid);
		String query = "delete from secretary where officeid = ? and secretaryid = ?";
		PreparedStatement ps = connection.prepareStatement(query);

		try {
			ps.setInt(1, officeid);
			ps.setInt(2, secId);

			ps.executeUpdate();
		} catch (Exception e) {
		}
		ps.close();
	}

	private int getMaxOfficdId() {
		return getMaxId("office");
	}

	public void updateOffice(int officeid, int spec, int subspec, String address, String tellNo, int cityid)
			throws SQLException {

		String query = "update office set spec = ?, subspec = ?, address = ?, "
				+ " phoneno = ?, cityid = ? WHERE id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, spec);
		ps.setInt(2, subspec);
		ps.setString(3, address);
		ps.setString(4, tellNo);
		ps.setInt(5, cityid);
		ps.setInt(6, officeid);

		ps.executeUpdate();
		ps.close();
	}

	public void updateOffice(int officeid, double latitude, double longitude) throws SQLException {

		String query = "update office set latitude = ?, longitude = ? " + " WHERE id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setDouble(1, latitude);
		ps.setDouble(2, longitude);
		ps.setInt(3, officeid);

		ps.executeUpdate();
		ps.close();
	}

	public int getPermissionOnOffice(int officeId, String username) {
		if (username.equals(GUEST_USERNAME))
			return Role.guest;

		int userId = getUserId(username, officeId);

		return getPermissionOnOffice(officeId, userId);
	}

	public int getPermissionOnOffice(int officeId, int userId) {
		int perm = Role.patient;
		String query1 = "Select * from doctoroffice where officeid = ? and doctorid = ?";
		String query2 = "Select * from secretary where officeid = ? and secretaryid = ?";
		PreparedStatement ps1, ps2;
		ResultSet rs1, rs2;
		try {
			ps1 = connection.prepareStatement(query1);
			ps1.setInt(1, officeId);
			ps1.setInt(2, userId);
			rs1 = ps1.executeQuery();

			if (rs1.next()) {
				perm = Role.doctor;
			}
			rs1.close();
			ps1.close();
			if (perm != Role.doctor) {
				ps2 = connection.prepareStatement(query2);
				ps2.setInt(1, officeId);
				ps2.setInt(2, userId);
				rs2 = ps2.executeQuery();
				if (rs2.next())
					perm = Role.secretary;
				rs2.close();
				ps2.close();
			}
		} catch (SQLException e) {
			perm = Role.patient;
		}

		return perm;
	}

	public boolean checkUserPass(String username, String password, int officeId) throws SQLException {
		if (username.equals(GUEST_USERNAME) && password.equals(GUEST_PASSWORD)) {
			return true;
		}
		String query = "select * from user where username = ? " + "and password = ? and officeid = ?";
		boolean ret = false;
		PreparedStatement ps;
		ps = connection.prepareStatement(query);
		ps.setString(1, username);
		ps.setString(2, password);
		ps.setInt(3, officeId);

		ResultSet rs = ps.executeQuery();
		if (rs.next())
			ret = true;
		ps.close();
		return ret;
	}

	public Vector<Spec> getAllSpec() {
		String query = "select id, spec from spec";
		Vector<Spec> res = new Vector<Spec>();

		ResultSet rs;
		if (!openConnection())
			return res;
		try {
			Statement stmt = connection.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				Spec s = new Spec();
				s.id = rs.getInt(1);
				s.name = rs.getString(2);
				res.addElement(s);
			}
		} catch (SQLException e) {
		}
		closeConnection();
		return res;
	}

	public Vector<Subspec> getSubspec(int specId) {
		String query = "select id, specid, subspec from subspec where specid = ?";
		Vector<Subspec> res = new Vector<Subspec>();

		ResultSet rs;
		if (!openConnection())
			return res;
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setInt(1, specId);
			rs = stmt.executeQuery();

			while (rs.next()) {
				Subspec s = new Subspec();
				s.id = rs.getInt(1);
				s.specId = rs.getInt(2);
				s.name = rs.getString(3);
				res.addElement(s);
			}
		} catch (SQLException e) {
		}
		closeConnection();
		return res;
	}

	public int getMaxTurnId() {
		return getMaxId("turn");
	}

	public void addTurn(Turn t) throws SQLException {
		String query = "insert into turn (id, date, starthour, startminute, capacity,"
				+ " reserved, officeid, duration) values (?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, t.id);
		ps.setString(2, t.date);
		ps.setInt(3, t.hour);
		ps.setInt(4, t.min);
		ps.setInt(5, t.capacity);
		ps.setInt(6, 0);
		ps.setInt(7, t.officeId);
		ps.setInt(8, t.duration);

		ps.executeUpdate();
	}

	public void addTurnBatch(Vector<Turn> turns) throws SQLException {
		String query = "insert into turn (id, date, starthour, startminute, capacity,"
				+ " reserved, officeid, duration) values (?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement ps = connection.prepareStatement(query);
		for (Turn t : turns) {
			ps.setInt(1, t.id);
			ps.setString(2, t.date);
			ps.setInt(3, t.hour);
			ps.setInt(4, t.min);
			ps.setInt(5, t.capacity);
			ps.setInt(6, t.reserved);
			ps.setInt(7, t.officeId);
			ps.setInt(8, t.duration);

			ps.addBatch();
		}
		ps.executeBatch();
	}

	public Vector<Turn> getTurn(String username, int officeId, String fromDate, String toDate) {
		String query = "select id, date, starthour, startminute, capacity, reserved, officeid, duration "
				+ " from turn where officeid = ? and date between ? and ? order by date";
		Vector<Turn> vec = new Vector<Turn>();

		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, officeId);
			ps.setString(2, fromDate);
			ps.setString(3, toDate);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Turn t = new Turn();
				t.id = rs.getInt(1);
				t.date = rs.getString(2);
				t.hour = rs.getInt(3);
				t.min = rs.getInt(4);
				t.capacity = rs.getInt(5);
				t.reserved = rs.getInt(6);
				t.officeId = rs.getInt(7);
				t.duration = rs.getInt(8);

				t.longDate = Helper.convertShortDateToLong(t.date);
				vec.add(t);
			}
		} catch (SQLException e) {

		}
		for (Turn t : vec) {
			t.isReserved = isReserved(username, officeId, t.id);
		}

		return vec;
	}

	public void addBiography(int officeid, String bio) throws SQLException {
		String query = "update office set biography = ? where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setString(1, bio);
		ps.setInt(2, officeid);

		ps.executeUpdate();
	}

	public String getBiography(int officeid) throws SQLException {
		String query = "select biography from office where officeid = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeid);

		ResultSet rs = ps.executeQuery();
		if (rs.next())
			return rs.getString(1);
		return "";
	}

	public void reserveTurn(Reservation_new res) throws SQLException {
		String query = "insert into reserve (id, userid, turnid, taskid, numberofturns,"
				+ " patientid, firstturn, payment, price) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, res.id);
		ps.setInt(2, res.userId);
		ps.setInt(3, res.turnId);
		ps.setInt(4, res.taskId);
		ps.setInt(5, res.numberOfTurns);
		ps.setInt(6, res.patientId);
		ps.setInt(7, res.firstReservationId);
		ps.setInt(8, res.payment);
		ps.setInt(9, res.price);

		ps.executeUpdate();
	}

	public boolean checkCapacity(Reservation_new res) throws SQLException {
		String query = "select (capacity - reserved) from turn where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, res.turnId);
		ResultSet rs = ps.executeQuery();
		boolean sw = false;
		if (rs.next()) {
			int remain = rs.getInt(1);
			if (remain >= res.numberOfTurns)
				sw = true;
		}
		return sw;
	}

	public void decreseCapacity(Reservation_new res) throws SQLException {
		String query = "update turn set reserved = reserved + ? where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, res.numberOfTurns);
		ps.setInt(2, res.turnId);
		ps.executeUpdate();
	}

	public void increseCapacity(int turnId, int numberOfTurns) throws SQLException {
		String query = "update turn set reserved = reserved - ? where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, numberOfTurns);
		ps.setInt(2, turnId);
		ps.executeUpdate();
	}

	public int getMaxReservationId() {
		return getMaxId("reserve");
	}

	public int getMaxPicId() {
		int id = 0;
		String query = "select max(maxpicid) from maxgallerypic";
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if (rs.next())
				id = rs.getInt(1);
		} catch (SQLException e) {
			id = 0;
		}
		if (id == 0) {
			return getMaxId("gallery");
		}
		return id;
	}

	private int getMaxId(String tableName) {
		int id = 0;
		String query = "select max(id) from " + tableName;
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if (rs.next())
				id = rs.getInt(1);
		} catch (SQLException e) {
			id = 0;
		}
		return id;
	}

	public Vector<Reservation2> getReservationForAdmin(int turnId) throws SQLException {
		return getReservation(turnId, false, 0);
	}

	public Vector<Reservation2> getReservationForUser(int turnId, String username) throws SQLException {
		int officeId = getOfficeIdByTurnId(turnId);
		int userId = getUserId(username, officeId);
		return getReservation(turnId, true, userId);
	}

	private Vector<Reservation2> getReservation(int turnId, boolean isUser, int userId) throws SQLException {
		String query = "select reserve.id, u1.username, reserve.turnid, "
				+ "reserve.taskid, reserve.numberofturns, u2.name, u2.lastname, "
				+ "u2.mobileno, reserve.firstturn, reserve.payment "
				+ "from reserve  join user as u1 on reserve.userid = u1.id "
				+ "join user as u2 on reserve.patientid = u2.id " + "where turnid = ? ";
		if (isUser)
			query += " and u1.id = ?";
		Vector<Reservation2> vec = new Vector<Reservation2>();
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, turnId);
		if (isUser)
			ps.setInt(2, userId);

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Reservation2 r = new Reservation2();
			r.id = rs.getInt(1);
			r.username = rs.getString(2);
			r.turnId = rs.getInt(3);
			r.taskId = rs.getInt(4);
			r.numberOfTurns = rs.getInt(5);
			r.patientFirstName = rs.getString(6);
			r.patientLastName = rs.getString(7);
			r.patientPhoneNo = rs.getString(8);
			r.firstReservationId = rs.getInt(9);
			r.payment = rs.getInt(10);

			vec.addElement(r);
		}

		return vec;
	}

	public DoctorInfo getDoctorInfo(int officeId) throws SQLException {
		DoctorInfo res = new DoctorInfo();
		String query = "select user.username, user.name, user.lastname, office.spec, office.subspec,"
				+ "spec.spec, subspec.subspec " + "from office join doctoroffice on office.id = doctoroffice.officeid "
				+ "join user on doctoroffice.doctorid=user.id " + "join spec on office.spec=spec.id "
				+ "join subspec on office.subspec = subspec.id " + "where office.id = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			res.username = rs.getString(1);
			res.firstName = rs.getString(2);
			res.lastName = rs.getString(3);
			res.specId = rs.getInt(4);
			res.subSpecId = rs.getInt(5);
			res.specName = rs.getString(6);
			res.subSpecName = rs.getString(7);
		}
		return res;
	}

	public Vector<Reservation4> getReservation(String username, int officeId) throws SQLException {
		DoctorInfo Dinf = getDoctorInfo(officeId);
		int userId = getUserId(username, officeId);
		String query = "select reserve.id, reserve.turnid, reserve.taskid,"
				+ " reserve.numberofturns, user.name, user.lastname, user.mobileno, "
				+ " reserve.firstturn, reserve.payment, turn.date, turn.starthour, "
				+ "turn.startminute, turn.duration, task.name " + "from reserve join turn on reserve.turnid = turn.id "
				+ "join user on reserve.patientid = user.id " + "join task on reserve.taskid = task.id "
				+ "where (reserve.userid = ? or reserve.patientid = ?) and turn.officeId = ? "
				+ "order by turn.date desc ";
		Vector<Reservation4> vec = new Vector<Reservation4>();
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, userId);
		ps.setInt(2, userId);
		ps.setInt(3, officeId);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Reservation4 r = new Reservation4();
			r.doctorFirstName = Dinf.firstName;
			r.doctorLastName = Dinf.lastName;
			r.doctorSpec = Dinf.specName;
			r.doctorSubSpec = Dinf.subSpecName;
			r.doctorUsername = Dinf.username;
			r.username = username;
			r.reservationId = rs.getInt(1);
			r.turnId = rs.getInt(2);
			r.taskId = rs.getInt(3);
			r.numberOfTurns = rs.getInt(4);
			r.patientFirstName = rs.getString(5);
			r.patientLastName = rs.getString(6);
			r.patientPhoneNo = rs.getString(7);
			r.firstReservationId = rs.getInt(8);
			r.payment = rs.getInt(9);
			r.date = rs.getString(10);
			r.longDate = Helper.convertShortDateToLong(r.date);
			int hour = rs.getInt(11);
			int min = rs.getInt(12);
			int duration = rs.getInt(13);
			r.time = Helper.getTime(hour, min, duration);
			r.taskName = rs.getString(14);
			vec.addElement(r);
		}

		return vec;
	}

	public Vector<Reservation2> getReservation(int officeId, String fromDate, String toDate) throws SQLException {
		String query = "select reserve.id, u1.username, turnid, taskid, numberofturns, "
				+ "u2.name, u2.lastname, u2.mobileno, firstturn, payment, date "
				+ "from reserve join turn on reserve.turnid = turn.id " + "join user as u1 on reserve.userid = user.id "
				+ "join user as u2 on reserve.patientid = user.id " + "where officeId = ? and date between ? and ?";
		Vector<Reservation2> vec = new Vector<Reservation2>();
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ps.setString(2, fromDate);
		ps.setString(3, toDate);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Reservation2 r = new Reservation2();
			r.id = rs.getInt(1);
			r.username = rs.getString(2);
			r.turnId = rs.getInt(3);
			r.taskId = rs.getInt(4);
			r.numberOfTurns = rs.getInt(5);
			r.patientFirstName = rs.getString(6);
			r.patientLastName = rs.getString(7);
			r.patientPhoneNo = rs.getString(8);
			r.firstReservationId = rs.getInt(9);
			r.payment = rs.getInt(10);
			r.date = rs.getString(11);

			vec.addElement(r);
		}

		return vec;
	}

	public User getUserInfo(int userId) throws SQLException {
		User res = new User();
		String query = "select username, mobileno, name, lastname, cityid, photo, email " + "from user where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			res.username = rs.getString(1);
			res.mobileno = rs.getString(2);
			res.name = rs.getString(3);
			res.lastname = rs.getString(4);
			res.cityid = rs.getInt(5);
			res.pic = null;

			Blob blob = rs.getBlob(6);
			if (blob != null) {
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				blob.free();
				res.pic = Helper.getString(blobAsBytes);
			}
			res.email = rs.getString(7);
		}
		addProvinceToUser(res);
		return res;
	}

	public User getUserInfoWithoutPic(int userId) throws SQLException {
		User res = User.getErrorUser();
		String query = "select username, mobileno, name, lastname, cityid " + "from user where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			res.username = rs.getString(1);
			res.mobileno = rs.getString(2);
			res.name = rs.getString(3);
			res.lastname = rs.getString(4);
			res.cityid = rs.getInt(5);
			res.pic = null;
		}
		addProvinceToUser(res);
		return res;
	}

	private void addProvinceToUser(User u) throws SQLException {
		CityProvince cp = getProvinceOfCity(u.cityid);
		u.provinceid = cp.provinceId;
		u.province = cp.province;
		u.city = cp.city;
	}

	public boolean updateUserPic(int userId, byte[] pic) throws SQLException {
		String query = "update user set photo = ? where id = ?";
		if (!openConnection()) {
			return false;
		} else {
			PreparedStatement ps = connection.prepareStatement(query);

			if (pic == null || pic.length == 0) {
				ps.setNull(1, java.sql.Types.BLOB);
			} else {
				ByteArrayInputStream temp = new ByteArrayInputStream(pic);
				ps.setBinaryStream(1, temp, pic.length);
			}
			ps.setInt(2, userId);
			ps.executeUpdate();
			ps.close();
		}
		closeConnection();
		return true;
	}

	public boolean updateUserInfo(int userId, String name, String lastname, String mobileno, int cityid)
			throws SQLException {
		boolean result = true;
		String query = "update user set name = ?, lastname = ?, mobileno = ?, " + "cityid = ? where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);

		ps.setString(1, name);
		ps.setString(2, lastname);
		ps.setString(3, mobileno);
		ps.setInt(4, cityid);
		ps.setInt(5, userId);
		ps.executeUpdate();
		ps.close();
		closeConnection();
		return result;
	}

	public boolean updateUserInfo(int userId, String name, String lastname, String mobileno, int cityid,
			String newPassword) throws SQLException {
		boolean result = true;
		String query = "update user set name = ?, lastname = ?, mobileno = ?, "
				+ "cityid = ?, password = ? where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);

		ps.setString(1, name);
		ps.setString(2, lastname);
		ps.setString(3, mobileno);
		ps.setInt(4, cityid);
		ps.setString(5, newPassword);
		ps.setInt(6, userId);
		ps.executeUpdate();
		ps.close();
		closeConnection();
		return result;
	}

	public boolean updateUserInfo(int userId, String name, String lastname, String mobileno, int cityid,
			String newPassword, String email) throws SQLException {
		boolean result = true;
		String query = "update user set name = ?, lastname = ?, mobileno = ?, "
				+ "cityid = ?, password = ?, email = ? where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);

		ps.setString(1, name);
		ps.setString(2, lastname);
		ps.setString(3, mobileno);
		ps.setInt(4, cityid);
		ps.setString(5, newPassword);
		ps.setString(6, email);
		ps.setInt(7, userId);
		ps.executeUpdate();
		ps.close();
		closeConnection();
		return result;
	}

	public boolean updateUserPassword(int userId, String newPassword) throws SQLException {
		String query = "update user set password = ? where id = ?";
		if (!openConnection()) {
			return false;
		} else {
			PreparedStatement ps = connection.prepareStatement(query);

			ps.setString(1, newPassword);
			ps.setInt(2, userId);
			ps.executeUpdate();
			ps.close();
		}
		closeConnection();
		return true;
	}

	public String getCityName(int id) throws SQLException {
		String city = "";
		String query = "select name from city where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);

		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			city = rs.getString(1);
		}
		return city;
	}

	public String getSpec(int id) throws SQLException {
		String city = "";
		String query = "select spec from spec where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);

		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			city = rs.getString(1);
		}
		return city;
	}

	public String getSubSpec(int id) throws SQLException {
		String city = "";
		String query = "select subspec from subspec where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);

		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			city = rs.getString(1);
		}
		return city;
	}

	public Office getOfficeInfo(int id) throws SQLException {
		Office office = new Office();
		office.id = id;
		String query = "select user.username, office.spec, office.subspec, "
				+ "office.address, office.phoneno, office.cityid, "
				+ "office.latitude, office.longitude, office.timequantum, "
				+ "office.biography, user.name, user.lastname "
				+ "from office join doctoroffice on office.id = doctoroffice.officeid "
				+ " join user on doctoroffice.doctorid = user.id " + " where office.id = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			office.doctorUsername = rs.getString(1);
			office.specId = rs.getInt(2);
			office.subspecId = rs.getInt(3);
			office.address = rs.getString(4);
			office.tellNo = rs.getString(5);
			office.cityId = rs.getInt(6);
			office.latitude = rs.getDouble(7);
			office.longitude = rs.getDouble(8);
			office.timeQuantum = rs.getInt(9);
			office.biograophy = rs.getString(10);
			office.doctorName = rs.getString(11);
			office.doctorLastName = rs.getString(12);
		}
		office.spec = getSpec(office.specId);
		office.subSpec = getSubSpec(office.subspecId);
		CityProvince cp = getProvinceOfCity(office.cityId);
		office.city = cp.city;
		office.province = cp.province;
		office.provinceId = cp.provinceId;

		return office;
	}

	public CityProvince getProvinceOfCity(int cityId) throws SQLException {
		CityProvince cp = new CityProvince();
		String query = "select province.id, province.name, city.id, city.name "
				+ "from city, province where province.id = city.provinceid and city.id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, cityId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			cp.provinceId = rs.getInt(1);
			cp.province = rs.getString(2);
			cp.cityId = rs.getInt(1);
			cp.city = rs.getString(4);
		}
		return cp;
	}

	public Vector<Integer> getDoctorOffice(String username) throws SQLException {
		Vector<Integer> vec = new Vector<>();
		String query = "select id from office where doctorname = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			vec.add(rs.getInt(1));
		}
		return vec;
	}

	public byte[] getDrPic(int officeId) throws SQLException {
		String query = "select photo from user " + "join doctoroffice on user.id = doctoroffice.doctorid "
				+ "where doctoroffice.officeid = ? ";
		byte[] res = null;

		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			Blob blob = rs.getBlob(1);
			if (blob != null) {
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				blob.free();
				res = blobAsBytes;
			}
		}
		return res;
	}

	public byte[] getUserPic(int userId) throws SQLException {
		String query = "select photo from user where id = ? ";
		byte[] res = null;

		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			Blob blob = rs.getBlob(1);
			if (blob != null) {
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				blob.free();
				res = blobAsBytes;
			}
		}
		return res;
	}

	public boolean isReserved(String username, int officeId, int turnId) {
		boolean res = false;
		String query = " select * from reserve join user on user.id = reserve.userid "
				+ "where user.username = ? and user.officeId = ? and reserve.turnid = ? ";
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setString(1, username);
			ps.setInt(2, officeId);
			ps.setInt(3, turnId);
			ResultSet rs = ps.executeQuery();

			if (rs.next())
				res = true;
			else
				res = false;
		} catch (Exception e) {
			res = false;
		}

		return res;
	}

	public Info_Reservation getUserOfficeTurn(int reservationId) throws SQLException {
		Info_Reservation res = new Info_Reservation();
		String query = "select u1.username, reserve.turnid, reserve.numberofturns,"
				+ " turn.officeid, u2.id, turn.date, turn.starthour, " + " turn.startminute, turn.duration "
				+ "from reserve join turn on reserve.turnid = turn.id " + " join user as u1 on reserve.userid = u1.id "
				+ "join user as u2 on reserve.patientId = u2.id " + "where reserve.id = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, reservationId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			res.username = rs.getString(1);
			res.turnId = rs.getInt(2);
			res.numberOfTurns = rs.getInt(3);
			res.officeId = rs.getInt(4);
			res.patientId = rs.getInt(5);
			res.date = rs.getString(6);
			res.longDate = Helper.convertShortDateToLong(res.date);
			int h = rs.getInt(7);
			int m = rs.getInt(8);
			int d = rs.getInt(9);
			res.time = Helper.getTime(h, m, d);
		}
		return res;
	}

	public void removeFromReserve(int reservatinId) throws SQLException {
		String query = "delete from reserve where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, reservatinId);
		ps.executeUpdate();
	}

	public Vector<Info_Reservation2> getAllReservation(int turnId, boolean isForAUser, int userId) throws SQLException {
		Vector<Info_Reservation2> vec = new Vector<Info_Reservation2>();
		String query = "select id, patientname, numberofturns "
				+ "from reserve join turn on turn.id = reserve.turnid where turn.id = ? ";
		if (isForAUser)
			query += " and (userid = ? or patientid = ?)";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, turnId);
		if (isForAUser) {
			ps.setInt(2, userId);
			ps.setInt(3, userId);
		}

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Info_Reservation2 temp = new Info_Reservation2();
			temp.id = rs.getInt(1);
			temp.patientName = rs.getString(2);
			temp.numberOfTurns = rs.getInt(3);

			vec.addElement(temp);
		}
		return vec;
	}

	public Vector<Info_User> searchUserWithoutPic(String username, String name, String lastName, String mobileNo,
			int officeId) throws SQLException {
		String query = "select username, user.name, lastname, mobileno, cityid, city.name "
				+ "from user join city on user.cityid = city.id where user.officeId = ? and ";
		if (!username.isEmpty())
			query += "username like '%" + username + "%' and ";
		if (!name.isEmpty())
			query += "user.name like '%" + name + "%' and ";
		if (!lastName.isEmpty())
			query += "lastname like '%" + lastName + "%' and ";
		if (!mobileNo.isEmpty())
			query += "mobileNo like '%" + mobileNo + "%' and ";
		query += " username is not null ";

		Vector<Info_User> vec = new Vector<Info_User>();
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Info_User info = new Info_User();
			info.username = rs.getString(1);
			info.name = rs.getString(2);
			info.lastname = rs.getString(3);
			info.mobileno = rs.getString(4);
			info.cityId = rs.getInt(5);
			info.pic = null;
			info.city = rs.getString(6);

			vec.addElement(info);
		}
		return vec;
	}

	public int insertGuest(String firstName, String lastName, String mobileNo, int cityId, int officeId)
			throws SQLException {
		int id = getMaxId("user") + 1;
		String query = "insert into user(id, username, password, mobileno, name,"
				+ " lastname, officeid, cityid, photo) values(?, NULL, NULL, ?, ?, ?, ?, ?, NULL)";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, id);
		ps.setString(2, mobileNo);
		ps.setString(3, firstName);
		ps.setString(4, lastName);
		ps.setInt(5, officeId);
		ps.setInt(6, cityId);
		ps.executeUpdate();
		return id;
	}

	public Vector<Task> getAllTasks(int officeId) throws SQLException {
		Vector<Task> vec = new Vector<Task>();
		String query = "select id, name, price from task where officeid = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Task t = new Task();
			t.id = rs.getInt(1);
			t.name = rs.getString(2);
			t.price = rs.getInt(3);
			t.officeId = officeId;

			vec.addElement(t);
		}
		return vec;
	}

	public Vector<Info_Message> getMessages(int userId, int officeId, boolean onlyUnread) throws SQLException {
		Vector<Info_Message> vec = new Vector<Info_Message>();
		String query = "select M.id, M.subject, M.message, M.date, M.time, " + "U1.username, U1.name, U1.lastname "
				+ "from message as M join user as U2 on M.receiverid = U2.id "
				+ "join user as U1 on M.senderid = U1.id " + "where U2.id = ? and M.officeid = ? ";
		if (onlyUnread)
			query += " and M.isread = 0 ";
		query += "order by M.date desc, M.time desc ";

		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, userId);
		ps.setInt(2, officeId);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Info_Message info = new Info_Message();
			info.id = rs.getInt(1);
			info.subject = rs.getString(2);
			info.message = rs.getString(3);
			info.date = rs.getString(4);
			info.time = rs.getString(5);
			info.senderUsername = rs.getString(6);
			info.senderFirstName = rs.getString(7);
			info.senderLastName = rs.getString(8);

			vec.addElement(info);
		}

		return vec;
	}

	public void sendMessage(int officeId, int senderid, int receiverid, String subject, String message, String date,
			String time) throws SQLException {
		int[] recieverIds = new int[1];
		recieverIds[0] = receiverid;
		sendMessageBatch(officeId, senderid, recieverIds, subject, message, date, time);
	}

	public void sendMessageBatch(int officeId, int senderid, int[] receiverid, String subject, String message,
			String date, String time) throws SQLException {
		String query = "insert into message(id, officeid, senderid, receiverid, "
				+ "isread, subject, message, date, time) " + "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";

		int id = getMaxId("message") + 1;
		PreparedStatement ps = connection.prepareStatement(query);
		for (int i : receiverid) {
			ps.setInt(1, id++);
			ps.setInt(2, officeId);
			ps.setInt(3, senderid);
			ps.setInt(4, i);
			ps.setInt(5, 0); // isread
			ps.setString(6, subject);
			ps.setString(7, message);
			ps.setString(8, date);
			ps.setString(9, time);

			ps.addBatch();
		}
		ps.executeBatch();
	}

	public void setMessageRead(int officeId, int userId, int messageId) throws SQLException {
		String query = "update message set isread = 1 where id = ? and receiverid = ? and officeid = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, messageId);
		ps.setInt(2, userId);
		ps.setInt(3, officeId);

		ps.executeUpdate();
	}

	public void setAllMessageRead(int officeId, int receiverId) throws SQLException {
		String query = "update message set isread = 1 where receiverid = ? and officeid = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, receiverId);
		ps.setInt(2, officeId);

		ps.executeUpdate();
	}

	public void removeFromTurn(int officeId, int turnId) throws SQLException {
		String query1 = "delete from reserve where turnid = ?";
		String query = "delete from turn where id = ?";
		// officeid ro baraye in gozashtam ke ye vaght doctore X nobate Y ro
		// hazf nakone.
		// sath dastresi ham dare to Websevices.java check mishe

		PreparedStatement ps = connection.prepareStatement(query1);
		ps.setInt(1, turnId);
		ps.executeUpdate();
		ps.close();

		ps = connection.prepareStatement(query);
		ps.setInt(1, turnId);
		ps.executeUpdate();
	}

	public Turn getTurnById(int id) throws SQLException {
		Turn turn = new Turn();
		String query = "select id, date, starthour, startminute, duration,"
				+ " capacity, reserved, officeid  from turn where id = ?";

		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			turn.id = rs.getInt(1);
			turn.date = rs.getString(2);
			turn.hour = rs.getInt(3);
			turn.min = rs.getInt(4);
			turn.duration = rs.getInt(5);
			turn.capacity = rs.getInt(6);
			turn.reserved = rs.getInt(7);
			turn.officeId = rs.getInt(8);
		}
		return turn;
	}

	public Vector<UserTurn> getUserTurn(int officeId, String fromDate, String toDate) throws SQLException {
		String query = "select u1.username, u2.name, u2.lastname, u2.mobileno, "
				+ "reserve.numberofturns, turn.date, turn.starthour, turn.startminute, "
				+ "turn.duration, turn.capacity, turn.reserved, reserve.id, turn.id, "
				+ "task.id, task.name, reserve.price, u2.username "
				+ "from reserve join turn on reserve.turnid = turn.id " + "join user as u1 on reserve.userid = u1.id "
				+ "join user as u2 on reserve.patientid = u2.id " + "join task on reserve.taskid = task.id "
				+ "where turn.officeId = ? and date between ? and ?";
		Vector<UserTurn> vec = new Vector<UserTurn>();
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ps.setString(2, fromDate);
		ps.setString(3, toDate);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			UserTurn ut = new UserTurn();
			ut.username = rs.getString(1);
			ut.patientFirstName = rs.getString(2);
			ut.patientLastName = rs.getString(3);
			ut.patientPhoneNo = rs.getString(4);
			ut.numberOfTurns = rs.getInt(5);
			ut.shortDate = rs.getString(6);
			ut.longDate = Helper.convertShortDateToLong(ut.shortDate);
			ut.hour = rs.getInt(7);
			ut.min = rs.getInt(8);
			ut.duration = rs.getInt(9);
			ut.capacity = rs.getInt(10);
			ut.reserved = rs.getInt(11);
			ut.reservationId = rs.getInt(12);
			ut.turnId = rs.getInt(13);
			ut.taskId = rs.getInt(14);
			ut.taskName = rs.getString(15);
			ut.price = rs.getInt(16);
			ut.patientUsername = rs.getString(17);

			vec.addElement(ut);
		}

		return vec;
	}

	public void removeMessage(int officeId, int userId, int messageId) throws SQLException {
		String query = "delete from message where id = ? and receiverid = ? and officeid = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, messageId);
		ps.setInt(2, userId);
		ps.setInt(3, officeId);
		ps.executeUpdate();
	}

	public Vector<Info_Patient> getOneDayPatient(int officeId, String date) throws SQLException {
		String query = "select user.username, user.name, user.lastname, user.mobileno, "
				+ "reserve.id, reserve.firstturn, reserve.taskid, task.name, "
				+ "reserve.payment, reserve.description, taskgroup.id, taskgroup.name " + "from reserve "
				+ "join turn on turn.id = reserve.turnid " + "join user on reserve.patientid = user.id "
				+ "join task on reserve.taskid = task.id " + "join taskgroup on task.taskgroup = taskgroup.id "
				+ "where turn.officeid = ? and turn.date = ?";

		Vector<Info_Patient> vec = new Vector<Info_Patient>();
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ps.setString(2, date);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Info_Patient info = new Info_Patient();
			info.username = rs.getString(1);
			info.firstName = rs.getString(2);
			info.lastName = rs.getString(3);
			info.mobileNo = rs.getString(4);
			info.reservationId = rs.getInt(5);
			info.firstReservationId = rs.getInt(6);
			info.taskId = rs.getInt(7);
			info.taskName = rs.getString(8);
			info.payment = rs.getInt(9);
			info.description = rs.getString(10);
			info.taskGroupId = rs.getInt(11);
			info.taskGroupName = rs.getString(12);

			vec.addElement(info);
		}
		return vec;
	}

	public void reception(int reservationId, int payment, String description) throws SQLException {
		String query = "update reserve set payment = ?, description = ? where " + "id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, payment);
		ps.setString(2, description);
		ps.setInt(3, reservationId);

		ps.executeUpdate();
	}

	public boolean isHaveDoctorPermission(String username, String password, int officeId) {
		try {
			if (checkUserPass(username, password, officeId)) {
				int role = getPermissionOnOffice(officeId, username);
				if (role == Role.doctor) {
					return true;
				}
			}
		} catch (SQLException e) {

		}
		return false;
	}

	public boolean isHaveSecretaryPermission(String username, String password, int officeId) {
		try {
			if (checkUserPass(username, password, officeId)) {
				int role = getPermissionOnOffice(officeId, username);
				if (role == Role.doctor || role == Role.secretary) {
					return true;
				}
			}
		} catch (SQLException e) {

		}
		return false;
	}

	public boolean isHavePatientPermission(String username, String password, int officeId, String patientUsername) {
		try {
			if (checkUserPass(username, password, officeId)) {
				if (username.equals(patientUsername))
					return true;
				int role = getPermissionOnOffice(officeId, username);
				if (role == Role.doctor || role == Role.secretary) {
					return true;
				}
			}
		} catch (SQLException e) {

		}
		return false;
	}

	public Vector<Info_patientFile> getPatientAllTurn(String username, int officeId) throws SQLException {
		Vector<Info_patientFile> vec = new Vector<Info_patientFile>();
		int userId = getUserId(username, officeId);
		String query = "select reserve.id, reserve.taskid, turn.date, "
				+ "turn.startHour, turn.startMinute, turn.duration, " + "task.name, reserve.price, "
				+ "reserve.description, reserve.payment, reserve.firstturn " + "from reserve "
				+ "join turn on reserve.turnid = turn.id " + "join task on reserve.taskid = task.id "
				+ "where reserve.patientid = ? and turn.officeId = ? " + "order by turn.date ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, userId);
		ps.setInt(2, officeId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Info_patientFile pf = new Info_patientFile();
			pf.reservationId = rs.getInt(1);
			pf.taskId = rs.getInt(2);
			pf.date = rs.getString(3);
			pf.longDate = Helper.convertShortDateToLong(pf.date);
			int h = rs.getInt(4);
			int m = rs.getInt(5);
			int d = rs.getInt(6);
			pf.time = Helper.getTime(h, m, d);
			pf.taskName = rs.getString(7);
			pf.price = rs.getInt(8);
			pf.description = rs.getString(9);
			pf.payment = rs.getInt(10);
			pf.firstReservationId = rs.getInt(11);

			vec.addElement(pf);

		}
		return vec;
	}

	public void insertIntoGallery(int officeId, int picId, String picStr, String description, String date)
			throws SQLException {
		String query = "insert into gallery(id, officeid, photo, " + "description, date) values(?, ?, ?, ?, ?) ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, picId);
		ps.setInt(2, officeId);
		byte[] pic = Helper.getBytes(picStr);
		if (pic == null || pic.length == 0) {
			ps.setNull(3, java.sql.Types.BLOB);
		} else {
			ByteArrayInputStream temp1 = new ByteArrayInputStream(pic);
			ps.setBinaryStream(3, temp1, pic.length);
		}
		ps.setString(4, description);
		ps.setString(5, date);
		ps.executeUpdate();
		updateLastPicId(officeId, picId);
	}

	public void updateLastPicId(int officeId, int picId) {
		String query = "update maxgallerypic set maxpicid = ? where officeid = ?";
		boolean run = true;
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, picId);
			ps.setInt(2, officeId);
			int res = ps.executeUpdate();
			if (res <= 0)
				run = false;
		} catch (SQLException e) {
			run = false;
		}

		if (run)
			return;

		query = "insert into maxgallerypic(officeid, maxpic, maxpicid) values(?, ?, ?)";
		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, officeId);
			ps.setInt(2, Constants.DEFAULT_GALLERY_PIC_NO);
			ps.setInt(3, picId);
			ps.executeUpdate();
		} catch (SQLException e) {
			run = false;
		}
	}

	public PhotoDesc getGalleryPic(int officeId, int picId) throws SQLException {
		String query = "select photo, description, date from gallery where id = ? and officeid = ? ";
		PhotoDesc res = null;

		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, picId);
		ps.setInt(2, officeId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			res = new PhotoDesc();
			res.id = picId;
			Blob blob = rs.getBlob(1);
			if (blob != null) {
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				blob.free();
				res.photo = Helper.getString(blobAsBytes);
			}
			res.description = rs.getString(2);
			res.date = rs.getString(3);
		}
		return res;
	}

	public Vector<Integer> getAllPicId(int officeId) throws SQLException {
		String query = "select id from gallery where officeid = ? ";
		Vector<Integer> res = new Vector<Integer>();
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			res.add(rs.getInt(1));
		}

		return res;
	}

	public void deleteFromGallery(int officeId, int picId) throws SQLException {
		String query = "delete from gallery where id = ? and officeid = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, picId);
		ps.setInt(2, officeId);
		ps.executeUpdate();
	}

	public void changeGalleryPicDescription(int officeId, int picId, String description) throws SQLException {
		String query = "update gallery set description = ? where id = ? and officeid = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setString(1, description);
		ps.setInt(2, picId);
		ps.setInt(3, officeId);
		ps.executeUpdate();
	}

	public int getTaskPrice(int taskId) throws SQLException {
		int price = 0;
		String query = "select price from task where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, taskId);
		ResultSet rs = ps.executeQuery();
		if (rs.next())
			price = rs.getInt(1);
		return price;
	}

	public Vector<TaskGroup> getTaskGroups(int officeId) throws SQLException {
		Vector<TaskGroup> vec = new Vector<TaskGroup>();
		String query = "select id, name from taskgroup where officeid = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			TaskGroup t = new TaskGroup();
			t.id = rs.getInt(1);
			t.name = rs.getString(2);
			t.officeId = officeId;

			vec.addElement(t);
		}
		return vec;
	}

	public Vector<Task> getAllTasks(int officeId, int taskGroup) throws SQLException {
		Vector<Task> vec = new Vector<Task>();
		String query = "select id, name, price from task where officeid = ? and taskgroup = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ps.setInt(2, taskGroup);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Task t = new Task();
			t.id = rs.getInt(1);
			t.name = rs.getString(2);
			t.price = rs.getInt(3);
			t.officeId = officeId;
			t.taskGroupId = taskGroup;

			vec.addElement(t);
		}
		return vec;
	}

	public int addTaskGroup(String name, int officeId) throws SQLException {
		int id = getMaxId("taskgroup") + 1;
		String query = "insert into taskgroup(id, name, officeid) values(?, ?, ?)";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, id);
		ps.setString(2, name);
		ps.setInt(3, officeId);
		ps.executeUpdate();
		return id;
	}

	public void updateTaskGroup(int id, String name) throws SQLException {
		String query = "update taskgroup set name = ? where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setString(1, name);
		ps.setInt(2, id);
		ps.executeUpdate();
	}

	public void deleteTaskGroup(int id) throws SQLException {
		String query = "delete from taskgroup where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, id);
		ps.executeUpdate();
	}

	public int addTask(String name, int officeId, int taskGroup, int price) throws SQLException {
		int id = getMaxId("task") + 1;
		String query = "insert into task(id, name, price, officeid, taskgroup) values(?, ?, ?, ?, ?)";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, id);
		ps.setString(2, name);
		ps.setInt(3, price);
		ps.setInt(4, officeId);
		ps.setInt(5, taskGroup);
		ps.executeUpdate();
		return id;
	}

	public void updateTaskPrice(int id, int price) throws SQLException {
		String query = "update task set price = ? where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, price);
		ps.setInt(2, id);
		ps.executeUpdate();
	}

	public void updateTaskName(int id, String taskName) throws SQLException {
		String query = "update task set name = ? where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setString(1, taskName);
		ps.setInt(2, id);
		ps.executeUpdate();
	}

	public void deleteTask(int id) throws SQLException {
		String query = "delete from task where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, id);
		ps.executeUpdate();
	}

	public boolean isAnyoneReserveTaskGroup(int taskGroup) throws SQLException {
		String query = "select * from reserve join task on reserve.taskid = task.id " + " where task.taskgroup = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, taskGroup);
		ResultSet rs = ps.executeQuery();
		if (rs.next())
			return true;
		else
			return false;
	}

	public boolean isAnyoneReserveTask(int taskId) throws SQLException {
		String query = "select * from reserve join task on reserve.taskid = task.id " + " where task.id = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, taskId);
		ResultSet rs = ps.executeQuery();
		if (rs.next())
			return true;
		else
			return false;
	}

	public boolean isGalleryPicsNumberLessThanMax(int officeId) throws SQLException {
		int maxno = 0;
		int picno = 0;
		String query = " SELECT maxpic " + " from maxgallerypic WHERE officeid = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			maxno = rs.getInt(1);
		} else {
			maxno = constant.Constants.DEFAULT_GALLERY_PIC_NO;
		}

		query = "select count(*) from gallery where officeid = ?";
		ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		rs = ps.executeQuery();
		if (rs.next()) {
			picno = rs.getInt(1);
		}

		return (picno < maxno);
	}

	public Vector<Info_User> getAllSecretary(int officeId) throws SQLException {
		String query = "select username, user.name, lastname, mobileno, cityid, city.name "
				+ "from user join city on user.cityid = city.id " + " join secretary on user.id = secretaryid "
				+ " where secretary.officeid = ? ";

		Vector<Info_User> vec = new Vector<Info_User>();
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Info_User info = new Info_User();
			info.username = rs.getString(1);
			info.name = rs.getString(2);
			info.lastname = rs.getString(3);
			info.mobileno = rs.getString(4);
			info.cityId = rs.getInt(5);
			info.pic = null;
			info.city = rs.getString(6);

			vec.addElement(info);
		}
		return vec;
	}

	public Info_User getUserInfo(String username, int officeId) throws SQLException {
		String query = "select username, user.name, lastname, mobileno, cityid, photo, city.name "
				+ "from user join city on user.cityid = city.id where username = ? and officeId = ?";

		Info_User info = null;
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setString(1, username);
		ps.setInt(2, officeId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			info = new Info_User();
			info.username = rs.getString(1);
			info.name = rs.getString(2);
			info.lastname = rs.getString(3);
			info.mobileno = rs.getString(4);
			info.cityId = rs.getInt(5);
			info.pic = null;

			Blob blob = rs.getBlob(6);
			if (blob != null) {
				int blobLength = (int) blob.length();
				byte[] blobAsBytes = blob.getBytes(1, blobLength);
				blob.free();
				info.pic = Helper.getString(blobAsBytes);
			}
			info.city = rs.getString(7);
		}
		return info;
	}

	public Vector<PhotoDesc> getAllPicIdDesc(int officeId) throws SQLException {
		String query = "select id, description, date from gallery where officeid = ? ";
		Vector<PhotoDesc> res = new Vector<PhotoDesc>();
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			PhotoDesc p = new PhotoDesc();
			p.id = rs.getInt(1);
			p.description = rs.getString(2);
			p.date = rs.getString(3);
			p.photo = null;
			res.add(p);
		}

		return res;
	}

	public boolean checkMasterPassword(String username, String password) throws SQLException {
		String dbuser, dbpass;
		String query = "select username, password from master ";

		PreparedStatement ps = connection.prepareStatement(query);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			dbuser = rs.getString(1);
			dbpass = rs.getString(2);
			return (dbuser.equals(username) && dbpass.equals(password));
		} else {
			return false;
		}
	}

	public void InsertInDoctorOffice(int officeId, int doctorId) throws SQLException {
		String query = "insert into doctoroffice(officeid, doctorid) values(?, ?) ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ps.setInt(2, doctorId);
		ps.executeUpdate();
	}

	public int getOfficeIdByTurnId(int turnId) throws SQLException {
		int officeId = -1;
		String query = "select officeid from turn where id = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, turnId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			officeId = rs.getInt(1);
		}
		return officeId;
	}

	public Vector<Ticket> getUserTicket(int userId) throws SQLException {
		Vector<Ticket> vec = new Vector<Ticket>();
		String query = "select ticket.id, userId, ticket.subjectId, topic, priority, "
				+ "startDate, endDate, ticketsubject.subject from ticket join ticketsubject on ticket.subjectId = ticketsubject.id where userId = ?"
				+ " order by endDate desc ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Ticket temp = new Ticket();
			temp.id = rs.getInt(1);
			temp.userId = rs.getInt(2);
			temp.subjectId = rs.getInt(3);
			temp.topic = rs.getString(4);
			temp.priority = rs.getInt(5);
			temp.startDate = rs.getString(6);
			temp.endDate = rs.getString(7);
			temp.subject = rs.getString(8);

			vec.add(temp);
		}
		return vec;
	}

	public int setUserTicket(int userId, int subjectId, String topic, int priority) throws SQLException {

		int id = getMaxId("ticket") + 1;

		String startDate = Helper.getTodayShortDate() + " " + Helper.getCurrentTime();
		String endDate = Helper.getTodayShortDate() + " " + Helper.getCurrentTime();

		String query = "insert into ticket (id, userId, subjectId, topic, priority, startDate, endDate) values (?,?,?,?,?,?,?) ";

		if (!openConnection()) {
			id = 0;
		} else {
			try {
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setInt(1, id);
				ps.setInt(2, userId);
				ps.setInt(3, subjectId);
				ps.setString(4, topic);
				ps.setInt(5, priority);
				ps.setString(6, startDate);
				ps.setString(7, endDate);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		closeConnection();
		return id;
	}

	public Vector<TicketMessage> getUserTicketMessage(int ticketId) throws SQLException {
		Vector<TicketMessage> vec = new Vector<TicketMessage>();
		String query = "select ticketmessage.id, userId, message, dateMessage, ticketId, username, name, lastname "
				+ " from ticketmessage join user on user.id=userId where ticketId = ?";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, ticketId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			TicketMessage temp = new TicketMessage();
			temp.id = rs.getInt(1);
			temp.userId = rs.getInt(2);
			temp.message = rs.getString(3);
			temp.dateMessage = rs.getString(4);
			temp.ticketId = rs.getInt(5);
			temp.username = rs.getString(6);
			temp.firstName = rs.getString(7);
			temp.lastName = rs.getString(8);

			vec.add(temp);
		}
		System.out.println(vec.size());
		return vec;
	}

	public String setUserTicketMessage(int userId, int ticketId, String message) throws SQLException {

		String msg = "ok";
		String date = Helper.getTodayShortDate() + " " + Helper.getCurrentTime();
		String query = "insert into ticketmessage (id, userId, message, dateMessage, ticketId) values (?,?,?,?,?) ";
		String query2 = "update ticket set endDate=? where id=?";

		if (!openConnection()) {
			msg = "\u0645\u0634\u06a9\u0644\u06cc \u062f\u0631 \u067e\u0627\u06cc\u06af\u0627\u0647 "
					+ "\u062f\u0627\u062f\u0647 \u0633\u0645\u062a \u0633\u0631\u0648\u0631 "
					+ "\u0628\u0647 \u0648\u062c\u0648\u062f \u0622\u0645\u062f\u0647 " + "\u0627\u0633\u062a";
		} else {
			int id = getMaxId("ticketmessage") + 1;
			try {
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setInt(1, id);
				ps.setInt(2, userId);
				ps.setString(3, message);
				ps.setString(4, date);
				ps.setInt(5, ticketId);
				ps.executeUpdate();
				PreparedStatement ps2 = connection.prepareStatement(query2);
				ps2.setString(1, date);
				ps2.setInt(2, ticketId);
				ps2.executeUpdate();
				ps.close();
				ps2.close();
			} catch (SQLException e) {
				e.printStackTrace();
				msg = e.getMessage();
			} catch (Exception e) {
				e.printStackTrace();
				e.printStackTrace();
			}
		}
		closeConnection();
		return msg;
	}

	public Vector<TicketSubject> getUserTicketSubject() throws SQLException {
		Vector<TicketSubject> vec = new Vector<TicketSubject>();
		String query = "select id, subject from ticketsubject";

		PreparedStatement ps = connection.prepareStatement(query);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			TicketSubject temp = new TicketSubject();
			temp.id = rs.getInt(1);
			temp.subject = rs.getString(2);
			vec.add(temp);
		}
		System.out.println(vec.size());
		return vec;
	}

	public Vector<Ticket> getAllUserTicket() throws SQLException {
		Vector<Ticket> vec = new Vector<Ticket>();
		String query = "select ticket.id, userId, ticket.subjectId, topic, priority, "
				+ "startDate, endDate, ticketsubject.subject from ticket join ticketsubject on ticket.subjectId = ticketsubject.id"
				+ " order by endDate desc ";
		PreparedStatement ps = connection.prepareStatement(query);
		// ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Ticket temp = new Ticket();
			temp.id = rs.getInt(1);
			temp.userId = rs.getInt(2);
			temp.subjectId = rs.getInt(3);
			temp.topic = rs.getString(4);
			temp.priority = rs.getInt(5);
			temp.startDate = rs.getString(6);
			temp.endDate = rs.getString(7);
			temp.subject = rs.getString(8);

			vec.add(temp);
		}
		return vec;
	}

	public int setQuestion(String label, int replyType, int officeId) throws SQLException {

		int id = getMaxId("question") + 1;
		String query = "insert into question (id, label, replyType, officeId) values (?,?,?,?) ";

		try {
			PreparedStatement ps = connection.prepareStatement(query);
			ps.setInt(1, id);
			ps.setString(2, label);
			ps.setInt(3, replyType);
			ps.setInt(4, officeId);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			id = 0;
		} catch (Exception e) {
			id = 0;
		}

		return id;
	}

	public String setReply(int userId, int questionId, String reply) throws SQLException {

		int id = getMaxId("reply") + 1;
		String msg = "ok";
		String query = "insert into reply (id, userId, questionId, reply) values (?,?,?,?) ";

		if (!openConnection()) {
			msg = "\u0645\u0634\u06a9\u0644\u06cc \u062f\u0631 \u067e\u0627\u06cc\u06af\u0627\u0647 "
					+ "\u062f\u0627\u062f\u0647 \u0633\u0645\u062a \u0633\u0631\u0648\u0631 "
					+ "\u0628\u0647 \u0648\u062c\u0648\u062f \u0622\u0645\u062f\u0647 " + "\u0627\u0633\u062a";
		} else {
			try {
				PreparedStatement ps = connection.prepareStatement(query);
				ps.setInt(1, id);
				ps.setInt(2, userId);
				ps.setInt(3, questionId);
				ps.setString(4, reply);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
				msg = e.getMessage();
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
		}
		closeConnection();
		return msg;
	}
	public Vector<Question> getQuestion(int officeId) throws SQLException {
		Vector<Question> vec = new Vector<Question>();
		String query = "select id, label, replyType, officeId from question where officeId = ? ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, officeId);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Question temp = new Question();
			temp.id = rs.getInt(1);
			temp.label = rs.getString(2);
			temp.replyType = rs.getInt(3);
			temp.officeId = rs.getInt(4);
			
			vec.add(temp);
		}
		return vec;
	}
	public Vector<Reply> getReply(int userId) throws SQLException {
		Vector<Reply> vec = new Vector<Reply>();
		String query = " ";
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, userId);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Reply temp = new Reply();
			temp.id = rs.getInt(1);
			temp.userId = rs.getInt(2);
			temp.questionId = rs.getInt(3);
			temp.reply = rs.getString(4);

			vec.add(temp);
		}
		return vec;
	}
	
	public void deleteFromQuestion(int questionId, int officeId) throws SQLException{
		String query = "delete from question where id = ? and officeid = ? ";
		
		PreparedStatement ps = connection.prepareStatement(query);
		ps.setInt(1, questionId);
		ps.setInt(2, officeId);
		
		ps.executeUpdate();
	}
}
