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
		u.username = "خطا";
		u.pic = null;
		
		return u;
	}
}
