package primitives;

public class User {
	public String name;
	public String lastname;
	public String mobileno;
	public String username;
//	public String password;
	public int role;
	public int cityid;
	public String city;
	public int provinceid;
	public String province;
	public String pic;
	public String email;
	
	public static User getErrorUser(){
		User u = new User();
		u.name = "خطا";
		u.lastname = "Error";
		u.username = "";
		u.pic = null;
		u.role = 0;
		
		return u;
	}
	
	public Info_User getInfoUser(){
		Info_User info = new Info_User();
		info.city = city;
		info.cityId = cityid;
		info.lastname = lastname;
		info.mobileno = mobileno;
		info.name = name;
		info.pic = pic;
		info.username = username;
		return info;
	}
}
